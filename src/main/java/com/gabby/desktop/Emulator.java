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
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.gabby.core.Mmu;

public class Emulator extends JComponent implements ActionListener {
    private static final long serialVersionUID = 458596725723358140L;

    private DesktopDisplay display;
    private Mmu mmu;
    private DesktopCpu cpu;
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
        if ("load rom".equals(e.getActionCommand())) {
            JFileChooser fc = new JFileChooser();

            int ret = fc.showOpenDialog(this);

            if (ret == JFileChooser.APPROVE_OPTION) {
                loadRom(fc.getSelectedFile());
            }
        } else if ("save state".equals(e.getActionCommand())) {
            JFileChooser fc = new JFileChooser();
            fc.setApproveButtonText("Save");

            int ret = fc.showOpenDialog(this);

            if (ret == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();

/*                    BufferedWriter out = new BufferedWriter(new FileWriter(f));

                    /*out.writeInt(cpu.a());       // 0
                    p = out.getFilePointer();
                    out.writeInt(cpu.b());       // 3
                    p = out.getFilePointer();
                    out.writeInt(cpu.c());       // 0
                    p = out.getFilePointer();
                    out.writeInt(cpu.d());       // 255
                    p = out.getFilePointer();
                    out.writeInt(cpu.e());      // 138
                    p = out.getFilePointer();
                    out.writeInt(cpu.f());      // 64
                    p = out.getFilePointer();
                    out.writeInt(cpu.h());      // 194
                    p = out.getFilePointer();
                    out.writeInt(cpu.l());      // 5
                    p = out.getFilePointer();
                    out.writeInt(cpu.sp());     // 53247
                    p = out.getFilePointer();
                    out.writeInt(cpu.isZero() ? 1 : 0); // 1
                    p = out.getFilePointer();
                    out.writeInt(cpu.isSubtract() ? 1 : 0);  // 0
                    p = out.getFilePointer();
                    out.writeInt(cpu.isHalfCarry() ? 1 : 0); // 1
                    p = out.getFilePointer();
                    out.writeInt(cpu.isCarry() ? 1 : 0); // 0
                    p = out.getFilePointer();
                    out.writeInt(cpu.getPc()); // 752
                    p = out.getFilePointer();

                    int size = mmu.getAllMemory().length;


                    out.writeInt(size);
                    out.write(mmu.getAllMemory());*//*
                    
                    String s = "";
                    
                    s += cpu.a();
                    s += " ";
                    s += cpu.b();
                    s += " ";
                    s += cpu.c();
                    s += " ";
                    s += cpu.d();
                    s += " ";
                    s += cpu.e();
                    s += " ";
                    s += cpu.f();
                    s += " ";
                    s += cpu.h();
                    s += " ";
                    s += cpu.l();
                    s += " ";
                    s += cpu.sp();
                    s += " ";
                    s += (cpu.isZero() ? 1 : 0);
                    s += " ";
                    s += (cpu.isSubtract() ? 1 : 0);
                    s += " ";
                    s += (cpu.isHalfCarry() ? 1 : 0);
                    s += " ";
                    s += (cpu.isCarry() ? 1 : 0);
                    s += " ";
                    s += cpu.getPc();
                    
                    byte[] b = mmu.getAllMemory();
                    
                    for (int i = 0; i < b.length; i++)
                        s += " " + b[i];
                    
                    out.write(s);
                    
                    out.close();  */

                cpu.saveState(f.getPath());
            }
        } else if ("load state".equals(e.getActionCommand())) {
            JFileChooser fc = new JFileChooser();

            fc.setApproveButtonText("Load");
            int ret = fc.showOpenDialog(this);

            if (ret == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                cpu.loadState(f.getPath());

                /*Scanner in = new Scanner(f);

              this.display = new DesktopDisplay(mmu, this);
              this.input = new DesktopInput(mmu);
              this.cpu = new DesktopCpu(mmu, display);

              cpu.setA(in.readInt());
              p = in.getFilePointer();
              cpu.setB(in.readInt());
              p = in.getFilePointer();
              cpu.setC(in.readInt());
              p = in.getFilePointer();
              cpu.setD(in.readInt());
              p = in.getFilePointer();
              cpu.setE(in.readInt());
              p = in.getFilePointer();
              cpu.setF(in.readInt());
              p = in.getFilePointer();
              cpu.setH(in.readInt());
              p = in.getFilePointer();
              cpu.setL(in.readInt());
              p = in.getFilePointer();
              cpu.setSP(in.readInt());
              p = in.getFilePointer();
              cpu.setZero(in.readInt() == 1);
              p = in.getFilePointer();
              cpu.setSubtract(in.readInt() == 1);
              p = in.getFilePointer();
              cpu.setHalfCarry(in.readInt() == 1);
              p = in.getFilePointer();
              cpu.setCarry(in.readInt() == 1);
              p = in.getFilePointer();
              cpu.setPc(in.readInt());
              p = in.getFilePointer();*/

                /*cpu.setA(in.nextInt());
                cpu.setB(in.nextInt());
                cpu.setC(in.nextInt());
                cpu.setD(in.nextInt());
                cpu.setE(in.nextInt());
                cpu.setF(in.nextInt());
                cpu.setH(in.nextInt());
                cpu.setL(in.nextInt());
                cpu.setSP(in.nextInt());
                cpu.setZero(in.nextInt() == 1);
                cpu.setSubtract(in.nextInt() == 1);
                cpu.setHalfCarry(in.nextInt() == 1);
                cpu.setCarry(in.nextInt() == 1);
                cpu.setPc(in.nextInt());

                in.close();

                if (cpuThread != null)
                    cpuThread.interrupt();

                cpuThread = new Thread() {
                    public void run() {
                        try {
                            running = true;
                            System.out.println("running");
                            cpu.emulate(cpu.getPc());
                            running = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.err.println(String.format("Program counter: %x", cpu.getPc()));
                        }
                    }
                };

                cpuThread.start();*/
            }
        } else if ("change size".equals(e.getActionCommand())) {
            JMenuItem item = (JMenuItem) e.getSource();

            if ("160x144".equals(item.getText())) {
                //display.buffer = new BufferedImage(160, 144, BufferedImage.TYPE_INT_RGB);

                this.getParent().setPreferredSize(new Dimension(160, 144));
                SwingUtilities.getWindowAncestor(this).pack();
                scale = 1;
            } else if ("320x288".equals(item.getText())) {
                //display.buffer = new BufferedImage(320, 288, BufferedImage.TYPE_INT_RGB);

                this.getParent().setPreferredSize(new Dimension(320, 288));
                SwingUtilities.getWindowAncestor(this).pack();
                scale = 2;
            } else if ("640x576".equals(item.getText())) {
                //display.buffer = new BufferedImage(640, 576, BufferedImage.TYPE_INT_RGB);

                this.getParent().setPreferredSize(new Dimension(640, 576));
                SwingUtilities.getWindowAncestor(this).pack();
                scale = 4;
            }
        } else if ("stop".equals(e.getActionCommand())) {
            cpuThread.interrupt();
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
