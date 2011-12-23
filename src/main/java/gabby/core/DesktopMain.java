package com.gabby.core;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Color;
import javax.swing.SwingUtilities;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.nio.ByteBuffer;

class DesktopMain extends Canvas {

    protected Ram ram;
    
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g = (Graphics2D) graphics;
        
        if (BitTwiddles.getBit(7, ram.getMemory().get(Ram.LCDC)) != 0) {
            drawBackground(g);
            drawWindow(g);

            Sprite.drawAllSprites(ram.getMemory(), g);
        }
    }

    protected void drawTile(Graphics g, int spriteNumber, int table, int x, int y) {
        ByteBuffer spriteData = ByteBuffer.allocate(16);
        spriteData.put(ram.getMemory().array(), table + spriteNumber * 16, 16);
        Color c = new Color(255, 0, 255, 255);
		
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                byte color = BitTwiddles.getColorFromBytePair(j, spriteData.get(i), spriteData.get(i + 1));
                if (color > 0)
                    g.drawLine(x + j, y + i, x + j, y + i);
            }
        }
    }

    protected void drawBackground(Graphics2D g) {
        if (BitTwiddles.getBit(0, ram.getMemory().get(Ram.LCDC)) == 1) {
            if (BitTwiddles.getBit(3, ram.getMemory().get(Ram.LCDC)) == 0) {
                for (int i = 0; i < 32 * 32; i++) {
                    int patternNumber = ram.getMemory().get(Ram.BACKGROUND_ONE + i);
                    int scx = ram.getMemory().get(Ram.SCX);
                    int scy = ram.getMemory().get(Ram.SCY);
					
                    if (scx < 0)
                        scx += 256;
                    if (scy < 0)
                        scy += 256;
                    if (patternNumber < 0)
                        patternNumber += 256;
                    if (BitTwiddles.getBit(4, ram.getMemory().get(Ram.LCDC)) == 1)
                        drawTile(g, patternNumber, Ram.TILE_TABLE_ONE, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
                    else
                        drawTile(g, patternNumber, Ram.TILE_TABLE_TWO, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
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
                        drawTile(g, patternNumber, Ram.TILE_TABLE_ONE, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
                    else
                        drawTile(g, patternNumber, Ram.TILE_TABLE_TWO, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
                }
            }
        }
    }
    protected void drawWindow(Graphics2D g) {
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
                        drawTile(g, patternNumber, Ram.TILE_TABLE_ONE, (i % 32) + wx, (int) Math.floor(i / 32) + wy);
                    else
                        drawTile(g, patternNumber, Ram.TILE_TABLE_TWO, (i % 32) + wx, (int) Math.floor(i / 32) + wy);
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
                        drawTile(g, patternNumber, Ram.TILE_TABLE_ONE, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
                    else
                        drawTile(g, patternNumber, Ram.TILE_TABLE_TWO, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
                }
            }
        }	
    }
    
    public static void main(String[] args) {
      JFrame frame = new JFrame("Gabby");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.add(new DesktopMain());
      frame.setSize(160, 144);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
      frame.setIgnoreRepaint(true);
    }
}
