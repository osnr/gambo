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

package com.gabby.desktop;

import java.awt.image.BufferedImage;

import com.gabby.core.Display;
import com.gabby.core.Mmu;

class DesktopDisplay extends Display {
    BufferedImage buffer;
    Emulator emulator;
    
	public DesktopDisplay(Mmu mmu, Emulator emulator) {
		super(mmu);
		
        this.emulator = emulator;
        this.buffer = emulator.buffer;
	}

	@Override
	protected int getPixel(int x, int y) {
		return buffer.getRGB(x, y) & 0xFFFFFF; // Display expects 1-byte RGB
	}

	@Override
	protected void setPixel(int x, int y, int rgb) {
		buffer.setRGB(x, y, rgb);
	}

	@Override
	protected void repaint() {
		emulator.repaint();
	}
}
