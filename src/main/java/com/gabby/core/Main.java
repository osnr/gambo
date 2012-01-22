package com.gabby.core;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.*;

public class Main implements Serializable {
    public static void main(final String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Gabby");
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Gabby");
                final Emulator emulator = new Emulator();

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(emulator);
                frame.setSize(160, 144);
                frame.setLocationRelativeTo(null);
                frame.setIgnoreRepaint(true);

                JMenuBar menuBar = new JMenuBar();
                JMenu fileMenu = new JMenu("File");
                menuBar.add(fileMenu);

                JMenuItem loadRom = new JMenuItem("Load ROM");
                loadRom.addActionListener(emulator);
                loadRom.setActionCommand("load rom");

                JMenuItem saveState = new JMenuItem("Save State");
                saveState.addActionListener(emulator);
                saveState.setActionCommand("save state");

                JMenuItem loadState = new JMenuItem("Load State");
                loadState.addActionListener(emulator);
                loadState.setActionCommand("load state");

                fileMenu.add(loadRom);
                fileMenu.addSeparator();
                fileMenu.add(saveState);
                fileMenu.add(loadState);

                frame.setJMenuBar(menuBar);
                frame.setVisible(true);

                emulator.processArguments(args);

                (new java.util.Timer()).scheduleAtFixedRate((new TimerTask() {
                    public void run() {
                        emulator.repaint();
                    }
                }), 0, 17);
            }
        });
    }
}