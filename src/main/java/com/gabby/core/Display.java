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

    protected int clock;
    protected int mode;
    protected int line;
    protected Ram ram;
    BufferedImage buffer;
    Emulator emulator;

    public Display(Ram ram, Emulator emulator) {
        clock = mode = line = 0;
        this.ram = ram;
        buffer = new BufferedImage(160, 144, BufferedImage.TYPE_INT_ARGB);
        this.emulator = emulator;
        this.emulator.buffer = buffer;
    }

    public void scanline(int line) {
        int bgmap = (BitTwiddles.getBit(3, ram.getMemory().get(Ram.LCDC)) == 0) ? Ram.TILE_MAP_ONE : Ram.TILE_MAP_TWO;
        int lineOffset = bgmap + (((line + ram.read(Ram.SCY)) & 0xFF) / 8);
        int firstTileOffset = ram.read(Ram.SCX) / 8;
        int y = (line + ram.read(Ram.SCY)) & 0x7;
        int x = ram.read(Ram.SCX);
        int tile = ram.read(lineOffset + firstTileOffset);
        Color c = null;
        Graphics2D g = buffer.createGraphics();

        if (BitTwiddles.getBit(3, ram.getMemory().get(Ram.LCDC)) == 1 && tile < 128) {
            tile += 256;
        }

        for (int i = 0; i < 160; i++) {
            c = BitTwiddles.getColorFromBytePair(x, ram.getMemory().get(0x8000 + tile), ram.getMemory().get(0x8000 + tile + 1));
            g.setPaint(c);
            g.drawLine(x, x, y, y);
            x++;

            if (x == 8) {
                x = 0;
                lineOffset = (lineOffset + 1) & 31;
                tile = ram.read(lineOffset + firstTileOffset);

                if (BitTwiddles.getBit(3, ram.getMemory().get(Ram.LCDC)) == 1 && tile < 128) {
                    tile += 256;
                }
            }
        }
        
        g.dispose();
    }

    public void step(int deltaClock) {
        clock += deltaClock;

        switch (mode) {
            case OAM_READ_MODE:
                if (clock > 79) {
                    clock = 0;
                    mode = VRAM_READ_MODE;
                }

                break;
            case VRAM_READ_MODE:
                if (clock > 171) {
                    clock = 0;
                    mode = HBLANK_MODE;

                    scanline(line);
                }

                break;
            case HBLANK_MODE:
                if (clock > 203) {
                    clock = 0;
                    line++;

                    if (line == 143) { // at right
                        mode = VBLANK_MODE;
                        emulator.bufferFromBuffer(buffer);
                        emulator.repaint();

                        /*Graphics2D g = (Graphics2D) buffer.getGraphics();
                        g.setBackground(new Color(255, 255, 255, 0));
                        g.clearRect(0,0, buffer.getWidth(), buffer.getHeight());*/
                    } else {
                        mode = OAM_READ_MODE;
                    }
                }

                break;
            case VBLANK_MODE:
                if (clock > 455) { // at bottom
                    clock = 0;
                    line++;

                    if (line > 153) {
                        mode = 2;
                        line = 0;
                    }
                }

                break;
            default:
                break;
        }
        
        ram.getMemory().put(Ram.LY, (byte) line);
    }
}
