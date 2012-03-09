package com.gabby.core.banking;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.gabby.core.Mmu;
import com.gabby.core.Mmu.Mbc;

public class Mbc1 implements Mbc {
	private ByteBuffer rom;
	private ByteBuffer ram;
	
	private boolean ramEnabled = false; // 0x0000 - 0x1FFF
	private int romBank = 0x01; // 0x2000 - 0x3FFF
	private int ramBank = 0x00; // 0x4000 - 0x5FFF
	private boolean romOnlyMode = false;
	
	private int getRamSize() {
		switch (rom.get(Mmu.RAM_SIZE)) {
		case 0: return 0;
		case 1: return 0x800;
		case 2: return 0x2000;
		case 3: return 0x8000;
		case 4: return 0x20000;
		default: return 0x20000;
		}
	}
	
	public Mbc1(ByteBuffer rom) {
		this.rom = rom;
		
		this.ram = ByteBuffer.allocate(getRamSize());
	}
	
	@Override
	public void bank(int addr, int data) {
		// game tried to write below 0x8000,
		// change banking settings
		if ((addr & 0xE000) == 0x0000) { // 0x0000 - 0x1FFF
			ramEnabled = (data & 0x0F) == 0x0A;
		} else if ((addr & 0xE000) == 0x2000) { // 0x2000 - 0x3FFF
			romBank = (data & 0x1F) + ((data & 0x1F) == 0 ? 1 : 0);
		} else if ((addr & 0xE000) == 0x4000) { // 0x4000 - 0x5FFF
			ramBank = (data & 0x03);
		} else if ((addr & 0xE000) == 0x6000) { // 0x6000 - 0x7FFF
			romOnlyMode = (data & 0x01) != 0;
		}
	}

	@Override
	public int readRom(int addr) {
		// 0x4000 - 0x7FFF
		if (!romOnlyMode) {
			return rom.get((ramBank << 19) | (romBank << 14) | (addr & 0x3FFF)) & 0xFF;
		} else {
			return rom.get((romBank << 14) | (addr & 0x3FFF)) & 0xFF;
		}
	}

	@Override
	public int readRam(int addr) {
		// 0xA000 - 0xBFFF
		if (ramEnabled) {
			if (!romOnlyMode) {
				return ram.get(addr & 0x1FFF) & 0xFF;
			} else {
				return ram.get((ramBank << 13) | (addr & 0x1FFF)) & 0xFF;
			}
		} else {
			throw new RuntimeException("Tried to access RAM");
			// return 0x00;
		}
	}

	@Override
	public void writeRam(int addr, int n) {
		// game writing from 0xA000 - 0xBFFF
		if (ramEnabled) {
			if (romOnlyMode) {
				ram.put(addr & 0x1FFF, (byte) n);
			} else {
				ram.put((ramBank << 13) | (addr & 0x1FFF), (byte) n);
			}
		}
	}
}
