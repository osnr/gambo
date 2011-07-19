package com.gabby.core;

public class CPU {
	private static final int INTERRUPT_PERIOD = 0;

	private static final byte[] CYCLES = new byte[256];

	private byte[] memory;

	static {
		// CYCLES[
	}
	
	public static void main() {

	}

	public CPU(byte[] mem) {
		memory = mem;
	}

	public void emulate(int initialPC) {
		byte opc;
		int pc = initialPC;

		int counter = CPU.INTERRUPT_PERIOD;
		
		while(true) {
			opc = memory[pc++];
			counter -= CYCLES[opc];
		}
	}
}