/*
    Copyright (c) 2012 by Vincent Pacelli and Omar Rizwan

    This file is part of Gambo.

    Gambo is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gambo is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Gambo.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gambo.web.client;

import com.gambo.core.Display;
import com.gambo.core.Mmu;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.ImageData;

class WebDisplay extends Display {
	int width = GamboWeb.WIDTH;
	int height = GamboWeb.HEIGHT;

    ImageData bufferImage;
    CanvasPixelArray cpa;
    Canvas canvas;
    
	public WebDisplay(Mmu mmu, Canvas canvas) {
		super(mmu);
		
        this.canvas = canvas;
        
        width = canvas.getCoordinateSpaceWidth();
        height = canvas.getCoordinateSpaceHeight();
        
        this.bufferImage = canvas.getContext2d().createImageData(width, height);
        this.cpa = bufferImage.getData();
	}

	@Override
	protected int getPixel(int x, int y) {
		return (cpa.get(y*width*4 + x*4 + 0) << 16) |
				(cpa.get(y*width*4 + x*4 + 1) << 8) |
				cpa.get(y*width*4 + x*4 + 2);
	}

	@Override
	protected void setPixel(int x, int y, int rgb) {
		cpa.set(y*width*4 + x*4 + 0, (rgb & 0xFF0000) >> 16); // red
		cpa.set(y*width*4 + x*4 + 1, (rgb & 0x00FF00) >> 8); // green
		cpa.set(y*width*4 + x*4 + 2, (rgb & 0x0000FF)); // blue
		cpa.set(y*width*4 + x*4 + 3, 0xFF); // alpha
	}
	
	@Override
	protected void repaint() {
		canvas.getContext2d().putImageData(bufferImage, 0, 0);
	}
}
