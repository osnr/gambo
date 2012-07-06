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

import java.io.*;
import java.util.Calendar;

import com.gambo.core.Cpu;
import com.gambo.core.Display;
import com.gambo.core.Mmu;
import com.gambo.core.SaveState;

public class DesktopCpu extends Cpu {
	long lastSync;
    private boolean saveState = false, loadState = false;
    private String path = "";
	
	public DesktopCpu(Mmu mmu, Display display) {
		super(mmu, display);
	}

    /**
     * Tells the CPU to save as soon as it finishes its current opcode and associated operations.
     * @param path Path to which the state is saved.
     */
    public void saveState(String path) {
        this.path = path;
        saveState = true;
    }
       
    public void loadState(String path) {
        this.path = path;
        this.loadState = true;
    }

    @Override
    protected boolean emulateOp() throws IllegalOperationException {
        if (saveState) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
                out.writeObject(new SaveState(this, mmu, display));
                out.close();

                path = "";
                saveState = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (loadState) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
                SaveState s = (SaveState) in.readObject();
                in.close();

                this.loadFromSaveState(s);
                mmu.loadFromSaveState(s);
                display.loadFromSaveState(s);

                path = "";
                loadState = false;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
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
