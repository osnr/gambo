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

import com.gabby.core.banking.Mbc1;

import java.util.Arrays;

public class Mmu {
    public static final int MEMORY_SIZE = 0xFFFF;
    
	public static final int ROM_LIMIT = 0x8000;

    public static final int BACKGROUND_WIDTH = 256;
    public static final int BACKGROUND_HEIGHT = 256;
    public static final int WINDOW_WIDTH = 160;
    public static final int WINDOW_HEIGHT = 144;
	
    public static final int CART_TYPE = 0x0147;
    public static final int ROM_SIZE = 0x0148;
    public static final int RAM_SIZE = 0x0149;
    
    public static final int OAM = 0xFE00;
    public static final int IF = 0xFF0F;
    public static final int LCDC = 0xFF40;
    public static final int STAT = 0xFF41;
    public static final int SCX = 0xFF43;
    public static final int SCY = 0xFF42;
    public static final int WX = 0xFF4B;
    public static final int WY = 0xFF4A;
    public static final int LY = 0xFF44;
    public static final int LYC = 0xFF45;
    public static final int DMA = 0xFF46;
    public static final int BGP = 0xFF47;
    public static final int OBP0 = 0xFF48;
    public static final int OBP1 = 0xFF49;
	
    public static final int TILE_TABLE_ONE = 0x8000;
    public static final int VRAM = 0x8000;
    public static final int TILE_TABLE_TWO = 0x8800;
    public static final int SPRITE_TABLE = TILE_TABLE_ONE;
    public static final int TILE_MAP_ONE = 0x9800;
    public static final int TILE_MAP_TWO = 0x9C00;

    public static final int DIV = 0xFF04;
    public static final int TIMA = 0xFF05;
    public static final int TMA = 0xFF06;
    public static final int TMC = 0xFF07;
    
    public static final int JOYP = 0xFF00;
	
    // flags for 5 interrupts in here
    public static final int IE = 0xFFFF;
    
    public class Timers {
    	// timers
    	// ------
	    private int divCounter;
	    private int timaCounter;
	    
	    private boolean isClockEnabled() {
		    return (read(Mmu.TMC) & 0x02) != 0;
	    }
	    
	    private void divide(int deltaClock) {
		    divCounter += deltaClock;
		    
		    if (divCounter >= 255) {
			    divCounter = 0;
			    memory[Mmu.DIV] = (byte) (read(Mmu.DIV) + 1); // write directly to buffer
		    }
	    }
	    
	    int getClockFreq() {
		    return read(Mmu.TMC) & 0x03;
	    }
	    
	    void resetCounter() {
		    switch (this.getClockFreq()) {
		    case 0: timaCounter = 1024; break;
		    case 1: timaCounter = 16; break;
		    case 2: timaCounter = 64; break;
		    case 3: timaCounter = 256;
		    }
	    }
	    
	    public void step(int deltaClock) {
		    this.divide(deltaClock);
		    
		    if (this.isClockEnabled()) {
			    timaCounter -= deltaClock;
			    
			    if (timaCounter <= 0) {
				    this.resetCounter();
				
				    if (rom[Mmu.TIMA] == 0xFF) {
					    memory[Mmu.TIMA] = (byte) read(Mmu.TMA);
					    
					    interrupts.setInterrupt(Interrupts.TIMER);
				    } else {
					    memory[Mmu.TIMA] = (byte) (read(Mmu.TIMA) + 1);
				    }
    			}
    		}
    	}
    }
    public final Timers timers = new Timers();
    
    public class Interrupts {
		// interrupts
		// ----------
		public static final int VBLANK = 0;
		public static final int LCDC = 1;
		public static final int TIMER = 2;
		public static final int SERIAL = 3;
		public static final int INPUT = 4;
	
		private boolean interrupts = false; // IME (master flag)
	
		public void enableInterrupts() {
			interrupts = true;
		}
	
		public void disableInterrupts() {
			interrupts = false;
		}

		private boolean interrupt(int flags, int i) {
			return (flags & (1 << i)) != 0;
		}
	
