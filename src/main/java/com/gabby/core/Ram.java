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

package com.gabby.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.AttributedString;
import java.util.ArrayList;

public class Ram {

    public static final int MEMORY_SIZE = 0xFFFF;
    
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
    public static final int LY = 0xFF44;
    public static final int LYC = 0xFF45;
	
    public static final int TILE_TABLE_ONE = 0x8000;
    public static final int TILE_TABLE_TWO = 0x8800;
    public static final int SPRITE_TABLE = TILE_TABLE_ONE;
    public static final int TILE_MAP_ONE = 0x9800;
    public static final int TILE_MAP_TWO = 0x9C00;

    public static final int INPUT = 0xFF00;
	
    // flags for 5 interrupts in here
    public static final int IE = 0xFFFF;
    
    ByteBuffer memory;

    public Ram() {
        memory = ByteBuffer.allocate(0xFFFF + 1);
        memory.order(ByteOrder.LITTLE_ENDIAN);
    }
	
    public ByteBuffer getMemory() {
        return memory;
    }
	
    public void setMemory(ByteBuffer memory) {
        this.memory = memory;
    }
    
    // read unsigned byte from a position in memory
    public int read(int addr) {
        return memory.get(addr) & 0xFF; // unsign
    }
    
    public int read16(int addr) {
        return memory.getShort(addr) & 0xFFFF; // unsign
    }

    // write to a position in memory
    public void write(int addr, int n) {
        memory.put(addr, (byte) n);
    }

    public void write(int addr, int n1, int n2) {
        memory.put(addr, (byte) n2);
        memory.put(addr + 1, (byte) n1);
    }

    public void write16(int addr, int n) {
        memory.putShort(addr, (short) n);
    }
}
