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

package com.gabby.core.banking;

import com.gabby.core.Mmu;
import com.gabby.core.Mmu.Mbc;

public class Mbc3 implements Mbc {
	private byte[] rom;
	private byte[] ram;
	
	private boolean ramEnabled = false; // 0x0000 - 0x1FFF
	private int romBank = 0x01; // 0x2000 - 0x3FFF
	private int ramBank = 0x00; // 0x4000 - 0x5FFF
	
	private int getRamSize() {
		switch (rom[Mmu.RAM_SIZE]) {
		case 0: return 0;
		case 1: return 0x800;
		case 2: return 0x2000;
		case 3: return 0x8000;
		case 4: return 0x20000;
		default: return 0x20000;
		}
	}
	
	public Mbc3(byte[] rom) {
		this.rom = rom;
		
		this.ram = new byte[getRamSize()];
	}
	
	@Override
	public void bank(int addr, int data) {
		// game tried to write below 0x8000,
		// change banking settings
		if ((addr & 0xE000) == 0x0000) { // 0x0000 - 0x1FFF
			ramEnabled = (data & 0x0F) == 0x0A;

		} else if ((addr & 0xE000) == 0x2000) { // 0x2000 - 0x3FFF
			romBank = (data & 0x7F) + ((data & 0x7F) == 0 ? 1 : 0);

		} else if ((addr & 0xE000) == 0x4000) { // 0x4000 - 0x5FFF
			ramBank = data;

		} else if ((addr & 0xE000) == 0x6000) { // 0x6000 - 0x7FFF
			// TODO insert RTC stuff here
		}
	}

	@Override
	public int readRom(int addr) {
		// 0x4000 - 0x7FFF
		return rom[(romBank << 14) | (addr & 0x3FFF)] & 0xFF;
	}

	@Override
	public int readRam(int addr) {
		// 0xA000 - 0xBFFF
		if (ramEnabled) {
			if ((ramBank >= 0x00) && (ramBank <= 0x03)) {
				return ram[(ramBank << 13) | (addr & 0x1FFF)] & 0xFF;
			} else {
				// TODO insert RTC stuff here
				return 0x00;
			}
		} else {
			throw new RuntimeException("Tried to access RAM");
		}
	}

	@Override
	public void writeRam(int addr, int n) {
		// game writing from 0xA000 - 0xBFFF
		if (ramEnabled) {
			if ((ramBank >= 0x00) && (ramBank <= 0x03)) {
				ram[(ramBank << 13) | (addr & 0x1FFF)] = (byte) n;
			} else {
				// TODO insert RTC stuff here
			}
		}
	}
	
	@Override
	public byte[] dumpRam() {
		return ram;
	}

	@Override
	public void loadRam(byte[] ram) {
		this.ram = ram;
	}
}
