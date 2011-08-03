package com.gabby.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.AttributedString;
import java.util.ArrayList;

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
	
	protected void drawTile(Canvas c, int spriteNumber, int table, int x, int y) {
		ByteBuffer spriteData = ByteBuffer.allocate(16);
		spriteData.put(memory.array(), table + spriteNumber * 16, 16);
		Paint p = new Paint();
		p.setARGB(255, 0, 255, 255);
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				byte color = BitTwiddles.getColorFromBytePair(j, spriteData.get(i), spriteData.get(i + 1));
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
	
	protected ArrayList<Sprite> cullSprites(ArrayList<Sprite> sprites) {
		ArrayList<Sprite> culled = new ArrayList<Sprite>();
		int scx = BitTwiddles.unsign(memory.get(SCX));
		int scy = BitTwiddles.unsign(memory.get(SCY));
		
		for (Sprite s : sprites) {
		//	if (s.getX() - scx < 0 || s.getX() - scx > WINDOW_WIDTH)
		//		continue;
			culled.add(s);
			// TODO: Implement culling. I want to see the drawing in action without culling before I add it.
		}
		
		return culled;
	}
	
	protected void drawBackground(Canvas c) {
		if (BitTwiddles.getBit(0, memory.get(LCDC)) == 1) {
			if (BitTwiddles.getBit(3, memory.get(LCDC)) == 0) {
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
					if (BitTwiddles.getBit(4, memory.get(LCDC)) == 1)
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
					if (BitTwiddles.getBit(4, memory.get(LCDC)) == 1)
						drawTile(c, patternNumber, TILE_TABLE_ONE, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
					else
						drawTile(c, patternNumber, TILE_TABLE_TWO, (i % 32) + scx, (int) Math.floor(i / 32) + scy);
				}
			}
		}
		
	}
	
	protected void drawWindow(Canvas c) {
		if (BitTwiddles.getBit(5, memory.get(LCDC)) == 1) {
			if (BitTwiddles.getBit(6, memory.get(LCDC)) == 0) {
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
					if (BitTwiddles.getBit(4, memory.get(LCDC)) == 1)
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
					if (BitTwiddles.getBit(4, memory.get(LCDC)) == 1)
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
					
					if (BitTwiddles.getBit(7, memory.get(LCDC)) != 0) {
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
