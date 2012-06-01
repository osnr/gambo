package com.gabby.core;

import java.io.*;

public class SaveState implements Serializable {
    public int a, b, c, d, e, f, h, l;
    public int pc, sp;
    public byte[] memory;
    public int divCounter, timaCounter;
    public int mode, line, lineCounter;
    public int buttons, dpad;

    public SaveState() {
        memory = new byte[Mmu.MEMORY_SIZE];
    }

    public SaveState(Cpu cpu, Mmu mmu, Display display) {
        this.a = cpu.a();
        this.b = cpu.b();
        this.c = cpu.c();
        this.d = cpu.d();
        this.e = cpu.e();
        this.f = cpu.f();
        this.h = cpu.h();
        this.l = cpu.l();

        this.pc = cpu.getPc();
        this.sp = cpu.sp();

        this.memory = mmu.getAllMemory();

        this.divCounter = mmu.timers.getDivCounter();
        this.timaCounter = mmu.timers.getTimaCounter();
        
        this.mode = display.getMode();
        this.line = display.getLine();
        this.lineCounter = display.getLineCounter();

        this.buttons = mmu.inputs.getButtons();
        this.dpad = mmu.inputs.getDpad();
    }

    public void copyFrom(SaveState s) {
        this.a = s.a;
        this.b = s.b;
        this.c = s.c;
        this.d = s.d;
        this.e = s.e;
        this.f = s.f;
        this.h = s.h;
        this.l = s.l;

        this.pc = s.pc;
        this.sp = s.sp;

        this.memory = s.memory.clone(); // is clone needed?

        this.timaCounter = s.timaCounter;
        this.divCounter = s.divCounter;

        this.mode = s.mode;
        this.line = s.line;
        this.lineCounter = s.lineCounter;

        this.buttons = s.buttons;
        this.dpad = s.dpad;
    }

    public void writeToFile(String path) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject(this);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void readFromFile(String path) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            copyFrom((SaveState) in.readObject());
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
