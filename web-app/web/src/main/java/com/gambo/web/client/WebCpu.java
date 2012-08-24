package com.gambo.web.client;

import java.util.Date;

import com.gambo.core.Cpu;
import com.gambo.core.Display;
import com.gambo.core.Mmu;
import com.google.gwt.user.client.Timer;

public class WebCpu extends Cpu {
	Timer timer = new ResumeExecution();
	long lastSync;
	
	public WebCpu(Mmu mmu, Display display) {
		super(mmu, display);
	}

    @Override
    protected void log(int opcode, int[] regs) {

    }

	@Override
	protected boolean cycling(boolean newFrame) {
		return !newFrame;
	}
	
	@Override
	protected void timingSync() {
		long t = new Date().getTime();
		
		int delay = (int) (16 - (t - lastSync));
		if (delay <= 0 || delay > 16) delay = 1;

		timer.schedule(delay);
	}

	class ResumeExecution extends Timer {
		@Override
		public void run() {
			try {
				lastSync = new Date().getTime();
				emulate(getPc());
			} catch (IllegalOperationException e) {
				e.printStackTrace();
			}
		}
	}
}
