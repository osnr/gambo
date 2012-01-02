package com.gabby.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Cpu {
    private static final int INTERRUPT_PERIOD = 0;

    private static final byte[] CYCLES = new byte[256];

    private ByteBuffer memory;

    // registers
    // ---------
    // 8-bit registers: unsigned bytes
    private int a, b, c, d, e, f, h, l;

    public int getA() { return a; }
    public void setA(int a) { this.a = a; }

    public int getB() { return b; }
    public void setB(int b) { this.b = b; }

    public int getC() { return c; }
    public void setC(int c) { this.c = c; }

    public int getD() { return d; }
    public void setD(int d) { this.d = d;}

    public int getE() { return e; }
    public void setE(int e) { this.e = e; }

    public int getF() { return f; }
    public void setF(int f) { this.f = f; }

    public int getH() { return h; }
    public void setH(int h) { this.h = h; }

    public int getL() { return l; }
    public void setL(int l) { this.l = l; }

    // 16-bit registers
    private int bc() { return b << 8 | c; }
    private void ld_bc(int n) { }
    private void ld_bc(int n1, int n2) { b = n1; c = n2; }

    private int de() { return d << 8 | e; }
    private void ld_de(int n) { }
    private void ld_de(int n1, int n2) { d = n1; e = n2; }

    private int hl() { return h << 8 | l; }
    private void ld_hl(int n) { }
    private void ld_hl(int n1, int n2) { h = n1; l = n2; }
	
    private int sp; // stack pointer: 16-bit

    public int getSp() { return sp; }
    public void setSp(int sp) { this.sp = sp; }

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

    public Cpu() {
        memory = ByteBuffer.allocate(0xFFFF);
        memory.order(ByteOrder.LITTLE_ENDIAN);
    }

    // memory access

    // pop 1 byte from program counter position in memory
    // then move forward
    private int readPC() {
        return read(pc++);
    }

    // pop 2 bytes from program counter position in memory
    // then move pc forward 2
    private int readPC16() {
        pc += 2;
        return memory.getShort(pc - 2) & 0xFFFF; // unsign
    }

    // read unsigned byte from a position in memory
    private int read(int addr) {
        return memory.get(addr) & 0xFF; // unsign
    }

    // write to a position in memory
    private void write(int addr, int n) {
        memory.put(addr, (byte) n);
    }

    private void write(int addr, int n1, int n2) {
        memory.put(addr, (byte) n1);
        memory.put(addr + 1, (byte) n2);
    }

    private void write16(int addr, int n) {
        memory.putShort(addr, (short) n);
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
                // Load next 2 bytes in memory into BC
                ld_bc(readPC(), readPC());
                break;

            case 0x02: // LD (BC), A
                // Save value of A to memory at location BC
                write(bc(), a);
                break;

            case 0x03: // INC BC
                // Increment 16-bit BC by 1
                ld_bc(bc() + 1);
                break;

            case 0x04: // INC B
                // Increment B by 1
                b++;

                zero = (b == 0);
                subtract = false;
                halfCarry = false; // ?
                break;

            case 0x05: // DEC B
                // Decrement B by 1
                b--;

                zero = (b == 0);
                subtract = true;
                halfCarry = false; // ?
                break;

            case 0x06: // LD B, n
                // Load next byte in memory into B
                b = readPC();
                break;

            case 0x07: // RLC A
                // Rotate A left with carry
                // Store old bit 7 of A in CF
                // Reset SF, HCF, ZF
				
                break;

            case 0x08: // LD (nn), SP
                // Save value of SP to memory at location [next 2 bytes]
                write16(readPC16(), sp);
                break;

            case 0x09: // ADD HL, BC
                // Add 16-bit BC to HL
                // (result is stored in HL)
                ld_hl(hl() + bc());

                break;

            case 0x0A: // LD A, (BC)
                // Save value of memory at location BC to A
                a = read(bc());
                break;

            case 0x0B: // DEC BC
                // Decrement 16-bit BC by 1
                ld_bc(bc() - 1);
                break;

            case 0x0C: // INC C
                // Increment C by 1
                c++;

                zero = (c == 0);
                subtract = false;
                halfCarry = false; // ?
                break;

            case 0x0D: // DEC C
                // Decrement C by 1
                c--;

                zero = (c == 0);
                subtract = true;
                halfCarry = false; // ?
                break;

            case 0x0E: // LD C, n
                // Load next byte in memory into C
                c = readPC();
                break;

            case 0x0F: // RRC A
                // Rotate A right with carry
                break;

            case 0x10: // STOP
                // Stop CPU until user input
                break;

            case 0x11: // LD DE, nn
                // Load next 2 bytes in memory into DE
                ld_de(readPC(), readPC());
                break;

            case 0x12: // LD (DE), A
                // Save value of A to memory at location DE
                write(de(), a);
                break;

            case 0x13: // INC DE
                // Increment 16-bit DE by 1
                ld_de(de() + 1);
                break;

            case 0x14: // INC D
                // Increment D by 1
                d++;

                break;

            case 0x15: // DEC D
                // Decrement D by 1
                d--;

                zero = (d == 0);
                subtract = true;
                halfCarry = false; // ?
                break;

            case 0x16: // LD D, n
                // Load next byte in memory into D
                d = readPC();
                break;

            case 0x17: // RL A
                // Rotate A left?
                break;

            case 0x18: // JR n
                // Relative jump by (signed) next byte
                pc += ((byte) readPC()); // sign
                break;

            case 0x19: // ADD HL, DE
                // Add 16-bit DE to HL
                // Result is stored in HL
                ld_hl(hl() + de());

                break;

            case 0x1A: // LD A, (DE)
                // Save value of memory at location DE to A
                a = read(de());
                break;

            case 0x1B: // DEC DE
                // Decrement 16-bit DE by 1
                ld_de(de() - 1);

                break;

            case 0x1C: // INC E
                // Increment E by 1
                e++;

                break;

            case 0x1D: // DEC E
                // Decrement E by 1
                e--;

                zero = (e == 0);
                subtract = true;
                halfCarry = false; // ?
                break;

            case 0x1E: // LD E, n
                // Load next byte in memory into E
                e = readPC();
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
                // Load next 2 bytes in memory into HL
                ld_hl(readPC16());
                break;

            case 0x22: // LDI (HL), A
                // Save A into memory at location HL, then increment 16-bit HL
                write(hl(), a);
                ld_hl(hl() + 1);

                break;

            case 0x23: // INC HL
                // Increment 16-bit HL by 1
                ld_hl(hl() + 1);

                break;

            case 0x24: // INC H
                // Increment H by 1
                h++;

                break;

            case 0x25: // DEC H
                // Decrement H by 1
                h--;

                break;

            case 0x26: // LD H, n
                // Load next byte in memory into H
                h = readPC();
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
                ld_hl(hl() + hl());

                break;

            case 0x2A: // LDI A, (HL)
				

                break;

            case 0x2B: // DEC HL

                break;

            case 0x2C: // INC L

                break;

            case 0x2D: // DEC L

                break;

            case 0x2E: // LD L, n

                break;

            case 0x2F: // CPL

                break;

            case 0x30: // JR NC, n

                break;

            case 0x31: // LD SP, nn

                break;

            case 0x32: // LDD (HL), A

                break;

            case 0x33: // INC SP

                break;

            case 0x34: // INC (HL)

                break;

            case 0x35: // DEC (HL)

                break;

            case 0x36: // LD (HL), n

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

                break;

            case 0x3C: // INC A

                break;

            case 0x3D: // DEC A

                break;

            case 0x3E: // LD A, n

                break;

            case 0x3F: // CCF

                break;

            case 0x40: // LD B, B

                break;

            case 0x41: // LD B, C

                break;

            case 0x42: // LD B, D

                break;

            case 0x43: // LD B, E

                break;

            case 0x44: // LD B, H

                break;

            case 0x45: // LD B, L

                break;

            case 0x46: // LD B, (HL)

                break;

            case 0x47: // LD B, A

                break;

            case 0x48: // LD C, B

                break;

            case 0x49: // LD C, C

                break;

            case 0x4A: // LD C, D

                break;

            case 0x4B: // LD C, E

                break;

            case 0x4C: // LD C, H

                break;

            case 0x4D: // LD C, L

                break;

            case 0x4E: // LD C, (HL)

                break;

            case 0x4F: // LD C, A

                break;

            case 0x50: // LD D, B

                break;

            case 0x51: // LD D, C

                break;

            case 0x52: // LD D, D

                break;

            case 0x53: // LD D, E

                break;

            case 0x54: // LD D, H

                break;

            case 0x55: // LD D, L

                break;

            case 0x56: // LD D, (HL)

                break;

            case 0x57: // LD D, A

                break;

            case 0x58: // LD E, B

                break;

            case 0x59: // LD E, C

                break;

            case 0x5A: // LD E, D

                break;

            case 0x5B: // LD E, E

                break;

            case 0x5C: // LD E, H

                break;

            case 0x5D: // LD E, L

                break;

            case 0x5E: // LD E, (HL)

                break;

            case 0x5F: // LD E, A

                break;

            case 0x60: // LD H, B

                break;

            case 0x61: // LD H, C

                break;

            case 0x62: // LD H, D

                break;

            case 0x63: // LD H, E

                break;

            case 0x64: // LD H, H

                break;

            case 0x65: // LD H, L

                break;

            case 0x66: // LD H, (HL)

                break;

            case 0x67: // LD H, A

                break;

            case 0x68: // LD L, B

                break;

            case 0x69: // LD L, C

                break;

            case 0x6A: // LD L, D

                break;

            case 0x6B: // LD L, E

                break;

            case 0x6C: // LD L, H

                break;

            case 0x6D: // LD L, L

                break;

            case 0x6E: // LD L, (HL)

                break;

            case 0x6F: // LD L, A

                break;

            case 0x70: // LD (HL), B

                break;

            case 0x71: // LD (HL), C

                break;

            case 0x72: // LD (HL), D

                break;

            case 0x73: // LD (HL), E

                break;

            case 0x74: // LD (HL), H

                break;

            case 0x75: // LD (HL), L

                break;

            case 0x76: // HALT

                break;

            case 0x77: // LD (HL), A

                break;

            case 0x78: // LD A, B

                break;

            case 0x79: // LD A, C

                break;

            case 0x7A: // LD A, D

                break;

            case 0x7B: // LD A, E

                break;

            case 0x7C: // LD A, H

                break;

            case 0x7D: // LD A, L

                break;

            case 0x7E: // LD, A, (HL)

                break;

            case 0x7F: // LD A, A

                break;

            case 0x80: // ADD A, B

                break;

            case 0x81: // ADD A, C

                break;

            case 0x82: // ADD A, D

                break;

            case 0x83: // ADD A, E

                break;

            case 0x84: // ADD A, H

                break;

            case 0x85: // ADD A, L

                break;

            case 0x86: // ADD A, (HL)

                break;

            case 0x87: // ADD A, A

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

                break;

            case 0x91: // SUB A, C

                break;

            case 0x92: // SUB A, D

                break;

            case 0x93: // SUB A, E

                break;

            case 0x94: // SUB A, H

                break;

            case 0x95: // SUB A, L

                break;

            case 0x96: // SUB A, (HL)

                break;

            case 0x97: // SUB A, A

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

            case 0xC6: // ADD, n

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
