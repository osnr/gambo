package com.gabby.core;


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
    private int af() { return regs[A] << 8 | regs[F]; }
    
    private void setAF(int nn) { }
    private void setAF(int n1, int n2) {
    	regs[A] = n1;
    	regs[F] = n2;
    }

    
    private int bc() { return regs[B] << 8 | regs[C]; }
    
    private void setBC(int nn) { }
    private void setBC(int n1, int n2) {
    	regs[B] = n1;
    	regs[C] = n2;
    }

    private int de() { return regs[D] << 8 | regs[E]; }
    
    private void setDE(int nn) { }
    private void setDE(int n1, int n2) {
    	regs[D] = n1;
    	regs[E] = n2;
    }

    private int hl() { return regs[H] << 8 | regs[L]; }
    
    private void setHL(int nn) { }
    private void setHL(int n1, int n2) {
    	regs[H] = n1;
    	regs[L] = n2;
    }
	
    private int sp = 0xFFFE; // stack pointer: 16-bit

    public int sp() {
    	return sp;
    }
    public void setSP(int nn) {
    	sp = nn & 0xFFFF; // unsign
    }

    
    // operation helpers
    // -----
    // note: xAt methods put result in RAM
    // xTo methods put result in either A or given register r
    // and operate on given value n

    private void inc(int r) {
    	regs[r] = (regs[r] + 1) & 0xFF;
    	
    	zero = (regs[r] == 0);
    	halfCarry = (regs[r] & 0xF) == 0; // don't know what this does
    	subtract = false;
	}
    private void incAt(int addr) {
    	
    }
    
    private void dec(int r) {
    	regs[r] = (regs[r] - 1) & 0xFF;
    	
    	zero = (regs[r] == 0);
    	halfCarry = (regs[r] & 0xF) == 0xF; // don't know what this does
    	subtract = true;
    }
    private void decAt(int addr) {
    	
    }
    
    private void add(int r1, int r2) {
    	addTo(r1, regs[r2]);
    }
    private void addTo(int r, int n) {
    	int tmp = regs[r] + n;
    	
    	halfCarry = ((tmp & 0xF) < (regs[r] & 0xF)); // ??
    	carry = (tmp > 0xFF);
    	
    	regs[r] = tmp & 0xFF;
    	
    	zero = (regs[r] == 0);
    	subtract = false;
    }
    
    private void addHL(int nn) {
    	int tmp = hl() + nn;
    	halfCarry = ((hl() & 0xFFF) > (tmp & 0xFFF)); // ??
    	carry = (tmp > 0xFFFF);
    	setHL(tmp & 0xFFFF);
    	subtract = false;
    }
    private void setHLAfterAdd(int n1, int n2) {
    	// WTF
    	int tmp = (n2 << 24) >> 24;
    	setHL((n1 + tmp) & 0xFFFF);
    	tmp = n1 ^ tmp ^ hl();
    	
    	carry = ((tmp & 0x100) == 0x100);
    	halfCarry = ((tmp & 0x10) == 0x10);
    	zero = false;
    	subtract = false;
    }
    private void addSP(int n) {
    	// I don't even remotely understand this method.
    	int tmp2 = (n << 24) >> 24; // ??
    	int tmp = (sp + tmp2) & 0xFFFF;
    	tmp2 = sp ^ tmp2 ^ tmp;
    	sp = tmp;
    	
    	carry = ((tmp2 & 0x100) == 0x100);
    	halfCarry = ((tmp2 & 0x10) == 0x10);
    	zero = false;
    	subtract = false;
    }
    
    private void adc(int r1, int r2) {
    	// Add regs[r2] and carry flag value to r1
    	adcTo(r1, regs[r2]);
    }
    private void adcTo(int r, int n) {
    	// Add n and carry flag value to r
    	int tmp = regs[r] + n + (carry ? 1 : 0);
    	
    	halfCarry = (((tmp & 0xF) + (regs[r] & 0xF) + (carry ? 1 : 0)) > 0xF); // ??
    	carry = (tmp > 0xFF);
    	
    	regs[r] = tmp & 0xFF;
    	
    	zero = (regs[r] == 0);
    	subtract = false;
    }
    
    private void sub(int r1, int r2) {
    	subTo(r1, regs[r2]);
    }
    private void subTo(int r, int n) {
    	int tmp = regs[r] - n;
    	
    	halfCarry = ((regs[r] & 0xF) < (tmp & 0xF)); // ??
    	carry = (tmp < 0);
    	
    	regs[r] = tmp & 0xFF;
    	
    	zero = (regs[r] == 0);
    	subtract = true;
    }
    
    private void sbc(int r1, int r2) {
    	sbcTo(r1, regs[r2]);
    }
    private void sbcTo(int r, int n) {
    	int tmp = regs[r] - n - (carry ? 1 : 0);
    	
    	halfCarry = (((regs[r] & 0xF) - (n & 0xF) - (carry ? 1 : 0)) < 0); // ??
    	carry = (tmp < 0);
    	
    	regs[r] = tmp & 0xFF;
    	
    	zero = (regs[r] == 0);
    	subtract = true;
    }
    
    
    private void and(int r) {
    	andTo(regs[r]);
    }
    private void andTo(int n) {
    	// AND with A, result in A
    	regs[A] &= n;
    	
    	zero = (regs[A] == 0);
    	halfCarry = true; // ??
    	subtract = false;
    	carry = false;
    }
    
    private void or(int r) {
    	or(regs[r]);
    }
    private void orTo(int n) {
    	// OR with A, result in A
    	regs[A] |= n;
    	
    	zero = (regs[A] == 0);
    	subtract = false;
    	halfCarry = false;
    	carry = false;
    }
    
    private void xor(int r) {
    	xorTo(regs[r]);
    }
    private void xorTo(int n) {
    	// XOR with A, result in A
    	regs[A] ^= n;
    	
    	zero = (regs[A] == 0);
    	subtract = false;
    	halfCarry = false;
    	carry = false;
    }
    
    private void cp(int r) {
    	cpTo(regs[r]);
    }
    private void cpTo(int n) {
    	// Compare A with n
    	// (Basically equiv. to subtraction w/ discarded result)
    	int tmp = regs[A] - n;
    	
    	halfCarry = ((regs[A] & 0xF) < (tmp & 0xF)); // ??
    	carry = (tmp < 0);
       	zero = ((tmp & 0xFF) == 0);
    	subtract = true;
    }
    
    private void rl(int r) {
    	
    }
    private void rlc(int r) {
    	
    }
    
    private void rr(int r) {
    	
    }
    private void rrc(int r) {
    	
    }
    
    private void daa() {
    	
    }
    
    // stack
    private void push(int nn) {
    	sp -= 2;
    	ram.write16(sp, nn);
    }
    
    private int pop() {
    	setSP(sp + 2);
    	return ram.read16(sp - 2);
    }
    
    // jumps
    private void ret() {
    	jp(pop());
    }
    
    private void jp(int addr) {
    	pc = addr;
    }
    
    private void jr(int n) {
    	pc += ((byte) n); // sign
    }
    
    private void call(int addr) {
    	push(addr);
    	jp(addr);
    }
    
    private void rst(int addr) {
    	push(pc);
    	jp(addr);
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

    // interrupts
    // ----------
    private boolean interrupts = true;
    
    private int counter = Cpu.INTERRUPT_PERIOD;

    public int getCounter() { return counter; }
    public void setCounter(int counter) { this.counter = counter; }

    private void enableInterrupts() {
    	interrupts = true;
    }
    
    private void disableInterrupts() {
    	interrupts = false;
    }
    
    // state
    // -----
    private int pc; // = initialPC;

    public int getPc() { return pc; }
    public void setPc(int pc) { this.pc = pc; }


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
        return ram.read16(pc - 2);
    }


    public void emulate(int initialPC) throws IllegalOperationException {
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
				rlc(A);
                break;

            case 0x08: // LD (nn), SP
                ram.write16(readPC16(), sp);
                break;

            case 0x09: // ADD HL, BC
            	addHL(bc());
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
            	rrc(A);
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
            	rl(A);
                break;

            case 0x18: // JR n
                // Relative jump by (signed) next byte
                jr(readPC());
                break;

            case 0x19: // ADD HL, DE
                addHL(de());
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
                rr(A);
                break;

            case 0x20: // JR NZ, n
                // Relative jump by (signed) next byte
                // IF last result was not zero
                if (!zero) jr(readPC());
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
            	// For floating-point math (haha, good luck w/ that)
                daa();
                break;

            case 0x28: // JR Z, n
                // Relative jump by (signed) next byte
                // IF last result was zero
                if (zero) jr(readPC());
                break;

            case 0x29: // ADD HL, HL
                // Add 16-bit HL to HL
                // Result in HL
                addHL(hl());
                break;

            case 0x2A: // LDI A, (HL)
				regs[A] = ram.read(hl());
				inc(A);
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
            	// Complement A register (Flip all bits)
            	regs[A] = ~regs[A] & 0xFF;
            	
            	subtract = true;
            	halfCarry = true;
                break;

            case 0x30: // JR NC, n
            	// Relative jump by next byte
            	// IF not carry
            	if (!carry) jr(readPC());
                break;

            case 0x31: // LD SP, nn
            	setSP(readPC16());
                break;

            case 0x32: // LDD (HL), A
            	ram.write(hl(), regs[A]);
            	decAt(hl());
                break;

            case 0x33: // INC SP
            	setSP(sp + 1);
                break;

            case 0x34: // INC (HL)
            	incAt(hl());
                break;

            case 0x35: // DEC (HL)
            	decAt(hl());
                break;

            case 0x36: // LD (HL), n
            	ram.write(hl(), readPC());
                break;

            case 0x37: // SCF
            	// set carry flag
            	carry = true;
                break;

            case 0x38: // JR C, n
            	// Relative jump by next byte
            	// IF carry
            	if (carry) jr(readPC());
                break;

            case 0x39: // ADD HL, SP
            	addHL(sp);
                break;

            case 0x3A: // LDD A, (HL)
            	regs[A] = ram.read(hl()); 
            	dec(A);
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
            	// flip carry flag
            	carry = !carry;
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
            	// Power down CPU until an interrupt occurs
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
            	addTo(A, ram.read(hl()));
                break;

            case 0x87: // ADD A, A
            	add(A, A);
                break;

            case 0x88: // ADC A, B
            	adc(A, B);
                break;

            case 0x89: // ADC A, C
            	adc(A, C);
                break;

            case 0x8A: // ADC A, D
            	adc(A, D);
                break;

            case 0x8B: // ADC A, E
            	adc(A, E);
                break;

            case 0x8C: // ADC A, H
            	adc(A, H);
                break;

            case 0x8D: // ADC A, L
            	adc(A, L);
                break;

            case 0x8E: // ADC A, (HL)
            	adcTo(A, ram.read(hl()));
                break;

            case 0x8F: // ADC A, A
            	adc(A, A);
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
            	subTo(A, hl());
                break;

            case 0x97: // SUB A, A
            	sub(A, A);
                break;

            case 0x98: // SBC A, B
            	sbc(A, B);
                break;

            case 0x99: // SBC A, C
            	sbc(A, C);
                break;

            case 0x9A: // SBC A, D
            	sbc(A, D);
                break;

            case 0x9B: // SBC A, E
            	sbc(A, E);
                break;

            case 0x9C: // SBC A, H
            	sbc(A, H);
                break;

            case 0x9D: // SBC A, L
            	sbc(A, L);
                break;

            case 0x9E: // SBC A, (HL)
            	sbcTo(A, hl());
                break;

            case 0x9F: // SBC A, A
            	sbc(A, A);
                break;

            case 0xA0: // AND B
            	and(B);
                break;

            case 0xA1: // AND C
            	and(C);
                break;

            case 0xA2: // AND D
            	and(D);
                break;

            case 0xA3: // AND E
            	and(E);
                break;

            case 0xA4: // AND H
            	and(H);
                break;

            case 0xA5: // AND L
            	and(L);
                break;

            case 0xA6: // AND (HL)
            	andTo(hl());
                break;

            case 0xA7: // AND A
            	and(A);
                break;

            case 0xA8: // XOR B
            	xor(B);
                break;

            case 0xA9: // XOR C
            	xor(C);
                break;

            case 0xAA: // XOR D
            	xor(D);
                break;

            case 0xAB: // XOR E
            	xor(E);
                break;

            case 0xAC: // XOR H
            	xor(H);
                break;

            case 0xAD: // XOR L
            	xor(L);
                break;

            case 0xAE: // XOR (HL)
            	xorTo(hl());
                break;

            case 0xAF: // XOR A
            	xor(A);
                break;

            case 0xB0: // OR B
            	or(B);
                break;

            case 0xB1: // OR C
            	or(C);
                break;

            case 0xB2: // OR D
            	or(D);
                break;

            case 0xB3: // OR E
            	or(E);
                break;

            case 0xB4: // OR H
            	or(H);
                break;

            case 0xB5: // OR L
            	or(L);
                break;

            case 0xB6: // OR (HL)
            	orTo(hl());
                break;

            case 0xB7: // OR A
            	or(A);
                break;

            case 0xB8: // CP B
            	cp(B);
                break;

            case 0xB9: // CP C
            	cp(C);
                break;

            case 0xBA: // CP D
            	cp(D);
                break;

            case 0xBB: // CP E
            	cp(E);
                break;

            case 0xBC: // CP H
            	cp(H);
                break;

            case 0xBD: // CP L
            	cp(L);
                break;

            case 0xBE: // CP (HL)
            	cpTo(hl());
                break;

            case 0xBF: // CP A
            	cp(A);
                break;

            case 0xC0: // RET !FZ
            	if (!zero) ret();
                break;

            case 0xC1: // POP BC
            	setBC(pop());
                break;

            case 0xC2: // JP !FZ, nn
            	// Absolute jump to position at next 2 bytes
            	// IF not zero
            	if (!zero) jp(readPC16());
                break;

            case 0xC3: // JP nn
            	// Absolute jump to position at next 2 bytes
            	jp(readPC16());
                break;

            case 0xC4: // CALL !FZ, nn
            	if (!zero) call(readPC16());
                break;

            case 0xC5: // PUSH BC
            	push(bc());
                break;

            case 0xC6: // ADD A, n
            	addTo(A, readPC());
                break;

            case 0xC7: // RST 0
            	rst(0);
                break;

            case 0xC8: // RET FZ
            	if (zero) ret();
                break;

            case 0xC9: // RET
            	ret();
                break;

            case 0xCA: // JP FZ, nn
            	// Absolute jump to position at next 2 bytes
            	// IF zero
            	if (zero) jp(readPC16());
                break;

            case 0xCB: // Secondary OP Code Set:

                break;

            case 0xCC: // CALL FZ, nn
            	if (zero) call(readPC16());
                break;

            case 0xCD: // CALL nn
            	call(readPC16());
                break;

            case 0xCE: // ADC A, n
            	adcTo(A, readPC());
                break;

            case 0xCF: // RST 0x08
            	rst(0x08);
                break;

            case 0xD0: // RET !FC
            	if (!carry) ret();
                break;

            case 0xD1: // POP DE
            	setDE(pop());
                break;

            case 0xD2: // JP !FC, nn
            	// Jump to position at next 2 bytes
            	// IF not carry
            	if (!carry) jp(readPC16());
                break;

            case 0xD3: // 0xD3 - Illegal
            	throw new IllegalOperationException(opcode);

            case 0xD4: // CALL !FC, nn
            	if (!carry) call(readPC16());
                break;

            case 0xD5: // PUSH DE
            	push(de());
                break;

            case 0xD6: // SUB A, n
            	subTo(A, readPC());
                break;

            case 0xD7: // RST 0x10
            	rst(0x10);
                break;

            case 0xD8: // RET FC
            	if (carry) ret();
                break;

            case 0xD9: // RETI
            	ret();
            	enableInterrupts();
                break;

            case 0xDA: // JP FC, nn
            	if (carry) jp(readPC16());
                break;

            case 0xDB: // 0xDB - Illegal
            	throw new IllegalOperationException(opcode);
                
            case 0xDC: // CALL FC, nn
            	if (carry) call(readPC16());
                break;

            case 0xDD: // 0xDD - Illegal
            	throw new IllegalOperationException(opcode);
                
            case 0xDE: // SBC A, n
            	sbcTo(A, readPC());
                break;

            case 0xDF: // RST 0x18
            	rst(0x18);
                break;

            case 0xE0: // LDH (n), A
            	// Put A into memory address 0xFF00 + n ?
            	ram.write(0xFF00 + readPC(), regs[A]);
                break;

            case 0xE1: // POP HL
            	setHL(pop());
                break;

            case 0xE2: // LD (0xFF00 + C), A
            	// why 0xFF00 + C?
            	ram.write(0xFF00 + regs[C], regs[A]);
                break;

            case 0xE3: // 0xE3 - Illegal
            case 0xE4: // 0xE4 - Illegal
            	throw new IllegalOperationException(opcode);
                
            case 0xE5: // PUSH HL
            	push(hl());
                break;

            case 0xE6: // AND n
            	andTo(readPC());
                break;

            case 0xE7: // RST 0x20
            	rst(0x20);
                break;

            case 0xE8: // ADD SP, n
            	addSP(readPC());
                break;

            case 0xE9: // JP (HL)
            	jp(ram.read16(hl()));
                break;

            case 0xEA: // LD (nn), A
            	ram.write(readPC16(), regs[A]);
                break;

            case 0xEB: // 0xEB - Illegal
            case 0xEC: // 0xEC - Illegal
            case 0xED: // 0xED - Illegal
            	throw new IllegalOperationException(opcode);
                
            case 0xEE: // XOR n
            	xorTo(readPC());
                break;

            case 0xEF: // RST 0x28
            	rst(0x28);
                break;

            case 0xF0: // LDH A, (0xFF00 + n)
            	// Set A to value at (0xFF00 + n)
            	regs[A] = ram.read(0xFF00 + readPC());
                break;

            case 0xF1: // POP AF
            	setAF(pop());
                break;

            case 0xF2: // LD A, (0xFF00 + C)
            	regs[A] = ram.read(0xFF00 + regs[C]);
                break;

            case 0xF3: // DI
            	disableInterrupts();
                break;

            case 0xF4: // 0xF4 - Illegal
            	throw new IllegalOperationException(opcode);

            case 0xF5: // PUSH AF
            	push(af());
                break;

            case 0xF6: // OR n
            	orTo(readPC());
                break;

            case 0xF7: // RST 0x30
            	rst(0x30);
                break;

            case 0xF8: // LDHL SP, n
            	// Add SP + n, store result in HL
            	setHLAfterAdd(sp, readPC()); // wow this is ugly
                break;

            case 0xF9: // LD SP, HL
            	setSP(hl());
                break;

            case 0xFA: // LD A, (nn)
            	regs[A] = ram.read(readPC16());
                break;

            case 0xFB: // EI
            	enableInterrupts();
                break;

            case 0xFC: // 0xFC - Illegal
            case 0xFD: // 0xFD - Illegal
            	throw new IllegalOperationException(opcode);

            case 0xFE: // CP n
            	cpTo(readPC());
                break;

            case 0xFF: // RST 0x38
            	rst(0x38);
            }

            // counter -= CYCLES[opc];
        }
    }
	
    public class IllegalOperationException extends Exception {
		private static final long serialVersionUID = 8646636447363934844L;

		public IllegalOperationException(int opcode) {
    		super("Invalid opcode: " + opcode);
    	}
	}
}
