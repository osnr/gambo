package com.gabby.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CPU {
	private static final int INTERRUPT_PERIOD = 0;

	private static final byte[] CYCLES = new byte[256];

	private ByteBuffer memory;

	static {
		// CYCLES[
	}
	
	public static void main() {

	}

	public CPU() {
		memory = ByteBuffer.allocate(0xFFFF);
		memory.order(ByteOrder.LITTLE_ENDIAN);
	}

	public void emulate(int initialPC) {
		byte opc;
		int pc = initialPC;

		int counter = CPU.INTERRUPT_PERIOD;
		
		while(true) {
			opc = memory.get(pc++);
			counter -= CYCLES[opc];
		}
	}
}