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