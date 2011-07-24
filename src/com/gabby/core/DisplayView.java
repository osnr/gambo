package com.gabby.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.AttributedString;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class DisplayView extends SurfaceView implements SurfaceHolder.Callback {

	public static final int BACKGROUND_WIDTH = 256;
	public static final int BACKGROUND_HEIGHT = 256;
	public static final int WINDOW_WIDTH = 160;
	public static final int WINDOW_HEIGHT = 144;
	
	public static final int OAM = 0xFE00;
	public static final int LCDC = 0xFF40;
	public static final int SCX = 0xFF43;
	public static final int SCY = 0xff42;
	public static final int WX = 0xFF4B;
	public static final int WY = 0xFF4A;
	
	public static final int TILE_TABLE_ONE = 0x8000;
	public static final int TILE_TABLE_TWO = 0x8800;
	public static final int SPRITE_TABLE = TILE_TABLE_ONE;
	public static final int BACKGROUND_ONE = 0x9800;
	public static final int BACKGROUND_TWO = 0x9C00;
	
	private Bitmap background;
	private Bitmap window;
	private SurfaceHolder surfaceHolder;
	
	private boolean running = false;
	
	ByteBuffer memory;
	
	/** 
	 * @param i The distance from the significant bit to the target bit (from 0 to 7).
	 * @param second The first byte.
	 * @param first The second byte.
	 * @return The color code for the pixel. If i is not within the specified range, then -1 is returned.
	 */
	public static byte getColorFromBytePair(int i, byte first, byte second) {
		switch (i) {
		case 0:
			return (byte) (((second >> 6) & 0x2) | ((first >> 7) & 0x1));
		case 1:
			return (byte) ((((second >> 5) & 0x2) | (first >> 6) & 0x1));
		case 2:
			return (byte) ((((second >> 4) & 0x2) | (first >> 5) & 0x1));
		case 3:
			return (byte) ((((second >> 3) & 0x2) | (first >> 4) & 0x1));
		case 4:
			return (byte) ((((second >> 2) & 0x2) | (first >> 3) & 0x1));
		case 5:
			return (byte) ((((second >> 1) & 0x2) | (first >> 2) & 0x1));
		case 6:
			return (byte) ((((second & 0x2) | ((first >> 1) & 0x1))));
		case 7:
			return (byte) (((second << 1) & 0x2) | (first & 0x1));
		}
		
		return -1;
	}
	
	private static int getBit(int i, byte b) {
		switch (i) {
		case 0:
			return (b >> 7) & 0x1;
		case 1:
			return (b >> 6) & 0x1;
		case 2:
			return (b >> 5) & 0x1;
		case 3:
			return (b >> 4) & 0x1;
		case 4:
			return (b >> 3) & 0x1;
		case 5:
			return (b >> 2) & 0x1;
		case 6:
			return (b >> 1) & 0x1;
		case 7:
			return b & 0x1;
		}
		
		return -1;
	}
	
	protected void drawTile(Canvas c, int spriteNumber, int table, int x, int y) {
		ByteBuffer spriteData = ByteBuffer.allocate(16);
		spriteData.put(memory.array(), table + spriteNumber * 16, 16);
		Paint p = new Paint();
		p.setARGB(255, 0, 255, 255);
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				byte color = getColorFromBytePair(j, spriteData.get(i), spriteData.get(i + 1));
				if (color > 0)
					c.drawPoint(x + j, y + i, p);
			}
		}
	}
	
	protected void drawSprite(Canvas c, int spriteNumber) {
		int spriteAttrib = OAM + spriteNumber * 4;
		
		int y = memory.get(spriteAttrib);
		int x = memory.get(spriteAttrib + 1);
		int patternNum = memory.get(spriteAttrib + 2);
		byte flags = memory.get(spriteAttrib + 3); // TODO: Implement flags
		
		drawTile(c, patternNum, SPRITE_TABLE, x, y);
	}
	
	protected void drawBackground(Canvas c) {
		if (getBit(0, memory.get(LCDC)) == 1) {
			if (getBit(3, memory.get(LCDC)) == 0) {
				for (int i = 0; i < 32 * 32; i++) {
					int patternNumber = memory.get(BACKGROUND_ONE + i);
					int scx = memory.get(SCX);
					int scy = memory.get(SCY);
					
					if (scx < 0)
						scx += 256;
					if (scy < 0)
						scy += 256;
					if (patternNumber < 0)
						patternNumber += 256;
					if (getBit(4, memory.get(LCDC)) == 1)
						drawTile(c, patternNumber, TILE_TABLE_ONE, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
					else
						drawTile(c, patternNumber, TILE_TABLE_TWO, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
				}
			} else {
				for (int i = 0; i < 32 * 32; i++) {
					int patternNumber = memory.get(BACKGROUND_TWO + i);
					int scx = memory.get(SCX);
					int scy = memory.get(SCY);
					
					if (scx < 0)
						scx += 256;
					if (scy < 0)
						scy += 256;
					if (patternNumber < 0)
						patternNumber += 128; // since the id's are signed
					if (getBit(4, memory.get(LCDC)) == 1)
						drawTile(c, patternNumber, TILE_TABLE_ONE, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
					else
						drawTile(c, patternNumber, TILE_TABLE_TWO, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
				}
			}
		}
		
	}
	
	protected void drawWindow(Canvas c) {
		if (getBit(5, memory.get(LCDC)) == 1) {
			if (getBit(6, memory.get(LCDC)) == 0) {
				for (int i = 0; i < 32 * 32; i++) {
					int patternNumber = memory.get(BACKGROUND_ONE + i);
					int wx = memory.get(WX);
					int wy = memory.get(WY);
					
					if (wx < 0)
						wx += 256;
					if (wy < 0)
						wy += 256;
					if (patternNumber < 0)
						patternNumber += 256;
					if (getBit(4, memory.get(LCDC)) == 1)
						drawTile(c, patternNumber, TILE_TABLE_ONE, (i % 32) + wx, (int) Math.floor(i / 32) + wy);
					else
						drawTile(c, patternNumber, TILE_TABLE_TWO, (i % 32) + wx, (int) Math.floor(i / 32) + wy);
				}
			} else {
				for (int i = 0; i < 32 * 32; i++) {
					int patternNumber = memory.get(BACKGROUND_TWO + i);
					int scx = memory.get(SCX);
					int scy = memory.get(SCY);
					
					if (scx < 0)
						scx += 256;
					if (scy < 0)
						scy += 256;
					if (patternNumber < 0)
						patternNumber += 128; // since the id's are signed
					if (getBit(4, memory.get(LCDC)) == 1)
						drawTile(c, patternNumber, TILE_TABLE_ONE, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
					else
						drawTile(c, patternNumber, TILE_TABLE_TWO, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
				}
			}
		}
		
	}
	
	class DisplayThread extends Thread {
		
		@Override
		public void run() {
			running = true;
			while (running) {
				Canvas c = null;
				try {
					c = surfaceHolder.lockCanvas();
					c.drawColor(Color.WHITE);
					
					if (getBit(7, memory.get(LCDC)) != 0) {
						drawBackground(c);
						drawWindow(c);
						for (int i = 0; i < 40; i++) {
							drawSprite(c, i); // Draw all sprites always?!
						}
						
					}
					
				} finally {
					if (c != null)
						surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
	
	private DisplayThread thread;
	
	public ByteBuffer getMemory() {
		return memory;
	}
	
	public void setMemory(ByteBuffer memory) {
		this.memory = memory;
	}
	
	public DisplayView(Context context) {
		super(context);
		background = Bitmap.createBitmap(BACKGROUND_WIDTH, BACKGROUND_HEIGHT, Config.ARGB_8888);
		window = Bitmap.createBitmap(WINDOW_WIDTH, WINDOW_HEIGHT, Config.ARGB_8888);
		thread = new DisplayThread();
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
	}
	
	public DisplayView(Context context, AttributeSet attribs) {
		super(context, attribs);
		background = Bitmap.createBitmap(BACKGROUND_WIDTH, BACKGROUND_HEIGHT, Config.ARGB_8888);
		window = Bitmap.createBitmap(WINDOW_WIDTH, WINDOW_HEIGHT, Config.ARGB_8888);
		thread = new DisplayThread();
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				
			}
		}
	}

}
