package com.gabby.core;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Canvas;
import java.awt.Color;
import javax.swing.SwingUtilities;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.nio.ByteBuffer;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;


class DesktopMain extends Canvas {
    Display display;
    Ram ram;

    public DesktopMain() {
        ram = new Ram();
        addKeyListener(new DesktopInput(ram));
    }
    
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g = (Graphics2D) graphics;
        display = new Display(g); // This could be nicer, but I am tired.
        display.draw(ram);
    }

    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gabby");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new DesktopMain());
        frame.setSize(160, 144);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setIgnoreRepaint(true);
    }
}
