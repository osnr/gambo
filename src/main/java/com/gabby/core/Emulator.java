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

package com.gabby.core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.gabby.loader.*;

class Emulator extends JComponent implements ActionListener {
    Display display;
    private Mmu mmu;
    private Cpu cpu;
    public BufferedImage buffer;
    private DesktopInput input;

    public Emulator() {
    	this.mmu = new Mmu();
    	
        this.display = new Display(mmu, this);
        this.input = new DesktopInput(mmu);
        
        this.cpu = new Cpu(mmu, display);
        
        buffer = new BufferedImage(160, 144, BufferedImage.TYPE_INT_RGB);
    }

    public void bufferFromBuffer(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        buffer = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        
        Graphics2D g = (Graphics2D) graphics;
        g.drawImage(buffer, null, 0, 0);
    }
    
    public void loadRom(File f) {
        Rom rom = RomLoader.loadGameBoyRom(f);
        System.out.println("Loaded: " + rom.getTitle());
        mmu.getMemory().clear();
        mmu.getMemory().put(rom.getRom().array());
        mmu.getMemory().rewind();

        (new Thread() {
            public void run() {
                try {
                    cpu.emulate(0x100);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(String.format("Program counter: %x", cpu.getPc()));
                }
            }
        }).start();
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if ("load rom".equals(e.getActionCommand())) {
                JFileChooser fc = new JFileChooser();

                int ret = fc.showOpenDialog(this);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    loadRom(fc.getSelectedFile());
                }
            } else if ("save state".equals(e.getActionCommand())) {
                JFileChooser fc = new JFileChooser();

                int ret = fc.showOpenDialog(this);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    FileOutputStream out = new FileOutputStream(f);
                    out.write(mmu.getMemory().array());
                    out.write(cpu.a());
                    out.write(cpu.b());
                    out.write(cpu.c());
                    out.write(cpu.d());
                    out.write(cpu.e());
                    out.write(cpu.f());
                    out.write(cpu.h());
                    out.write(cpu.l());
                    out.write(cpu.sp());
                    out.write(cpu.isZero() ? 1 : 0);
                    out.write(cpu.isSubtract() ? 1 : 0);
                    out.write(cpu.isHalfCarry() ? 1 : 0);
                    out.write(cpu.isCarry() ? 1 : 0);
                    out.write(cpu.getPc());
                    // out.write(cpu.getCounter());
                    out.close();
                }
            } else if ("load state".equals(e.getActionCommand())) {
                JFileChooser fc = new JFileChooser();

                int ret = fc.showOpenDialog(this);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    FileInputStream in = new FileInputStream(f);
                    byte[] b = new byte[Mmu.MEMORY_SIZE];
                    in.read(b);
                    mmu.getMemory().clear();
                    mmu.getMemory().put(b);
                    mmu.getMemory().rewind();
                    cpu.setA(in.read());
                    cpu.setB(in.read());
                    cpu.setC(in.read());
                    cpu.setD(in.read());
                    cpu.setE(in.read());
                    cpu.setF(in.read());
                    cpu.setH(in.read());
                    cpu.setL(in.read());
                    cpu.setSP(in.read());
                    cpu.setZero(in.read() == 1);
                    cpu.setSubtract(in.read() == 1);
                    cpu.setHalfCarry(in.read() == 1);
                    cpu.setCarry(in.read() == 1);
                    cpu.setPc(in.read());
                    // cpu.setCounter(in.read());
                    in.close();
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void processArguments(String[] args) {
        if (args.length > 0) {
            loadRom(new File(args[0]));
        }
    }

	protected void setInput(DesktopInput input) {
		this.input = input;
	}

	protected DesktopInput getInput() {
		return input;
	}
}
