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

import java.io.*;
import java.util.Calendar;

import com.gabby.core.Cpu;
import com.gabby.core.Display;
import com.gabby.core.Mmu;
import com.gabby.core.SaveState;

public class DesktopCpu extends Cpu {
	long lastSync;
    boolean saveState = false;
    String savePath = "";
	
	public DesktopCpu(Mmu mmu, Display display) {
		super(mmu, display);
	}

    /**
     * Tells the CPU to save as soon as it finishes its current opcode and associated operations.
     * @param path Path to which the state is saved.
     */
    public void saveState(String path) {
        savePath = path;
        saveState = true;
    }

    @Override
    protected boolean emulateOp() throws IllegalOperationException {
        if (saveState) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savePath));
                out.writeObject(new SaveState(this, mmu, display));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.emulateOp();
    }
	
	@Override
	protected boolean timingWait() {
		try {
			long t = Calendar.getInstance().getTimeInMillis();

			while (t - lastSync < 16) {
				Thread.sleep(1);

				t = Calendar.getInstance().getTimeInMillis();
			}

			lastSync = t;
			
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}
}
