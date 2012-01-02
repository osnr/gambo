package com.gabby.core;

import javax.swing.*;
import java.io.*;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.nio.ByteBuffer;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


class DesktopMain extends Canvas implements ActionListener {
    Display display;
    Ram ram;
    Cpu cpu;

    public DesktopMain() {
        ram = new Ram();
        cpu = new Cpu();
        addKeyListener(new DesktopInput(ram));
    }
    
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g = (Graphics2D) graphics;
        display = new Display(g); // This could be nicer, but I am tired.
        display.draw(ram);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if ("load rom".equals(e.getActionCommand())) {
                // TODO: Load ROM
            } else if ("save state".equals(e.getActionCommand())) {
                JFileChooser fc = new JFileChooser();

                int ret = fc.showOpenDialog(this);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    FileOutputStream out = new FileOutputStream(f);
                    out.write(ram.getMemory().array());
                    out.write(cpu.getA());
                    out.write(cpu.getB());
                    out.write(cpu.getC());
                    out.write(cpu.getD());
                    out.write(cpu.getE());
                    out.write(cpu.getF());
                    out.write(cpu.getH());
                    out.write(cpu.getL());
                    out.write(cpu.getSp());
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
                    byte[] b = new byte[(int) Ram.MEMORY_SIZE];
                    in.read(b);
                    ram.getMemory().clear();
                    ram.getMemory().put(b);
                    cpu.setA(in.read());
                    cpu.setB(in.read());
                    cpu.setC(in.read());
                    cpu.setD(in.read());
                    cpu.setE(in.read());
                    cpu.setF(in.read());
                    cpu.setH(in.read());
                    cpu.setL(in.read());
                    cpu.setSp(in.read());
                    cpu.setZero((in.read() == 1) ? true : false);
                    cpu.setSubtract((in.read() == 1) ? true : false);
                    cpu.setHalfCarry((in.read() == 1) ? true : false);
                    cpu.setCarry((in.read() == 1) ? true : false);
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
        DesktopMain dm = new DesktopMain();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(dm);
        frame.setSize(160, 144);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
    }
}
