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
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.TimerTask;

import javax.swing.*;

public class Main {
	public static void main(final String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Gabby");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String workingDirPath = System.getProperty("user.dir") + "/";

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

                JMenu loadRom = new JMenu("Load ROM");

                File romDir = new File(workingDirPath);
                String[] romNames = romDir.list();

                boolean listed = false;
                
                for (int i = 0; i < romNames.length; i++) {
                    if (romNames[i].toLowerCase().endsWith(".gb")) {
                        JMenuItem rom = new JMenuItem(romNames[i]);
                        rom.setAction(new RomMenuAction(romNames[i], emulator, workingDirPath + romNames[i]));
                        loadRom.add(rom);
                        listed = true;
                    }
                }

                if (!listed)
                    loadRom.add(new JMenuItem("No ROMs Found"));
                loadRom.addSeparator();

                JMenuItem loadOtherRom = new JMenuItem("Load Other ROM");
                loadOtherRom.setActionCommand("load rom");

                loadRom.add(loadOtherRom);

                JMenuItem saveState = new JMenuItem("Save State");
                saveState.addActionListener(emulator);
                saveState.setActionCommand("save state");

                JMenuItem loadState = new JMenuItem("Load State");
                loadState.addActionListener(emulator);
                loadState.setActionCommand("load state");
                
                JMenu romMenu = new JMenu("ROM");
                menuBar.add(romMenu);
                
                JMenuItem stop = new JMenuItem("Stop");
                stop.addActionListener(emulator);
                stop.setActionCommand("stop");

                romMenu.add(stop);
                
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
            }
        });
    }

    private static class RomMenuAction extends AbstractAction {
        private Emulator emulator;
        private String romPath;
        
        RomMenuAction(String name, Emulator emulator, String romPath) {
            super(name);
            
            this.emulator = emulator;
            this.romPath = romPath;
        }
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            emulator.loadRom(new File(romPath));
        }
    }
}