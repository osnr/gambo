package com.gabby.core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class DesktopInput extends KeyAdapter {
    protected byte buttons;
    protected byte dpad;
    protected Ram ram;

    public DesktopInput(Ram ram) {
        this.ram = ram;
        buttons = 0x20;
        dpad = 0x10;
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
    }

    public byte getInputByte() {
        if (BitTwiddles.getBit(4, ram.getMemory().get(Ram.INPUT)) == 0) // check endianness
            return dpad;
        else if (BitTwiddles.getBit(5, ram.getMemory().get(Ram.INPUT)) == 0) // check endianness
            return buttons;
        else
            return 0x0; // is this ok?    
    }
}
