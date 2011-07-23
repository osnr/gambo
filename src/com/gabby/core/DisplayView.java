package com.gabby.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.AttributedString;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
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
	
	private Bitmap background;
	private Bitmap window;
	private SurfaceHolder surfaceHolder;
	
	private boolean running = false;
	
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
	
	/**
	 * 
	 * @param mem The current memory of the GameBoy.
	 */
	protected void drawSprite(Canvas c, ByteBuffer spriteData, int x, int y) {
		Paint p = new Paint();
		p.setARGB(255, 0, 255, 255);
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				byte color = getColorFromBytePair(j, spriteData.get(i), spriteData.get(i + 1));
				if (color > 0)
					c.drawPoint(x + (j * 4), y + i, p);
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
					ByteBuffer b = ByteBuffer.allocate(16);
					b.order(ByteOrder.LITTLE_ENDIAN);
					b.put(new byte[] {0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55,});
					drawSprite(c, b, 25, 25);
				} finally {
					if (c != null)
						surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
	
	DisplayThread thread;
	
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
