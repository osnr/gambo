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
import java.awt.EventQueue;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Main {
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
                //frame.setSize(160, 164);
                frame.setLocationRelativeTo(null);
                frame.setIgnoreRepaint(true);
                frame.setResizable(false);
                frame.getContentPane().setPreferredSize(new Dimension(160, 144));

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
                
                JMenu optionsMenu = new JMenu("Options");
                menuBar.add(optionsMenu);
                
                JMenu screenSize = new JMenu("Screen Size");
                
                JMenuItem res160x144 = new JMenuItem("160x144");
                res160x144.addActionListener(emulator);
                res160x144.setActionCommand("change size");
                screenSize.add(res160x144);

                JMenuItem res320x288 = new JMenuItem("320x288");
                res320x288.addActionListener(emulator);
                res320x288.setActionCommand("change size");
                screenSize.add(res320x288);

                JMenuItem res640x576 = new JMenuItem("640x576");
                res640x576.addActionListener(emulator);
                res640x576.setActionCommand("change size");
                screenSize.add(res640x576);

                fileMenu.add(loadRom);
                fileMenu.addSeparator();
                fileMenu.add(saveState);
                fileMenu.add(loadState);
                
                optionsMenu.add(screenSize);

                frame.setJMenuBar(menuBar);
                frame.pack();
                frame.setVisible(true);

                emulator.processArguments(args);
                
                frame.addKeyListener(emulator.getInput());

                (new java.util.Timer()).scheduleAtFixedRate((new TimerTask() {
                    public void run() {
                        emulator.repaint();
                    }
                }), 0, 17);
            }
        });
    }
}