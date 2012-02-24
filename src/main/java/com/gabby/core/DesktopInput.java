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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class DesktopInput extends KeyAdapter {
    protected int buttons;
    protected int dpad;
    protected static final int DPAD = BitTwiddles.bx00010000;
    protected static final int BUTTONS = BitTwiddles.bx00100000;
    protected Ram ram;
    private Cpu cpu;

    public DesktopInput(Ram ram) {
        this.ram = ram;

        buttons = 0x2f;
        dpad = 0x1f;
    }
    
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_X)
            buttons |= 1;
        else if (e.getKeyCode() == KeyEvent.VK_Z)
            buttons |= 1 << 1;
        else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
            buttons |= 1 << 2;
        else if (e.getKeyCode() == KeyEvent.VK_ENTER)
            buttons |= 1 << 3;
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            dpad |= 1;
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
            dpad |= 1 << 1;
        else if (e.getKeyCode() == KeyEvent.VK_UP)
            dpad |= 1 << 2;
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
            dpad |= 1 << 3;

        ram.write(Ram.JOYP, getInputByte());
        cpu.setInterrupt(Cpu.INPUT);
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_X)
            buttons &= ~(1);
        else if (e.getKeyCode() == KeyEvent.VK_Z)
            buttons &= ~(1 << 1);
        else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
            buttons &= ~(1 << 2);
        else if (e.getKeyCode() == KeyEvent.VK_ENTER)
            buttons &= ~(1 << 3);
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            dpad &= ~(1);
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
            dpad = ~(1 << 1);
        else if (e.getKeyCode() == KeyEvent.VK_UP)
            dpad = ~(1 << 2);
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
            dpad = ~(1 << 3);

        ram.write(Ram.JOYP, getInputByte());
    }

    public int getInputByte() {
        if (BitTwiddles.getBit(4, ram.getMemory().get(Ram.JOYP)) == 0) // check endianness
            return dpad;
        else if (BitTwiddles.getBit(5, ram.getMemory().get(Ram.JOYP)) == 0) // check endianness
            return buttons;
        else
            return 0x0; // is this ok?    
    }
    
    public void step() {
        int joyp = ram.read(Ram.JOYP);

        if (joyp == BUTTONS)
            ram.write(Ram.JOYP, buttons);
        else if (joyp == DPAD)
            ram.write(Ram.JOYP, dpad);
    }

	protected void setCpu(Cpu cpu) {
		this.cpu = cpu;
	}

	protected Cpu getCpu() {
		return cpu;
	}
}
