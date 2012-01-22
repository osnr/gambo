/*
    Copyright (c) 2012 by Vincent Pacelli and Omar Rizwan

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

import java.io.BufferedReader;
import java.nio.ByteBuffer;
//import java.nio.
import java.util.ArrayList;

import java.awt.Graphics2D;
import java.awt.Color;

public class Sprite {
    private int x;
    private int y;
    private int tileNum;
    private boolean aboveBackground;
    private boolean xFlip;
    private boolean yFlip;
    private int paletteNum;
    private byte[] data;

    /**
     * @param mem The memory to parse for the sprites
     * @return An ArrayList of all sprites
     */
    // This looks slow
    public static ArrayList<Sprite> getAllSprites(ByteBuffer mem) {
        ArrayList<Sprite> a = new ArrayList<Sprite>();
		
        for (int i = 0; i < 40; i++) {
            a.add(new Sprite(mem, i));
        }
		
        return a;
    }

    /**
     * @param ram The ram from which the sprites are parsed
     * @return An ArrayList of all sprites
     */

    public static ArrayList<Sprite> getAllSprites(Ram ram) {
        return getAllSprites(ram.getMemory());
    }

    public static void drawAllSprites(ByteBuffer mem, Graphics2D g) {
		
        for (int i = 0; i < 40; i++) {
            (new Sprite(mem, i)).draw(g);
        }
    }
	
    public Sprite(ByteBuffer mem, int spriteNum) {
        y = BitTwiddles.unsign(mem.get(Ram.OAM + spriteNum * 4));
        x = BitTwiddles.unsign(mem.get(Ram.OAM + spriteNum * 4 + 1));
        tileNum = BitTwiddles.unsign(mem.get(Ram.OAM + spriteNum + 2));
        byte flags = mem.get(Ram.OAM + spriteNum + 3);
        
        data = new byte[16];
        mem.position(Ram.TILE_TABLE_ONE);
        mem.get(data);
		
        aboveBackground = (BitTwiddles.getBit(7, flags) == 0);
        yFlip = (BitTwiddles.getBit(6, flags) == 1);
        xFlip = (BitTwiddles.getBit(5, flags) == 1);
        paletteNum = BitTwiddles.getBit(4, flags);
    }
    
    public void draw(Graphics2D g) {
        if (!yFlip) {
            for (int i = 0; i < 8; i++) {
                if (!xFlip) {
                    for (int j = 0; j < 8; j++) {
                        Color c = BitTwiddles.getColorFromBytePair(j, data[i], data[i + 1]);
						
                        if (c != Color.WHITE) { //not necessary, but i think this might be faster?
                            g.setPaint(c);
                            g.drawLine(x + j, y + i, x + j, y + i);
                        }
                    }
                } else {
                    for (int j = 7; j >= 0; j--) {
                        Color c = BitTwiddles.getColorFromBytePair(j, data[i], data[i + 1]);
						
                        if (c != Color.WHITE) { 
                            g.setPaint(c);
                            g.drawLine(x + j, y + i, x + j, y + i);
                        }
                    }
                }
            }
        } else {
            for (int i = 7; i >= 0; i++) {
                if (!xFlip) {
                    for (int j = 0; j < 8; j++) {
                        Color c = BitTwiddles.getColorFromBytePair(j, data[i], data[i + 1]);
						
                        if (c != Color.WHITE) {
                            g.setPaint(c);
                            g.drawLine(x + j, y + i, x + j, y + i);
                        }
                    }
                } else {
                    for (int j = 7; j >= 0; j--) {
                        Color c = BitTwiddles.getColorFromBytePair(j, data[i], data[i + 1]);
						
                        if (c != Color.WHITE) {
                            g.setPaint(c);
                            g.drawLine(x + j, y + i, x + j, y + i);
                        }
                    }
                }
            }
        }
    }
	
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTileNum() {
        return tileNum;
    }

    public boolean isxFlip() {
        return xFlip;
    }

    public boolean isyFlip() {
        return yFlip;
    }

    public int getPaletteNum() {
        return paletteNum;
    }

    public byte[] getData() {
        return data;
    }
}
