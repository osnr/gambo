package com.gabby.core;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Sprite {
	private int x;
	private int y;
	private int tileNum;
	private boolean aboveBackground;
	private boolean xFlip;
	private boolean yFlip;
	private int paletteNum;
	private ByteBuffer data;
	
	static private Paint p;
	
	static {
		p = new Paint();
		p.setARGB(255, 200, 200, 200);
	}
	
	public ArrayList<Sprite> getAllSprites(ByteBuffer mem) {
		ArrayList<Sprite> a = new ArrayList<Sprite>();
		
		for (int i = 0; i < 40; i++) {
			a.add(new Sprite(mem, i));
		}
		
		return a;
	}
	
	private static int unsign(byte b) {
		if (b < 0)
			return b + 256;
		else
			return b;
	}
	
	public Sprite(ByteBuffer mem, int spriteNum) {
		y = unsign(mem.get(DisplayView.OAM + spriteNum * 4));
		x = unsign(mem.get(DisplayView.OAM + spriteNum * 4 + 1));
		tileNum = unsign(mem.get(DisplayView.OAM + spriteNum + 2));
		byte flags = mem.get(DisplayView.OAM + spriteNum + 3);
		data = ByteBuffer.allocate(16);
		data.put(mem.array(), DisplayView.TILE_TABLE_ONE, 16);
		
		aboveBackground = (DisplayView.getBit(7, flags) == 0);
		yFlip = (DisplayView.getBit(6, flags) == 1);
		xFlip = (DisplayView.getBit(5, flags) == 1);
		paletteNum = DisplayView.getBit(4, flags);
	}

	public void draw(Canvas c) {
		if (!yFlip) {
			for (int i = 0; i < 8; i++) {
				if (!xFlip) {
					for (int j = 0; j < 8; j++) {
						byte color = DisplayView.getColorFromBytePair(j, data.get(i), data.get(i + 1));
						
						if (color > 0)
							c.drawPoint(x + j, y + i, p);
					}
				} else {
					for (int j = 7; j >= 0; j--) {
						byte color = DisplayView.getColorFromBytePair(j, data.get(i), data.get(i + 1));
						
						if (color > 0)
							c.drawPoint(x + j, y + i, p);
					}
				}
			}
		} else {
			for (int i = 7; i >= 0; i++) {
				if (!xFlip) {
					for (int j = 0; j < 8; j++) {
						byte color = DisplayView.getColorFromBytePair(j, data.get(i), data.get(i + 1));
						
						if (color > 0)
							c.drawPoint(x + j, y + i, p);
					}
				} else {
					for (int j = 7; j >= 0; j--) {
						byte color = DisplayView.getColorFromBytePair(j, data.get(i), data.get(i + 1));
						
						if (color > 0)
							c.drawPoint(x + j, y + i, p);
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
