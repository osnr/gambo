package com.gabby.core;

import java.awt.Graphics2D;
import java.awt.Color;
import java.nio.ByteBuffer;

class Display {
    public static final int TILE_WIDTH = 8;
    public static final int TILE_HEIGHT = 8;
    public static final int SCREEN_WIDTH = 256;
    public static final int SCREEN_HEIGHT = 256;

    
    protected Graphics2D g;

    public Display(Graphics2D g) {
        this.g = g;
    }

    protected void drawTile(Graphics2D g, Ram ram, int spriteNumber, int table, int x, int y) {
        ByteBuffer spriteData = ByteBuffer.allocate(16);
        spriteData.put(ram.getMemory().array(), table + spriteNumber * 16, 16);
        Color c = new Color(255, 0, 255, 255);
		
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                Color color = BitTwiddles.getColorFromBytePair(j, spriteData.get(i), spriteData.get(i + 1));
                g.setPaint(color);
                g.drawLine(x + j, y + i, x + j, y + i);
            }
        }
    }

    protected void drawBackground(Graphics2D g, Ram ram) {
        if (BitTwiddles.getBit(0, ram.getMemory().get(Ram.LCDC)) == 1) {
            if (BitTwiddles.getBit(3, ram.getMemory().get(Ram.LCDC)) == 0) {
                for (int i = 0; i < 32 * 32; i++) {
                    int patternNumber = ram.getMemory().get(Ram.BACKGROUND_ONE + i);
                    int scx = ram.getMemory().get(Ram.SCX);
                    int scy = ram.getMemory().get(Ram.SCY);
					
                    if (scx < 0)
                        scx += 256;
                    else if (scx > 256)
                        scx -= 256;
                    if (scy < 0)
                        scy += 256;
                    else if (scy > 256)
                        scy -= 256;
                    
                    if (patternNumber < 0)
                        patternNumber += 256;
                    if (BitTwiddles.getBit(4, ram.getMemory().get(Ram.LCDC)) == 1)
                        drawTile(g, ram, patternNumber, Ram.TILE_TABLE_ONE, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
                    else
                        drawTile(g, ram, patternNumber, Ram.TILE_TABLE_TWO, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
                }
            } else {
                for (int i = 0; i < 32 * 32; i++) {
                    int patternNumber = ram.getMemory().get(Ram.BACKGROUND_TWO + i);
                    int scx = ram.getMemory().get(Ram.SCX);
                    int scy = ram.getMemory().get(Ram.SCY);
					
                    if (scx < 0)
                        scx += 256;
                    else if (scx > 256)
                        scx -= 256;
                    if (scy < 0)
                        scy += 256;
                    else if (scy > 256)
                        scy -= 256;
                    
                    if (patternNumber < 0)
                        patternNumber += 128; // since the id's are signed
                    if (BitTwiddles.getBit(4, ram.getMemory().get(Ram.LCDC)) == 1)
                        drawTile(g, ram, patternNumber, Ram.TILE_TABLE_ONE, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
                    else
                        drawTile(g, ram, patternNumber, Ram.TILE_TABLE_TWO, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
                }
            }
        }
    }
    
    protected void drawWindow(Graphics2D g, Ram ram) {
        if (BitTwiddles.getBit(5, ram.getMemory().get(Ram.LCDC)) == 1) {
            if (BitTwiddles.getBit(6, ram.getMemory().get(Ram.LCDC)) == 0) {
                for (int i = 0; i < 32 * 32; i++) {
                    int patternNumber = ram.getMemory().get(Ram.BACKGROUND_ONE + i);
                    int wx = ram.getMemory().get(Ram.WX);
                    int wy = ram.getMemory().get(Ram.WY);
					
                    if (wx < 0)
                        wx += 256;
                    if (wy < 0)
                        wy += 256;
                    if (patternNumber < 0)
                        patternNumber += 256;
                    if (BitTwiddles.getBit(4, ram.getMemory().get(Ram.LCDC)) == 1)
                        drawTile(g, ram, patternNumber, Ram.TILE_TABLE_ONE, (i % 32) + wx, (int) Math.floor(i / 32) + wy);
                    else
                        drawTile(g, ram, patternNumber, Ram.TILE_TABLE_TWO, (i % 32) + wx, (int) Math.floor(i / 32) + wy);
                }
            } else {
                for (int i = 0; i < 32 * 32; i++) {
                    int patternNumber = ram.getMemory().get(Ram.BACKGROUND_TWO + i);
                    int scx = ram.getMemory().get(Ram.SCX);
                    int scy = ram.getMemory().get(Ram.SCY);
					
                    if (scx < 0)
                        scx += 256;
                    if (scy < 0)
                        scy += 256;
                    if (patternNumber < 0)
                        patternNumber += 128; // since the id's are signed
                    if (BitTwiddles.getBit(4, ram.getMemory().get(Ram.LCDC)) == 1)
                        drawTile(g, ram, patternNumber, Ram.TILE_TABLE_ONE, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
                    else
                        drawTile(g, ram, patternNumber, Ram.TILE_TABLE_TWO, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
                }
            }
        }	
    }

    public void draw(Ram ram) {
        if (BitTwiddles.getBit(7, ram.getMemory().get(Ram.LCDC)) != 0) {
            drawBackground(g, ram);
            drawWindow(g, ram);

            Sprite.drawAllSprites(ram.getMemory(), g);
        }
    }
}
