package com.gabby.core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class DesktopInput extends KeyAdapter {
    protected boolean a, b, select, start;
    protected boolean up, down, left, right;
    
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_Z)
            a = true;
        else if (e.getKeyCode() == KeyEvent.VK_X)
            b = true;
        else if (e.getKeyCode() == KeyEvent.VK_ENTER)
            start = true;
        else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
            select = true;
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
            left = true;
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            right = true;
        else if (e.getKeyCode() == KeyEvent.VK_UP)
            up = true;
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
            down = true;
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_Z)
            a = false;
        else if (e.getKeyCode() == KeyEvent.VK_X)
            b = false;
        else if (e.getKeyCode() == KeyEvent.VK_ENTER)
            start = false;
        else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
            select = false;
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
            left = false;
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            right = false;
        else if (e.getKeyCode() == KeyEvent.VK_UP)
            up = false;
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
            down = false;        
    }

    public byte getButtonByte() {
        return 0x0;
    }

    public byte getDirectionByte() {
        return 0x0;
    }
}
