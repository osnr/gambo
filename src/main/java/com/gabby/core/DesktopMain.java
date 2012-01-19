package com.gabby.core;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Timer;
import java.util.TimerTask;

import com.gabby.loader.*;

class DesktopMain extends Canvas implements ActionListener {
    Display display;
    final Ram ram;
    final Cpu cpu;
    final BufferedImage buffer;
    
    public DesktopMain() {
        ram = new Ram();
        cpu = new Cpu(ram);
        display = new Display();
        buffer = new BufferedImage(160, 144, BufferedImage.TYPE_INT_RGB);
        addKeyListener(new DesktopInput(ram, cpu));
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g = (Graphics2D) graphics;
        BufferedImage secondBuffer = new BufferedImage(160, 144, BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = secondBuffer.createGraphics();
        bg.clearRect(0, 0, 160, 144);
        //g.clearRect(0, 0, 160, 144);

        display.draw(ram, buffer.createGraphics());
        for (int y = 0; y < 144; y++) {
            for (int x = 0; x < 160; x++) {
                bg.setPaint(new Color(buffer.getRGB(x, y)));
                bg.drawLine(x, y, x, y);
                ram.getMemory().put(Ram.LY, (byte) x);
            }
        }

        bg.dispose();
        g.drawImage(secondBuffer, null, 0, 0);

        cpu.setInterrupt(Cpu.VBLANK);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if ("load rom".equals(e.getActionCommand())) {
                JFileChooser fc = new JFileChooser();

                int ret = fc.showOpenDialog(this);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    Rom rom = RomLoader.loadGameBoyRom(fc.getSelectedFile());

                    ram.getMemory().clear();
                    ram.getMemory().put(rom.getRom().array());
                    ram.getMemory().rewind();
                    
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
            } else if ("save state".equals(e.getActionCommand())) {
                JFileChooser fc = new JFileChooser();

                int ret = fc.showOpenDialog(this);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    FileOutputStream out = new FileOutputStream(f);
                    out.write(ram.getMemory().array());
                    out.write(cpu.a());
                    out.write(cpu.b());
                    out.write(cpu.c());
                    out.write(cpu.d());
                    out.write(cpu.e());
                    out.write(cpu.f());
                    out.write(cpu.h());
                    out.write(cpu.l());
                    out.write(cpu.sp());
                    out.write(cpu.getZero() ? 1 : 0);
                    out.write(cpu.getSubtract() ? 1 : 0);
                    out.write(cpu.getHalfCarry() ? 1 : 0);
                    out.write(cpu.getCarry() ? 1 : 0);
                    out.write(cpu.getPc());
                    out.write(cpu.getCounter());
                    out.close();
                }
            } else if ("load state".equals(e.getActionCommand())) {
                JFileChooser fc = new JFileChooser();

                int ret = fc.showOpenDialog(this);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    FileInputStream in = new FileInputStream(f);
                    byte[] b = new byte[Ram.MEMORY_SIZE];
                    in.read(b);
                    ram.getMemory().clear();
                    ram.getMemory().put(b);
                    ram.getMemory().rewind();
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
                    cpu.setCounter(in.read());
                    in.close();
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gabby");
        final DesktopMain dm = new DesktopMain();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(dm);
        frame.setSize(160, 144);
        frame.setLocationRelativeTo(null);
        frame.setIgnoreRepaint(true);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem loadRom = new JMenuItem("Load ROM");
        loadRom.addActionListener(dm);
        loadRom.setActionCommand("load rom");

        JMenuItem saveState = new JMenuItem("Save State");
        saveState.addActionListener(dm);
        saveState.setActionCommand("save state");

        JMenuItem loadState = new JMenuItem("Load State");
        loadState.addActionListener(dm);
        loadState.setActionCommand("load state");

        fileMenu.add(loadRom);
        fileMenu.addSeparator();
        fileMenu.add(saveState);
        fileMenu.add(loadState);
        
        frame.setJMenuBar(menuBar);
        frame.setVisible(true);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dm.repaint();
            }});
    }
}