		public void checkInterrupts(Cpu cpu) {
			int ie = read(0xFFFF); // individual interrupt-enabled flags
			int ifl = read(0xFF0F); // interrupts triggered?
	
			if (this.interrupt(ie, Interrupts.VBLANK) &&
					this.interrupt(ifl, Interrupts.VBLANK)) {
				if (interrupts) {
					this.resetInterrupt(ifl, VBLANK);
					this.disableInterrupts();
					cpu.call(0x0040);
				}
				cpu.setHalting(false);
			}
	
			if (this.interrupt(ie, Interrupts.LCDC) &&
					this.interrupt(ifl, Interrupts.LCDC)) {
				if (interrupts) {
					resetInterrupt(ifl, LCDC);
					disableInterrupts();
					cpu.call(0x0048);
				}
				cpu.setHalting(false);
			}
	
			if (this.interrupt(ie, Interrupts.TIMER) &&
					this.interrupt(ifl, Interrupts.TIMER)) {
				if (interrupts) {
					resetInterrupt(ifl, TIMER);
					disableInterrupts();
					cpu.call(0x0050);
				}
				cpu.setHalting(false);
			}
	
			if (this.interrupt(ie, Interrupts.SERIAL) &&
					this.interrupt(ifl, Interrupts.SERIAL)) {
				if (interrupts) {
					resetInterrupt(ifl, SERIAL);
					disableInterrupts();
					cpu.call(0x0058);
				}
				cpu.setHalting(false);
			}
	
			if (this.interrupt(ie, INPUT) &&
					this.interrupt(ifl, INPUT)) {
				if (interrupts) {
					this.resetInterrupt(ifl, INPUT);
					this.disableInterrupts();
					cpu.call(0x0060);
				}
				cpu.setHalting(false);
			}
		}

		public void setInterrupt(int i) {
			this.setInterrupt(read(0xFF0F), i);
		}
		
		private void setInterrupt(int ifl, int i) {
			// trigger the interrupt itself
			memory[0xFF0F] = (byte) (ifl | (1 << i));
		}
	
		private void resetInterrupt(int ifl, int i) {
			// untrigger the interrupt itself
			memory[0xFF0F] = (byte) (ifl & ~(0x01 << i));
		}
    }
	public final Interrupts interrupts = new Interrupts();
	
	public class Inputs {
		private int buttons = 0xDF; // pin 15
		private int dpad = 0xEF; // pin 14

		public static final int BTN_A = 0xFE;
		public static final int BTN_B = 0xFD;
		public static final int BTN_SELECT = 0xFB;
		public static final int BTN_START = 0xF7;
		
		public static final int DPD_RIGHT = 0xFE;
		public static final int DPD_LEFT = 0xFD;
		public static final int DPD_UP = 0xFB;
		public static final int DPD_DOWN = 0xF7;
		
		public void pressedButton(int btn) {
			this.buttons &= btn;
			interrupts.setInterrupt(Interrupts.INPUT);
		}
		
		public void pressedDpad(int dpd) {
			this.dpad &= dpd;
			interrupts.setInterrupt(Interrupts.INPUT);
		}
		
		public void unpressedButton(int btn) {
			this.buttons |= ~btn;
		}
		
		public void unpressedDpad(int dpd) {
			this.dpad |= ~dpd;
		}
		
		private int joypValue(int data) {
			short output = 0x0F;
	    	if ((data & 0x10) == 0x00) {
	    		output &= this.dpad;
	    	}
	    	if ((data & 0x20) == 0x00) {
	    		output &= this.buttons;
	    	}
	    	output |= (data & 0xF0);
	    	return (byte) output;
	    }
	    
	    public void updateJoyp(int data) {
	    	// System.out.println("Updating joyp: " + Integer.toBinaryString(read(Mmu.JOYP)));
		    memory[Mmu.JOYP] = (byte) this.joypValue(data);
	    }
	}
	public final Inputs inputs = new Inputs();
    
	public interface Mbc {
		// game wrote to low memory,
	    // change banking settings
	    void bank(int addr, int data);

		int readRom(int addr);
		int readRam(int addr);
		
		void writeRam(int addr, int n);

		byte[] dumpRam();
		void loadRam(byte[] ram);
	}
	final Mbc mbc;
	
	byte[] rom;
	byte[] memory;

