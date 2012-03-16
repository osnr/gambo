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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.gabby.core.Cpu;
import com.gabby.core.Mmu;
import com.gabby.web.util.ByteBuffer;

public class Emulator extends JComponent implements ActionListener {
	private static final long serialVersionUID = 458596725723358140L;
	
	private DesktopDisplay display;
    private Mmu mmu;
    private Cpu cpu;
    private int scale;
    public BufferedImage buffer;
    private DesktopInput input;
    private Thread cpuThread;
    private boolean running; 

    public Emulator() {
        buffer = new BufferedImage(160, 144, BufferedImage.TYPE_INT_RGB);
        running = false;
        scale = 1;
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        if (running) {
            Graphics2D g = (Graphics2D) graphics;

            g.scale((double) scale, (double) scale);
            g.drawImage(buffer, null, 0, 0);
        }
    }

    public void loadRom(File f) {
    	byte[] buf;
        try {
            FileInputStream in = new FileInputStream(f);
            int size = (int) f.length();
            buf = new byte[size];
            
            in.read(buf);
            
            this.mmu = new Mmu(buf);
        	
            this.display = new DesktopDisplay(mmu, this);
            this.input = new DesktopInput(mmu);
            
            this.cpu = new DesktopCpu(mmu, display);
            
            cpuThread = new Thread() {
                public void run() {
                    try {
                        running = true;
                        cpu.emulate(0x100);
                        running = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println(String.format("Program counter: %x", cpu.getPc()));
                    }
                }
            };
            
            cpuThread.start();            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    out.write(mmu.getRom().array());
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

                    out.write(mmu.getAllMemory());
                    
                    out.close();
                }
            } else if ("load state".equals(e.getActionCommand())) {
                JFileChooser fc = new JFileChooser();

                fc.setApproveButtonText("Load");
                int ret = fc.showOpenDialog(this);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    FileInputStream in = new FileInputStream(f);
                    byte[] b = new byte[Mmu.MEMORY_SIZE];
                    in.read(b);
                    mmu = new Mmu(b);
                    this.display = new DesktopDisplay(mmu, this);
                    this.input = new DesktopInput(mmu);
                    this.cpu = new DesktopCpu(mmu, display);
                    mmu.getRom().clear();
                    mmu.getRom().put(b);
                    mmu.getRom().rewind();
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

                    for (int i = 0; i < 0x10000; i++)
                        mmu.write(in.read(), i);
                    
                    in.close();

                    cpuThread = new Thread() {
                        public void run() {
                            try {
                                running = true;
                                cpu.emulate(0x100);
                                running = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.err.println(String.format("Program counter: %x", cpu.getPc()));
                            }
                        }
                    };

                    cpuThread.start();
                }
            } else if ("change size".equals(e.getActionCommand())) {
                JMenuItem item = (JMenuItem) e.getSource();
                
                if ("160x144".equals(item.getText())) {
                    display.buffer = new BufferedImage(160, 144, BufferedImage.TYPE_INT_RGB);

                    this.getParent().setPreferredSize(new Dimension(160, 144));
                    SwingUtilities.getWindowAncestor(this).pack();
                    scale = 1;
                } else if ("320x288".equals(item.getText())) {
                    display.buffer = new BufferedImage(320, 288, BufferedImage.TYPE_INT_RGB);

                    this.getParent().setPreferredSize(new Dimension(320, 288));
                    SwingUtilities.getWindowAncestor(this).pack();
                    scale = 2;
                } else if ("640x576".equals(item.getText())) {
                    display.buffer = new BufferedImage(640, 576, BufferedImage.TYPE_INT_RGB);

                    this.getParent().setPreferredSize(new Dimension(640, 576));
                    SwingUtilities.getWindowAncestor(this).pack();
                    scale = 4;
                }
            } else if ("stop".equals(e.getActionCommand())) {
                cpuThread.interrupt();
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
