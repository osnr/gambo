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
    
	public static final int ROM_LIMIT = 0x8000;

    public static final int BACKGROUND_WIDTH = 256;
    public static final int BACKGROUND_HEIGHT = 256;
    public static final int WINDOW_WIDTH = 160;
    public static final int WINDOW_HEIGHT = 144;
	
    public static final int OAM = 0xFE00;
    public static final int IF = 0xFF0F;
    public static final int LCDC = 0xFF40;
    public static final int STAT = 0xFF41;
    public static final int SCX = 0xFF43;
    public static final int SCY = 0xff42;
    public static final int WX = 0xFF4B;
    public static final int WY = 0xFF4A;
    public static final int LY = 0xFF44;
    public static final int LYC = 0xFF45;
    public static final int BGP = 0xFF47;
    public static final int OBP0 = 0xFF48;
    public static final int OBP1 = 0xFF49;
	
    public static final int TILE_TABLE_ONE = 0x8000;
    public static final int VRAM = 0x8000;
    public static final int TILE_TABLE_TWO = 0x8800;
    public static final int SPRITE_TABLE = TILE_TABLE_ONE;
    public static final int TILE_MAP_ONE = 0x9800;
    public static final int TILE_MAP_TWO = 0x9C00;

    public static final int JOYP = 0xFF00;
	
    // flags for 5 interrupts in here
    public static final int IE = 0xFFFF;
    
    ByteBuffer memory;

    public Ram() {
        memory = ByteBuffer.allocate(0xFFFF + 1);
        memory.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    // preload appropriate register values
    // (probably done by the NINTENDO program on real hardware?)
    public void init() {
		this.write(0xFF05, 0x00); // TIMA
		this.write(0xFF06, 0x00); // TMA
		this.write(0xFF07, 0x00); // TAC
		this.write(0xFF10, 0x80); // NR10
		this.write(0xFF11, 0xBF); // NR11
		this.write(0xFF12, 0xF3); // NR12
		this.write(0xFF14, 0xBF); // NR14
		this.write(0xFF16, 0x3F); // NR21
		this.write(0xFF17, 0x00); // NR22
		this.write(0xFF19, 0xBF); // NR24
		this.write(0xFF1A, 0x7F); // NR30
		this.write(0xFF1B, 0xFF); // NR31
		this.write(0xFF1C, 0x9F); // NR32
		this.write(0xFF1E, 0xBF); // NR33
		this.write(0xFF20, 0xFF); // NR41
		this.write(0xFF21, 0x00); // NR42
		this.write(0xFF22, 0x00); // NR43
		this.write(0xFF23, 0xBF); // NR??
		this.write(0xFF24, 0x77); // NR50
		this.write(0xFF25, 0xF3); // NR51
		this.write(0xFF26, 0xF1); // NR52 (GB-only)
		this.write(0xFF26, 0xF0); // NR52 (SGB-only)

		this.write(0xFF40, 0x91); // LCDC
		this.write(0xFF42, 0x00); // SCY
		this.write(0xFF43, 0x00); // SCX
		this.write(0xFF45, 0x00); // LYC
		this.write(0xFF47, 0xFC); // BGP
		this.write(0xFF48, 0xFF); // OBP0
		this.write(0xFF49, 0xFF); // OBP1
		this.write(0xFF4A, 0x00); // WY
		this.write(0xFF4B, 0x00); // WX
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

    // reads the interval [start, end)
    public int[] readRange(int start, int end) {
        int[] b = new int[end - start];
        
        for (int i = start; i < end; i++) {
            b[i - start] = read(i);
        }

        return b;
    }
    
    public int read16(int addr) {
        return memory.getShort(addr) & 0xFFFF; // unsign
    }

    // write to a position in memory
    public void write(int addr, int n) {
	    if (addr < ROM_LIMIT) return;

        memory.put(addr, (byte) n);
    }

    public void write(int addr, int n1, int n2) {
	    if (addr < ROM_LIMIT) return;

        memory.put(addr, (byte) n2);
        memory.put(addr + 1, (byte) n1);
    }

    public void write16(int addr, int nn) {
	    if (addr < ROM_LIMIT) return;

        memory.putShort(addr, (short) nn);
    }
}
