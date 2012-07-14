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

package com.gambo.desktop;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.gambo.core.Cpu;
import com.gambo.core.Mmu;
import com.gambo.core.Mmu.Inputs;

class DesktopInput extends KeyAdapter {
    protected Mmu mmu;
    protected Cpu cpu;

    public DesktopInput(Mmu mmu, Cpu cpu) {
        this.mmu = mmu;
        this.cpu = cpu;
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_X:    
                mmu.inputs.pressedButton(Inputs.BTN_B);
                break;
            case KeyEvent.VK_Z:
                mmu.inputs.pressedButton(Inputs.BTN_A);
                break;
            case KeyEvent.VK_SHIFT:
                mmu.inputs.pressedButton(Inputs.BTN_SELECT);
                break;
            case KeyEvent.VK_ENTER:
                mmu.inputs.pressedButton(Inputs.BTN_START);
                break;
            case KeyEvent.VK_RIGHT:
                mmu.inputs.pressedDpad(Inputs.DPD_RIGHT);
                break;
            case KeyEvent.VK_LEFT:
                mmu.inputs.pressedDpad(Inputs.DPD_LEFT);
                break;
            case KeyEvent.VK_UP:
                mmu.inputs.pressedDpad(Inputs.DPD_UP);
                break;
            case KeyEvent.VK_DOWN:
                mmu.inputs.pressedDpad(Inputs.DPD_DOWN);
            case KeyEvent.VK_P:
                if (!cpu.isPaused())
                    cpu.pause();
                else
                    cpu.unpause();
                break;
            default:
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_X:
                mmu.inputs.unpressedButton(Inputs.BTN_B);
                break;
            case KeyEvent.VK_Z:
                mmu.inputs.unpressedButton(Inputs.BTN_A);
                break;
            case KeyEvent.VK_SHIFT:
                mmu.inputs.unpressedButton(Inputs.BTN_SELECT);
                break;
            case KeyEvent.VK_ENTER:
                mmu.inputs.unpressedButton(Inputs.BTN_START);
                break;
            case KeyEvent.VK_RIGHT:
                mmu.inputs.unpressedDpad(Inputs.DPD_RIGHT);
                break;
            case KeyEvent.VK_LEFT:
                mmu.inputs.unpressedDpad(Inputs.DPD_LEFT);
                break;
            case KeyEvent.VK_UP:
                mmu.inputs.unpressedDpad(Inputs.DPD_UP);
                break;
            case KeyEvent.VK_DOWN:
                mmu.inputs.unpressedDpad(Inputs.DPD_DOWN);
        }
    }
}
