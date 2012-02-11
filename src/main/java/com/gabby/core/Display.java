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

package com.gabby.core;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

class Display {
    public static final int TILE_WIDTH = 8;
    public static final int TILE_HEIGHT = 8;
    public static final int SCREEN_WIDTH = 256;
    public static final int SCREEN_HEIGHT = 256;

    public static final int HBLANK_MODE = 0;
    public static final int VBLANK_MODE = 1;
    public static final int OAM_READ_MODE = 2;
    public static final int VRAM_READ_MODE = 3;

    protected int modeClock, vblankClock;
    protected int mode;
    protected int line, lastLine;
    protected Ram ram;
    BufferedImage buffer;
    Emulator emulator;

    public Display(Ram ram, Emulator emulator) {
        modeClock = vblankClock = mode = line = lastLine = 0;
        this.ram = ram;
        buffer = new BufferedImage(160, 144, BufferedImage.TYPE_INT_ARGB);
        this.emulator = emulator;
        this.emulator.buffer = buffer;
    }

    public void scanline(int line) {
        int bgmap = (BitTwiddles.getBit(3, ram.read(Ram.LCDC)) == 0) ? Ram.TILE_MAP_ONE : Ram.TILE_MAP_TWO;
        int lineOffset = bgmap + (((line + ram.read(Ram.SCY)) & 0xFF) / 8);
        int firstTileOffset = ram.read(Ram.SCX) / 8;
        int y = (line + ram.read(Ram.SCY)) & 0x7;
        int x = ram.read(Ram.SCX);
        int tile = ram.read(lineOffset + firstTileOffset);
        Color c = null;
        Graphics2D g = buffer.createGraphics();

        if (BitTwiddles.getBit(3, ram.read(Ram.LCDC)) == 1 && tile < 128) {
            tile += 256;
        }

        for (int i = 0; i < 160; i++) {
            c = BitTwiddles.getColorFromBytePair(x, ram.read(0x8000 + tile), ram.read(0x8000 + tile + 1));
            g.setPaint(c);
            g.drawLine(x, x, y, y);
            x++;

            if (x == 8) {
                x = 0;
                lineOffset = (lineOffset + 1) & 31;
                tile = ram.read(lineOffset + firstTileOffset);

                if (BitTwiddles.getBit(3, ram.read(Ram.LCDC)) == 1 && tile < 128) {
                    tile += 256;
                }
            }
        }
        
        g.dispose();
    }

    public void step(int deltaClock) {
        int b = 0;
        vblankClock += deltaClock;

        if (vblankClock >= 70224) {
            line = 0;
            vblankClock -= 70224;
            modeClock = vblankClock;
        }

        line = vblankClock / 456;
        ram.write(Ram.LY, line);

        int lcdc = ram.read(Ram.LCDC);

        if ((lcdc & BitTwiddles.bx10000000) == 1) {
            if (ram.read(Ram.LY) == ram.read((Ram.LYC))) {
                if ((ram.read(Ram.STAT) & BitTwiddles.bx00000100) == 0) {
                    b = ram.read(Ram.STAT);
                    b |= BitTwiddles.bx00000100;
                    ram.write(Ram.STAT, b);
                    if ((ram.read(Ram.STAT) & BitTwiddles.bx01000000) != 0) {
                        ram.read(Ram.IF);
                        b |= BitTwiddles.bx00000010;
                        ram.write(Ram.IF, b);
                    }
                }
            } else {
                b = ram.read(Ram.STAT);
                b &= BitTwiddles.bx11111011;
            }
        }

        if (vblankClock >= 65664) { // VBLANK
            if ((lcdc & BitTwiddles.bx00000011) != BitTwiddles.bx00000001) {
                b = ram.read(Ram.LCDC);
                b &= BitTwiddles.bx11111100;
                b |= BitTwiddles.bx00000001;
                ram.write(Ram.LCDC, b);

                b = ram.read(Ram.STAT);
                b |= BitTwiddles.bx00000001;
                ram.write(Ram.STAT, b);

                if ((ram.read(Ram.LCDC) & BitTwiddles.bx10000000) == 0) {
                    // TODO: TURN OFF LCD
                }

                // TODO: Render to screen
            }
        } else {
            modeClock += deltaClock;

            if (modeClock >= 456)
                modeClock -= 456;
            if (modeClock  <= 80) {
                b = ram.read(Ram.LCDC);

                if ((b & BitTwiddles.bx00000011) != 2) {
                    b &= BitTwiddles.bx11111100;
                    b |= BitTwiddles.bx00000010;
                    ram.write(Ram.LCDC, b);

                    if ((ram.read(Ram.STAT) & BitTwiddles.bx00100000) != 0) {
                        b = ram.read(Ram.IF);
                        b |= BitTwiddles.bx00000010;
                        ram.write(Ram.IF, b);
                    }
                }
            } else if (modeClock <= (172 + 80)) {
                b = ram.read(Ram.LCDC);
                
                if ((b & BitTwiddles.bx00000011) != 3) {
                    b &= BitTwiddles.bx11111100;
                    b |= 3;
                    ram.write(Ram.LCDC, b);
                }
            } else {
                b = ram.read(Ram.LCDC);
                
                if ((b & BitTwiddles.bx00000011) != 0) {
                    b = ram.read(Ram.LCDC);

                    if ((b & BitTwiddles.bx10000000) > 0) {
                        if (lastLine != line) {
                            lastLine = line;
                            // TODO: Draw
                        }
                    }

                    b &= BitTwiddles.bx11111100;
                    b |= 0;
                    ram.write(Ram.LCDC, b);

                    if ((ram.read(Ram.STAT) & BitTwiddles.bx00001000) != 0) {
                        b = ram.read(Ram.IF);
                        b |= BitTwiddles.bx00000010;
                        ram.write(Ram.IF, b);
                    }
                }
            }
        }
        System.out.println(String.format("clkCounterMode: %d, vBlank: %d, LY: %d", modeClock, vblankClock, ram.read(Ram.LY)));
    }
}