    public Mmu(byte[] cart) {
	    rom = new byte[0x200000];

	    for (int i = 0; i < cart.length; i++) {
		    rom[i] = cart[i];
	    }
	    
	    memory = new byte[0x10000];
        
	    switch (rom[Mmu.CART_TYPE]) {
        case 0x00:
        	mbc = new Mbc1(rom);
        	break;
        case 0x01:
        case 0x02:
        case 0x03:
        	mbc = new Mbc1(rom);
        	break;
        case 0x05:
        case 0x06:
        	// mbc = new Mbc2();
        	// break;
        case 0x12:
        case 0x13:
        	// mbc = new Mbc3();
        	default:
        	mbc = null;
        }
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
    
    public byte[] getRom() {
    	return this.rom;
    }
    
    public byte[] getAllMemory() {
	    byte[] ramDump = mbc.dumpRam();
        byte[] everything = new byte[memory.length + ramDump.length];
        
        for (int i = 0; i < memory.length; i++)
            everything[i] = memory[i];
        
        for (int i = memory.length; i < everything.length; i++) {
            everything[i] = ramDump[i];
        }

        return everything;
    }
    
    public void setRom(byte[] rom) {
    	this.rom = rom;
    }

    public void setAllMemory(byte[] data) {
        memory = new byte[0x10000];
        
        for (int i = 0; i < 0x10000; i++) {
            memory[i] = data[i];
        }

        mbc.loadRam(Arrays.copyOfRange(data, 0x10000, data.length));
    }
    
    protected void dmaTransfer(int data) {
    	int addr = data << 8; // source address is data * 0x100
    	//System.out.println("Running DMA transfer from addr " + addr + " to 0xFE00.");
    	//System.out.println("Source memory: " + Arrays.toString(this.readRange(addr, addr + 0xA0)));
    	for (int i = 0; i < 0xA0; i++) { // copy A0 bytes to OAM
    		this.write(0xFE00 + i, read(addr + i));
    	}
    	//System.out.println("Dest memory: " + Arrays.toString(this.readRange(0xFE00, 0xFE00 + 0xA0)));
    }
    
    // read unsigned byte from a position in memory
    public int read(int addr) {
	    if ((addr >= 0x4000) && (addr <= 0x7FFF)) { // ROM bank read
		    return mbc.readRom(addr);
	    } else if ((addr >= 0xA000) && (addr <= 0xBFFF)) { // RAM bank read
		    return mbc.readRam(addr);
	    } else if (addr < 0x4000) { // unbanked ROM read
		    return rom[addr] & 0xFF; // unsign
	    } else { // main memory read
	    	return memory[addr] & 0xFF;
	    }
    }

    // reads the interval [start, end)
    public int[] readRange(int start, int end) {
        int[] b = new int[end - start];
        
        for (int i = start; i < end; i++) {
            b[i - start] = this.read(i);
        }

        return b;
    }
    
    public int read16(int addr) {
    	return this.read(addr) | (this.read(addr + 1) << 8);
    }
    
    // write to a position in memory
    public void write(int addr, int n) {
	    if (addr < ROM_LIMIT) {
	    	// game is trying to do something with banks,
	    	// if it's writing to a low location
	    	mbc.bank(addr, n);
	    } else if ((addr >= 0xA000) && (addr < 0xC000)) {
	    	mbc.writeRam(addr, n);
	    } else if (addr == Mmu.JOYP) { // update JOYP register
		    inputs.updateJoyp(n);
	    } else if (addr == Mmu.TMC) { // change timer settings
		    int oldFreq = timers.getClockFreq();
		    memory[Mmu.TMC] = (byte) n;
	    	int newFreq = timers.getClockFreq();
	    	
	    	if (oldFreq != newFreq) {
	    		timers.resetCounter();
	    	}
	    } else if (addr == Mmu.DMA) { // DMA transfer 
	    	dmaTransfer(n);
	    } else {
		    memory[addr] = (byte) n;
	    }
    }

    public void write(int addr, int n1, int n2) {
        this.write(addr, n2);
        this.write(addr + 1, n1);
    }

    public void write16(int addr, int nn) {
    	this.write(addr, (byte) (nn & 0xFF));
    	this.write(addr + 1, (byte) ((nn >> 8) & 0xFF));
    }
}
