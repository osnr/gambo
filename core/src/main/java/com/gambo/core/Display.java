/*  Copyright (c) 2012 by Vincent Pacelli and Omar Rizwan

    This file is part of Gabby.

    Gabby is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gabby is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Gabby.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gambo.core;

import java.util.Arrays;

import com.gambo.core.Mmu.Interrupts;

public abstract class Display {
    public static final int TILE_WIDTH = 8;
    public static final int TILE_HEIGHT = 8;
    public static final int SCREEN_WIDTH = 256;
    public static final int SCREEN_HEIGHT = 256;

    private int lineCounter = 456;
    private int mode;
    private int line;

    protected Mmu mmu;

    public Display(Mmu mmu) {
        mode = line = 0;

        this.mmu = mmu;
    }

    public int getLineCounter() {
        return lineCounter;
    }

    public void setLineCounter(int lineCounter) {
        this.lineCounter = lineCounter;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    protected abstract int getPixel(int x, int y);
    protected abstract void setPixel(int x, int y, int rgb);

    protected abstract void repaint();
    
    private int getColorFromPalette(int pal, int c) {
        int palb = mmu.read(pal);

        int colorCode = (palb >> (c * 2)) & 0x03;

        switch (colorCode) {
            case 0: // white
                return 0xFFFFFF;
            case 1: // light gray
                return 0xA9A9A9;
            case 2: // dark gray
                return 0x808080;
            case 3: // black
                return 0x000000;
            default: // WTF
                return 0xFF0000; // THINGS HAVE GONE HORRIBLY WRONG!
        }
    }

	private int tile(int x, int y) {
		int lcdc = mmu.read(Mmu.LCDC);

		int mapAddr;
		int dataAddr;

		if ((lcdc & 0x08) == 0) {
	        mapAddr = Mmu.TILE_MAP_ONE;
        } else {
	        mapAddr = Mmu.TILE_MAP_TWO;
        }

        mapAddr += (((y >> 3) << 5) + (x >> 3)) & 0x03FF;

		// if BG & window tile data is in table 2
		if ((lcdc & 0x10) == 0) {
			// pattern #0 should be at 0x9000, not 0x8800
			dataAddr = Mmu.TILE_TABLE_TWO + 0x800 + ((byte) mmu.read(mapAddr) << 4);
		} else {
			dataAddr = Mmu.TILE_TABLE_ONE + (mmu.read(mapAddr) << 4);
		}
		dataAddr += (y & 7) << 1;

		return (mmu.read(dataAddr + 0) << 0) | (mmu.read(dataAddr + 1) << 8);
	}

	private void drawBackground() {
		int iy = (mmu.read(Mmu.LY) + mmu.read(Mmu.SCY)) & 0xFF;
		int ix = mmu.read(Mmu.SCX);

		int tx = ix & 7;

		// get tile data for (ix, iy) from the selected tilemap (0 or 1) in LCDC
		int data = tile(ix, iy);

		for (int ox = 0; ox < 160; ox++) {
			int color = ( ((data & (0x0080 >> tx)) != 0) ? 1 : 0 )
				| ( ((data & (0x8000 >> tx)) != 0) ? 2 : 0 );

			this.setPixel(ox, line, getColorFromPalette(Mmu.BGP, color));

			ix = (ix + 1) & 0xFF;
			tx = (tx + 1) & 7;

			if (tx == 0) {
				data = tile(ix, iy);
			}
		}
	}

    private void drawWindow() {
        int lcdc = mmu.read(Mmu.LCDC);
        int wx = mmu.read(Mmu.WX), wy = mmu.read(Mmu.WY);
        int tileMap = 0;
        int tileData = 0;

        if ((wx <= 166) && (wy <= line) && (wx >= 7) && (line <= 143)) {
	        if ((lcdc & 0x40) == 0) {
		        tileMap = Mmu.TILE_MAP_ONE;
	        } else {
		        tileMap = Mmu.TILE_MAP_TWO;
	        }

	        if ((lcdc & 0x10) == 0) {
		        tileData = Mmu.TILE_TABLE_TWO;
	        } else {
		        tileData = Mmu.TILE_TABLE_ONE;
	        }

	        int row = line - wy;
	        int y = (row >> 3);
	        row &= 0x07;
	        int tileNum = y << 5;
	        int tile = 0;

	        for (int i = 0; i < 32; i++) {
		        if (tileData == Mmu.TILE_TABLE_ONE) {
			        tile = mmu.read(tileMap + tileNum);
		        } else {
			        tile = ((byte) mmu.read(tileMap + tileNum)) + 128;
		        }

		        int z = row << 1;
		        int tmpAddr = tileData + (tile << 4) + z;
		        z <<= 2;

		        int b1 = mmu.read(tmpAddr);
		        int b2 = mmu.read(tmpAddr + 1);

		        int[] tileBuff = new int[64];

		        tileBuff[z + 7] = ((b2 & 0x01) | ((b1 & 0x01) << 1));
		        tileBuff[z + 6] = (((b2 & 0x02) >> 1) | (((b1 & 0x02) >> 1) << 1));
		        tileBuff[z + 5] = (((b2 & 0x04) >> 2) | (((b1 & 0x04) >> 2) << 1));
		        tileBuff[z + 4] = (((b2 & 0x08) >> 3) | (((b1 & 0x08) >> 3) << 1));
		        tileBuff[z + 3] = (((b2 & 0x10) >> 4) | (((b1 & 0x10) >> 4) << 1));
		        tileBuff[z + 2] = (((b2 & 0x20) >> 5) | (((b1 & 0x20) >> 5) << 1));
		        tileBuff[z + 1] = (((b2 & 0x40) >> 6) | (((b1 & 0x40) >> 6) << 1));
		        tileBuff[z] = (((b2 & 0x80) >> 7) | (((b1 & 0x80) >> 7) << 1));

		        int x = (i << 3) + wx - 7;
		        z = ((line - wy) & 7) << 3;

		        for (int j = 0; j < 8; j++) {
			        if ((x >= 0) && (x < 160)) {
				        this.setPixel(x, line, getColorFromPalette(Mmu.BGP, tileBuff[z]));
			        }

			        x++;
			        z++;
		        }

		        tileNum++;
	        }
        }
    }

    private void drawSprites() {
        int numSpritesToDisplay = 0, height = 0;
        int[] spritesToDraw = new int[40];

        if ((mmu.read(Mmu.LCDC) & 0x02) > 0) {
            numSpritesToDisplay = 0;

            if ((mmu.read(Mmu.LCDC) & 0x04) == 0)
                height = 7; // 8x8
            else
                height = 15; // 8x16
        }

        for (int i = 0; i < 40; i++) {
            int y = mmu.read(Mmu.OAM + (i << 2)) - 16; // x coords are -16 for some reason
            int x = mmu.read(Mmu.OAM + (i << 2) + 1) - 8; // y coords are -8

            if ((x > -8) && (y >= (line - height)) && (x < 160) && (y <= line)) {
                spritesToDraw[numSpritesToDisplay] = ((x + 8) << 6) | i;
                numSpritesToDisplay++;
            }
        }

        Arrays.sort(spritesToDraw);

        int[] spriteBuff = new int[128];

        for (int i = 0; i < numSpritesToDisplay; i++) {
            // array is sorted in reverse..
            int sprite = spritesToDraw[spritesToDraw.length - (1 + i)] & 0x3F;

            int y = mmu.read(Mmu.OAM + (sprite << 2)) - 16;
            int x = mmu.read(Mmu.OAM + (sprite << 2) + 1) - 8;
            int pattern = mmu.read(Mmu.OAM + (sprite << 2) + 2);

            if (height == 15)
                pattern &= 0xFE;

            int flags = mmu.read(Mmu.OAM + (sprite << 2) + 3);

            int palette;
            if ((flags & 0x10) == 0) {
                palette = 1;
            } else {
                palette = 2;
            }

            for (int j = 0; j <= (height * 2) + 1; j += 2) {
                int b1 = mmu.read(Mmu.VRAM + (pattern << 4) + j);
                int b2 = mmu.read(Mmu.VRAM + (pattern << 4) + j + 1);
                //System.out.println(String.format("b1: %d, b2: %d", b1, b2));
                spriteBuff[(j << 2) + 7] = ((b2 & 0x01) | ((b1 & 0x01) << 1));
                spriteBuff[(j << 2) + 6] = (((b2 & 0x02) >> 1) | (((b1 & 0x02) >> 1) << 1));
                spriteBuff[(j << 2) + 5] = (((b2 & 0x04) >> 2) | (((b1 & 0x04) >> 2) << 1));
                spriteBuff[(j << 2) + 4] = (((b2 & 0x08) >> 3) | (((b1 & 0x08) >> 3) << 1));
                spriteBuff[(j << 2) + 3] = (((b2 & 0x10) >> 4) | (((b1 & 0x10) >> 4) << 1));
                spriteBuff[(j << 2) + 2] = (((b2 & 0x20) >> 5) | (((b1 & 0x20) >> 5) << 1));
                spriteBuff[(j << 2) + 1] = (((b2 & 0x40) >> 6) | (((b1 & 0x40) >> 6) << 1));
                spriteBuff[(j << 2)] = (((b2 & 0x80) >> 7) | (((b1 & 0x80) >> 7) << 1));
            }

            if ((flags & 0x20) != 0) {
                for (int j = 0; j <= height; j++) { // x-axis flip
                    int t = spriteBuff[(j << 3) + 0];
                    spriteBuff[(j << 3) + 0] = spriteBuff[(j << 3) + 7];
                    spriteBuff[(j << 3) + 7] = t;
                    t = spriteBuff[(j << 3) + 1];
                    spriteBuff[(j << 3) + 1] = spriteBuff[(j << 3) + 6];
                    spriteBuff[(j << 3) + 6] = t;
                    t = spriteBuff[(j << 3) + 2];
                    spriteBuff[(j << 3) + 2] = spriteBuff[(j << 3) + 5];
                    spriteBuff[(j << 3) + 5] = t;
                    t = spriteBuff[(j << 3) + 3];
                    spriteBuff[(j << 3) + 3] = spriteBuff[(j << 3) + 4];
                    spriteBuff[(j << 3) + 4] = t;
                }
            }

            if ((flags & 0x40) != 0) { // y-axis flip
                for (int j = 0; j < 8; j++) {
                    if (height == 7) {  /* Swap 8 pixels high sprites */
                        for (int h = 0; h < 25; h += 8) {
                            int t = spriteBuff[h + j];
                            spriteBuff[h + j] = spriteBuff[(56 - h) + j];
                            spriteBuff[(56 - h) + j] = t;
                        }
                    } else {   /* Swap 16 pixels high sprites */
                        for (int h = 0; h < 57; h += 8) {
                            int t = spriteBuff[h + j];
                            spriteBuff[h + j] = spriteBuff[(120 - h) + j];
                            spriteBuff[(120 - h) + j] = t;
                        }
                    }
                }
            }

            int bgc = getColorFromPalette(Mmu.BGP, 0);
            int z = line - y;

            for (int j = 0; j < 8; j++) {
                if (((x + j) >= 0) && ((y + z) >= 0) && ((x + j) < 160) && ((y + z) < 144)) {
                    if (spriteBuff[(z << 3) | j] > 0) { // if inside the screen and not transparant
                        if (((flags & 0x80) == 0) || (this.getPixel(x + j, y + z) == bgc)) {
                            if (palette == 1) {
                                this.setPixel(x + j, y + z, getColorFromPalette(Mmu.OBP0, spriteBuff[(z << 3) | j]));
                            } else {
                                this.setPixel(x + j, y + z, getColorFromPalette(Mmu.OBP1, spriteBuff[(z << 3) | j]));
                            }
                        }
                    }
                }
            }
        }
    }

	private void vblank() {
		int stat = mmu.read(Mmu.STAT);
		if ((stat & 0x03) != 0x01) {
	        stat &= 0xFB;
	        stat |= 0x01;
	        mmu.write(Mmu.STAT, stat);

			mmu.interrupts.setInterrupt(Interrupts.VBLANK);

			if ((mmu.read(Mmu.LCDC) & 0x80) == 0) {
				for (int i = 0; i < 160; i++) {
					for (int j = 0; j < 144; j++) {
						this.setPixel(0, 0, 0x000000);
					}
				}
			}

			repaint();
		}
	}

	private void draw() {
		int lcdc = mmu.read(Mmu.LCDC);

		if ((lcdc & 0x01) != 0) {
			drawBackground();
			if ((lcdc & 0x20) != 0) {
			 	drawWindow();
			}
		} else {
			// Screen is off, so draw black.
			for (int i = 0; i < 160; i++) {
                this.setPixel(i, line, 0x000000);
            }
		}

		if ((lcdc & 0x02) != 0) {
			drawSprites();
		}
	}

	// returns whether vblank was triggered
	private boolean nextLine() {
		// don't use write wrapper,
		// because a game write to LY always makes it 0
		mmu.getMemory()[Mmu.LY] = (byte) (mmu.read(Mmu.LY) + 1);
		line = mmu.read(Mmu.LY);

		lineCounter += 456;

		if (line == 144) {
			vblank();
			return true;

		} else if (line > 153) {
			mmu.getMemory()[Mmu.LY] = 0;
			line = 0;

		} else if (line < 144) {
			draw();
		}
		return false;
	}

	private void updateStatus() {
        int stat = mmu.read(Mmu.STAT);
        int lcdc = mmu.read(Mmu.LCDC);

		if ((lcdc & 0x80) == 0) { // if LCD control is set to off
	        // LCD is disabled
	        // reset vblank clock, scanline

			// TODO ambiguous: should the line / linecounter be reset?
			// lineCounter += 456;
	        // line = 0;
	        // mmu.write(Mmu.LY, 0);

	        // set mode to 1
	        stat &= 0xFB;
	        stat |= 0x01;
		}

		if (line >= 144) { // vblank (mode 1)
			// update stat register
			stat |= 0x01;
			stat &= 0xFD;

			// call the LCDC interrupt if it's flagged for mode 1
			if ((stat & 0x08) != 0 && mode != 1) {
				mode = 1;
				mmu.interrupts.setInterrupt(Interrupts.LCDC);
			}
		} else {
			if (lineCounter >= 376) { // mode 2
				stat |= 0x02;
				stat &= 0xFE;

				if ((stat & 0x10) != 0 && mode != 2) {
					mode = 2;
					mmu.interrupts.setInterrupt(Interrupts.LCDC);
				}
			} else if (lineCounter >= 204) { // mode 3
				mode = 3;

				stat |= 0x03;

			} else {
				mode = 0;

				stat &= 0xFC;

				if ((stat & 0x04) != 0 && mode != 0) {
					mode = 0;
					mmu.interrupts.setInterrupt(Interrupts.LCDC);
				}
			}
		}
		
		if (mmu.read(Mmu.LY) == mmu.read((Mmu.LYC))) {
			// is the coincidence flag not set already?
			if ((stat & 0x04) == 0) {
				// set the coincidence flag, beacause LY (current scanline) == LYC (game's favorite scanline)
				stat |= 0x04;

				// if STAT's coincidence interrupt flag is set,
				// then trigger an interrupt now
				if ((stat & 0x40) != 0) {
					mmu.interrupts.setInterrupt(Interrupts.LCDC);
				}
			}
		} else {
			// reset coincidence flag
			stat &= 0xFB;
		}

		mmu.write(Mmu.STAT, stat);
	}

	// executed on each operation
    // returns whether we're starting a new frame
    public boolean step(int deltaClock) {
	    boolean newFrame = false;
        int lcdc = mmu.read(Mmu.LCDC);

        if ((lcdc & 0x80) != 0) { // if LCD control is set to on
	        // TODO ambiguous: should we change lineCounter even if
	        // LCD control is off?

	        // scanline increments every 456 cycles
	        lineCounter -= deltaClock;
        }

        if (lineCounter <= 0) {
	        newFrame = nextLine();
        }

        updateStatus();

        return newFrame;
    }
    
    public void loadFromSaveState(SaveState s) {
        mode = s.mode;
        line = s.line;
        lineCounter = s.lineCounter;
    }
}