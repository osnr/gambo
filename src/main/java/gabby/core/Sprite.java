package com.gabby.core;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
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
    private ByteBuffer data;
	
    static private Color c;
	
    static {
        c = new Color(200, 200, 200);
    }
	
    public static ArrayList<Sprite> getAllSprites(ByteBuffer mem) {
        ArrayList<Sprite> a = new ArrayList<Sprite>();
		
        for (int i = 0; i < 40; i++) {
            a.add(new Sprite(mem, i));
        }
		
        return a;
    }
	
    public Sprite(ByteBuffer mem, int spriteNum) {
        y = BitTwiddles.unsign(mem.get(Vram.OAM + spriteNum * 4));
        x = BitTwiddles.unsign(mem.get(Vram.OAM + spriteNum * 4 + 1));
        tileNum = BitTwiddles.unsign(mem.get(Vram.OAM + spriteNum + 2));
        byte flags = mem.get(Vram.OAM + spriteNum + 3);
        data = ByteBuffer.allocate(16);
        data.put(mem.array(), Vram.TILE_TABLE_ONE, 16);
		
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
                        byte color = BitTwiddles.getColorFromBytePair(j, data.get(i), data.get(i + 1));
						
                        if (color > 0) {
                            g.setPaint(c);
                            g.drawLine(x + j, y + i, x + j, y + i);
                        }
                    }
                } else {
                    for (int j = 7; j >= 0; j--) {
                        byte color = BitTwiddles.getColorFromBytePair(j, data.get(i), data.get(i + 1));
						
                        if (color > 0) {
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
                        byte color = BitTwiddles.getColorFromBytePair(j, data.get(i), data.get(i + 1));
						
                        if (color > 0) {
                            g.setPaint(c);
                            g.drawLine(x + j, y + i, x + j, y + i);
                        }
                    }
                } else {
                    for (int j = 7; j >= 0; j--) {
                        byte color = BitTwiddles.getColorFromBytePair(j, data.get(i), data.get(i + 1));
						
                        if (color > 0) {
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

    public ByteBuffer getData() {
        return data;
    }
}
