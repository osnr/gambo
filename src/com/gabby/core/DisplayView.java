package com.gabby.core;

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
	
	private Bitmap background;
	private Bitmap window;
	private SurfaceHolder surfaceHolder;
	
	private boolean running = false;
	
	class DisplayThread extends Thread {
		@Override
		public void run() {
			running = true;
			while (running) {
				Canvas c = null;
				try {
					c = surfaceHolder.lockCanvas();
					Paint p = new Paint();
					p.setARGB(256, 200, 250, 100);
					c.drawLine(0, 256, 256, 0, p);
					c.save();
				} finally {
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
