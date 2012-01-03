package com.gabby.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Cpu {
    private static final int INTERRUPT_PERIOD = 0;

    public static final int A = 0;
    public static final int B = 1;
    public static final int C = 2;
    public static final int D = 3;
    public static final int E = 4;
    public static final int F = 5;
    public static final int H = 6;
    public static final int L = 7;

    private static final byte[] CYCLES = new byte[256];

    // registers
    // ---------
    // 8-bit registers: unsigned bytes
    private int[] regs = new int[8];

    public int a() { return regs[A]; }
    public void setA(int a) { regs[A] = a; }

    public int b() { return regs[B]; }
    public void setB(int b) { regs[B] = b; }

    public int c() { return regs[C]; }
    public void setC(int c) { regs[C] = c; }

    public int d() { return regs[D]; }
    public void setD(int d) { regs[D] = d;}

    public int e() { return regs[E]; }
    public void setE(int e) { regs[E] = e; }

    public int f() { return regs[F]; }
    public void setF(int f) { regs[F] = f; }

    public int h() { return regs[H]; }
    public void setH(int h) { regs[H] = h; }

    public int l() { return regs[L]; }
    public void setL(int l) { regs[L] = l; }

    // 16-bit registers
    private int bc() { return regs[B] << 8 | regs[C]; }
    
    private void setBC(int n) { }
    private void setBC(int n1, int n2) {
    	regs[B] = n1;
    	regs[C] = n2;
    }

    private int de() { return regs[D] << 8 | regs[E]; }
    
    private void setDE(int n) { }
    private void setDE(int n1, int n2) {
    	regs[D] = n1;
    	regs[E] = n2;
    }

    private int hl() { return regs[H] << 8 | regs[L]; }
    
    private void setHL(int n) { }
    private void setHL(int n1, int n2) {
    	regs[H] = n1;
    	regs[L] = n2;
    }
	
    private int sp; // stack pointer: 16-bit

    public int sp() {
    	return sp;
    }
    public void setSP(int sp) {
    	this.sp = sp;
    }

    
    // operation helpers
    // -----
    private void inc(int r) {
    	regs[r] = (regs[r] + 1) & 0xFF;
    	
    	zero = (regs[r] == 0);
    	halfCarry = (regs[r] & 0xF) == 0; // don't know what this does
    	subtract = false;
	}
    
    private void dec(int r) {
    	regs[r] = (regs[r] - 1) & 0xFF;
    	
    	zero = (regs[r] == 0);
    	halfCarry = (regs[r] & 0xF) == 0xF; // don't know what this does
    	subtract = true;
    }
    
    private void add(int r1, int r2) {
    	int tmp = regs[r1] + regs[r2];
    	
    	halfCarry = ((tmp & 0xF) < (A & 0xF)); // ??
    	carry = (tmp > 0xFF);
    	
    	regs[r1] = tmp & 0xFF;
    	
    	zero = (regs[r1] == 0);
    	subtract = false;
    }
    
    private void sub(int r1, int r2) {
    	int tmp = regs[r1] - regs[r2];
    	
    	halfCarry = ((regs[r1] & 0xF) < (tmp & 0xF));
    	carry = (tmp < 0);
    	
    	regs[r1] = tmp & 0xFF;
    	
    	zero = (regs[r1] == 0);
    	subtract = true;
    }
    
    private void and(int r) {
    	// AND with A, result in A
    }
    
    private void or(int r) {
    	// OR with A, result in A
    }
    
    private void xor(int r) {
    	// XOR with A, result in A
    }
    
    // flags
    // -----
    private boolean zero, // if the last math operation resulted in a zero
        subtract, // if the last math operation involved a subtraction
        halfCarry, // if the last math operation cause a carry from the lower nibble (bit 3-4)
        carry; // if the last math operation cause a carry (bit 7-8)

    public boolean getZero() { return zero; }
    public void setZero(boolean zero) { this.zero = zero; }

    public boolean getSubtract() { return subtract; }
    public void setSubtract(boolean subtract) { this.subtract = subtract; }

    public boolean getHalfCarry() { return halfCarry; }
    public void setHalfCarry(boolean halfCarry) { this.halfCarry = halfCarry; }

    public boolean getCarry() { return carry; }
    public void setCarry(boolean carry) { this.carry = carry; }

    private void clearFlags() {
        zero = false;
        subtract = false;
        halfCarry = false;
        carry = false;
    }

    private int pc; // = initialPC;

    public int getPc() { return pc; }
    public void setPc(int pc) { this.pc = pc; }

    private int counter = Cpu.INTERRUPT_PERIOD;

    public int getCounter() { return counter; }
    public void setCounter(int counter) { this.counter = counter; }

    public Cpu(Ram ram) {
    	this.ram = ram;
    }

    // memory access
    private Ram ram;
    
    // pop 1 byte from program counter position in memory
    // then move forward
    private int readPC() {
        return ram.read(pc++);
    }

    // pop 2 bytes from program counter position in memory
    // then move pc forward 2
    private int readPC16() {
        pc += 2;
        return ram.getMemory().getShort(pc - 2) & 0xFF; // unsign
    }


    public void emulate(int initialPC) {
        int opcode;

        while (true) {
            opcode = readPC();

            switch (opcode) {
            case 0x00: // NOP
                // No operation
                break;
				
            case 0x01: // LD BC, nn
                setBC(readPC(), readPC());
                break;

            case 0x02: // LD (BC), A
                ram.write(bc(), regs[A]);
                break;

            case 0x03: // INC BC
                setBC(bc() + 1);
                break;

            case 0x04: // INC B
                inc(B);
                break;

            case 0x05: // DEC B
            	dec(B);
                break;

            case 0x06: // LD B, n
                regs[B] = readPC();
                break;

            case 0x07: // RLC A
                // Rotate A left with carry
                // Store old bit 7 of A in CF
                // Reset SF, HCF, ZF
				
                break;

            case 0x08: // LD (nn), SP
                ram.write16(readPC16(), sp);
                break;

            case 0x09: // ADD HL, BC
                setHL(hl() + bc());

                break;

            case 0x0A: // LD A, (BC)
                regs[A] = ram.read(bc());
                break;

            case 0x0B: // DEC BC
                setBC(bc() - 1);
                break;

            case 0x0C: // INC C
                inc(C);
                break;

            case 0x0D: // DEC C
                dec(C);
                break;

            case 0x0E: // LD C, n
                regs[C] = readPC();
                break;

            case 0x0F: // RRC A
                // Rotate A right with carry
                break;

            case 0x10: // STOP
                // Stop CPU until user input
                break;

            case 0x11: // LD DE, nn
                setDE(readPC(), readPC());
                break;

            case 0x12: // LD (DE), A
                ram.write(de(), regs[A]);
                break;

            case 0x13: // INC DE
                setDE(de() + 1);
                break;

            case 0x14: // INC D
                inc(D);
                break;

            case 0x15: // DEC D
                dec(D);
                break;

            case 0x16: // LD D, n
                regs[D] = readPC();
                break;

            case 0x17: // RL A
                // Rotate A left?
                break;

            case 0x18: // JR n
                // Relative jump by (signed) next byte
                pc += ((byte) readPC()); // sign
                break;

            case 0x19: // ADD HL, DE
                setHL(hl() + de());

                break;

            case 0x1A: // LD A, (DE)
                regs[A] = ram.read(de());
                break;

            case 0x1B: // DEC DE
                setDE(de() - 1);

                break;

            case 0x1C: // INC E
                inc(E);
                break;

            case 0x1D: // DEC E
                dec(E);
                break;

            case 0x1E: // LD E, n
                regs[E] = readPC();
                break;

            case 0x1F: // RR A
                // Rotate A right?
                break;

            case 0x20: // JR NZ, n
                // Relative jump by (signed) next byte
                // IF last result was not zero
                if (!zero) pc += ((byte) readPC());
                break;

            case 0x21: // LD HL, nn
                setHL(readPC16());
                break;

            case 0x22: // LDI (HL), A
                // Save A into memory at location HL,
            	// then increment 16-bit HL
                ram.write(hl(), regs[A]);
                setHL(hl() + 1);
                break;

            case 0x23: // INC HL
                setHL(hl() + 1);
                break;

            case 0x24: // INC H
                inc(H);
                break;

            case 0x25: // DEC H
                dec(H);
                break;

            case 0x26: // LD H, n
                regs[H] = readPC();
                break;

            case 0x27: // DAA
                // Decimal-adjust A
                // WTF does this mean
                break;

            case 0x28: // JR Z, n
                // Relative jump by (signed) next byte
                // IF last result was zero
                if (zero) pc += ((byte) readPC());
                break;

            case 0x29: // ADD HL, HL
                // Add 16-bit HL to HL
                // Result in HL
                setHL(hl() + hl());

                break;

            case 0x2A: // LDI A, (HL)
				

                break;

            case 0x2B: // DEC HL
            	setHL(hl() - 1);
                break;

            case 0x2C: // INC L
            	inc(L);
                break;

            case 0x2D: // DEC L
            	dec(L);
                break;

            case 0x2E: // LD L, n
            	regs[L] = readPC();
                break;

            case 0x2F: // CPL

                break;

            case 0x30: // JR NC, n

                break;

            case 0x31: // LD SP, nn
            	sp = readPC16();
                break;

            case 0x32: // LDD (HL), A

                break;

            case 0x33: // INC SP
            	sp++;
                break;

            case 0x34: // INC (HL)

                break;

            case 0x35: // DEC (HL)

                break;

            case 0x36: // LD (HL), n
            	ram.write(hl(), readPC());
                break;

            case 0x37: // SCF
            	
                break;

            case 0x38: // JR C, n

                break;

            case 0x39: // ADD HL, SP

                break;

            case 0x3A: //  LDD A, (HL)

                break;

            case 0x3B: // DEC SP
            	sp--;
                break;

            case 0x3C: // INC A
            	inc(A);
                break;

            case 0x3D: // DEC A
            	dec(A);
                break;

            case 0x3E: // LD A, n
            	regs[A] = readPC();
                break;

            case 0x3F: // CCF

                break;

            case 0x40: // LD B, B
            	// WTF?
            	regs[B] = regs[B];
                break;

            case 0x41: // LD B, C
            	regs[B] = regs[C];
                break;

            case 0x42: // LD B, D
            	regs[B] = regs[D];
                break;

            case 0x43: // LD B, E
            	regs[B] = regs[E];
                break;

            case 0x44: // LD B, H
            	regs[B] = regs[H];
                break;

            case 0x45: // LD B, L
            	regs[B] = regs[L];
                break;

            case 0x46: // LD B, (HL)
            	regs[B] = ram.read(hl());
                break;

            case 0x47: // LD B, A
            	regs[B] = regs[A];
                break;

            case 0x48: // LD C, B
            	regs[C] = regs[B];
                break;

            case 0x49: // LD C, C
            	// WTF?
            	regs[C] = regs[C];
                break;

            case 0x4A: // LD C, D
            	regs[C] = regs[D];
                break;

            case 0x4B: // LD C, E
            	regs[C] = regs[E];
                break;

            case 0x4C: // LD C, H
            	regs[C] = regs[H];
                break;

            case 0x4D: // LD C, L
            	regs[C] = regs[L];
                break;

            case 0x4E: // LD C, (HL)
            	regs[C] = ram.read(hl());
                break;

            case 0x4F: // LD C, A
            	regs[C] = regs[A];
                break;

            case 0x50: // LD D, B
            	regs[D] = regs[B];
                break;

            case 0x51: // LD D, C
            	regs[D] = regs[C];
                break;

            case 0x52: // LD D, D
            	// WTF?
            	regs[D] = regs[D];
                break;

            case 0x53: // LD D, E
            	regs[D] = regs[E];
                break;

            case 0x54: // LD D, H
            	regs[D] = regs[H];
                break;

            case 0x55: // LD D, L
            	regs[D] = regs[L];
                break;

            case 0x56: // LD D, (HL)
            	regs[D] = ram.read(hl());
                break;

            case 0x57: // LD D, A
            	regs[D] = regs[A];
                break;

            case 0x58: // LD E, B
            	regs[E] = regs[B];
                break;

            case 0x59: // LD E, C
            	regs[E] = regs[C];
                break;

            case 0x5A: // LD E, D
            	regs[E] = regs[D];
                break;

            case 0x5B: // LD E, E
            	// WTF?
            	regs[E] = regs[E];
                break;

            case 0x5C: // LD E, H
            	regs[E] = regs[H];
                break;

            case 0x5D: // LD E, L
            	regs[E] = regs[L];
                break;

            case 0x5E: // LD E, (HL)
            	regs[E] = ram.read(hl());
                break;

            case 0x5F: // LD E, A
            	regs[E] = regs[A];
                break;

            case 0x60: // LD H, B
            	regs[H] = regs[B];
                break;

            case 0x61: // LD H, C
            	regs[H] = regs[C];
                break;

            case 0x62: // LD H, D
            	regs[H] = regs[D];
                break;

            case 0x63: // LD H, E
            	regs[H] = regs[E];
                break;

            case 0x64: // LD H, H
            	// WTF?
            	regs[H] = regs[H];
                break;

            case 0x65: // LD H, L
            	regs[H] = regs[L];
                break;

            case 0x66: // LD H, (HL)
            	regs[H] = ram.read(hl());
                break;

            case 0x67: // LD H, A
            	regs[H] = regs[A];
                break;

            case 0x68: // LD L, B
            	regs[L] = regs[B];
                break;

            case 0x69: // LD L, C
            	regs[L] = regs[C];
                break;

            case 0x6A: // LD L, D
            	regs[L] = regs[D];
                break;

            case 0x6B: // LD L, E
            	regs[L] = regs[E];
                break;

            case 0x6C: // LD L, H
            	regs[L] = regs[H];
                break;

            case 0x6D: // LD L, L
            	// WTF?
            	regs[L] = regs[L];
                break;

            case 0x6E: // LD L, (HL)
            	regs[L] = ram.read(hl());
                break;

            case 0x6F: // LD L, A
            	regs[L] = regs[A];
                break;

            case 0x70: // LD (HL), B
            	ram.write(hl(), regs[B]);
                break;

            case 0x71: // LD (HL), C
            	ram.write(hl(), regs[C]);
                break;

            case 0x72: // LD (HL), D
            	ram.write(hl(), regs[D]);
                break;

            case 0x73: // LD (HL), E
            	ram.write(hl(), regs[E]);
                break;

            case 0x74: // LD (HL), H
            	ram.write(hl(), regs[H]);
                break;

            case 0x75: // LD (HL), L
            	ram.write(hl(), regs[L]);
                break;

            case 0x76: // HALT
            	
                break;

            case 0x77: // LD (HL), A
            	ram.write(hl(), regs[A]);
                break;

            case 0x78: // LD A, B
            	regs[A] = regs[B];
                break;

            case 0x79: // LD A, C
            	regs[A] = regs[C];
                break;

            case 0x7A: // LD A, D
            	regs[A] = regs[D];
                break;

            case 0x7B: // LD A, E
            	regs[A] = regs[E];
                break;

            case 0x7C: // LD A, H
            	regs[A] = regs[H];
                break;

            case 0x7D: // LD A, L
            	regs[A] = regs[L];
                break;

            case 0x7E: // LD A, (HL)
            	regs[A] = ram.read(hl());
                break;

            case 0x7F: // LD A, A
            	// WTF?
            	regs[A] = regs[A];
                break;

            case 0x80: // ADD A, B
            	add(A, B);
                break;

            case 0x81: // ADD A, C
            	add(A, C);
                break;

            case 0x82: // ADD A, D
            	add(A, D);
                break;

            case 0x83: // ADD A, E
            	add(A, E);
                break;

            case 0x84: // ADD A, H
            	add(A, H);
                break;

            case 0x85: // ADD A, L
            	add(A, L);
                break;

            case 0x86: // ADD A, (HL)
            	
                break;

            case 0x87: // ADD A, A
            	add(A, A);
                break;

            case 0x88: // ADC A, B
            	
                break;

            case 0x89: // ADC A, C

                break;

            case 0x8A: // ADC A, D

                break;

            case 0x8B: // ADC A, E

                break;

            case 0x8C: // ADC A, H

                break;

            case 0x8D: // ADC A, L

                break;

            case 0x8E: // ADC A, (HL)

                break;

            case 0x8F: // ADC A, A

                break;

            case 0x90: // SUB A, B
            	sub(A, B);
                break;

            case 0x91: // SUB A, C
            	sub(A, C);
                break;

            case 0x92: // SUB A, D
            	sub(A, D);
                break;

            case 0x93: // SUB A, E
            	sub(A, E);
                break;

            case 0x94: // SUB A, H
            	sub(A, H);
                break;

            case 0x95: // SUB A, L
            	sub(A, L);
                break;

            case 0x96: // SUB A, (HL)

                break;

            case 0x97: // SUB A, A
            	sub(A, A);
                break;

            case 0x98: // SBC A, B

                break;

            case 0x99: // SBC A, C

                break;

            case 0x9A: // SBC A, D

                break;

            case 0x9B: // SBC A, E

                break;

            case 0x9C: // SBC A, H

                break;

            case 0x9D: // SBC A, L

                break;

            case 0x9E: // SBC A, (HL)

                break;

            case 0x9F: // SBC A, A

                break;

            case 0xA0: // AND B

                break;

            case 0xA1: // AND C

                break;

            case 0xA2: // AND D

                break;

            case 0xA3: // AND E

                break;

            case 0xA4: // AND H

                break;

            case 0xA5: // AND L

                break;

            case 0xA6: // AND (HL)

                break;

            case 0xA7: // AND A

                break;

            case 0xA8: // XOR B

                break;

            case 0xA9: // XOR C

                break;

            case 0xAA: // XOR D

                break;

            case 0xAB: // XOR E

                break;

            case 0xAC: // XOR H

                break;

            case 0xAD: // XOR L

                break;

            case 0xAE: // XOR (HL)

                break;

            case 0xAF: // XOR A

                break;

            case 0xB0: // OR B

                break;

            case 0xB1: // OR C

                break;

            case 0xB2: // OR D

                break;

            case 0xB3: // OR E

                break;

            case 0xB4: // OR H

                break;

            case 0xB5: // OR L

                break;

            case 0xB6: // OR (HL)

                break;

            case 0xB7: // OR A

                break;

            case 0xB8: // CP B

                break;

            case 0xB9: // CP C

                break;

            case 0xBA: // CP D

                break;

            case 0xBB: // CP E

                break;

            case 0xBC: // CP H

                break;

            case 0xBD: // CP L

                break;

            case 0xBE: // CP (HL)

                break;

            case 0xBF: // CP A

                break;

            case 0xC0: // RET !FZ

                break;

            case 0xC1: // POP BC

                break;

            case 0xC2: // JP !FZ, nn

                break;

            case 0xC3: // JP nn

                break;

            case 0xC4: // CALL !FZ, nn

                break;

            case 0xC5: // PUSH BC

                break;

            case 0xC6: // ADD A, n

                break;

            case 0xC7: // RST 0

                break;

            case 0xC8: // RET FZ

                break;

            case 0xC9: // RET

                break;

            case 0xCA: // JP FZ, nn

                break;

            case 0xCB: // Secondary OP Code Set:

                break;

            case 0xCC: // CALL FZ, nn

                break;

            case 0xCD: // CALL nn

                break;

            case 0xCE: // ADC A, n

                break;

            case 0xCF: // RST 0x8

                break;

            case 0xD0: // RET !FC

                break;

            case 0xD1: // POP DE

                break;

            case 0xD2: // JP !FC, nn

                break;

            case 0xD3: // 0xD3 - Illegal

                break;

            case 0xD4: // CALL !FC, nn

                break;

            case 0xD5: // PUSH DE

                break;

            case 0xD6: // SUB A, n
 
                break;

            case 0xD7: // RST 0x10

                break;

            case 0xD8: // RET FC

                break;

            case 0xD9: // RETI

                break;

            case 0xDA: // JP FC, nn

                break;

            case 0xDB: // 0xDB - Illegal

                break;

            case 0xDC: // CALL FC, nn

                break;

            case 0xDD: // 0xDD - Illegal

                break;

            case 0xDE: // SBC A, n

                break;

            case 0xDF: // RST 0x18

                break;

            case 0xE0: // LDH (n), A

                break;

            case 0xE1: // POP HL

                break;

            case 0xE2: // LD (0xFF00 + C), A

                break;

            case 0xE3: // 0xE3 - Illegal

                break;

            case 0xE4: // 0xE4 - Illegal

                break;

            case 0xE5: // PUSH HL

                break;

            case 0xE6: // AND n

                break;

            case 0xE7: // RST 0x20

                break;

            case 0xE8: // ADD SP, n

                break;

            case 0xE9: // JP, (HL)

                break;

            case 0xEA: // LD n, A

                break;

            case 0xEB: // 0xEB - Illegal

                break;

            case 0xEC: // 0xEC - Illegal

                break;

            case 0xED: // 0xED - Illegal

                break;

            case 0xEE: // XOR n

                break;

            case 0xEF: // RST 0x28

                break;

            case 0xF0: // LDH A, (n)

                break;

            case 0xF1: // POP AF

                break;

            case 0xF2: // LD A, (0xFF00 + C)

                break;

            case 0xF3: // DI

                break;

            case 0xF4: // 0xF4 - Illegal

                break;

            case 0xF5: // PUSH AF

                break;

            case 0xF6: // OR n

                break;

            case 0xF7: // RST 0x30

                break;

            case 0xF8: // LDHL SP, n

                break;

            case 0xF9: // LD SP, HL

                break;

            case 0xFA: // LD A, (nn)

                break;

            case 0xFB: // EI

                break;

            case 0xFC: // 0xFC - Illegal

                break;

            case 0xFD: // 0xFD - Illegal

                break;

            case 0xFE: // CP n

                break;

            case 0xFF: // RST 0x38

            }

            // counter -= CYCLES[opc];
        }
    }
	
}
