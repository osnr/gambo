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

package com.gabby.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RomLoader {
    public static Rom loadGameBoyRom(File f) {
        Rom r = new Rom();
        try {
            FileInputStream in = new FileInputStream(f);
            int size = (int) f.length();
            byte[] buf = new byte[size];
			
            in.read(buf);
            r.getEntry().put(buf, 0x0100, Rom.ENTRY_LENGTH);
            r.getLogo().put(buf, 0x0104, Rom.LOGO_LENGTH);
            r.getTitle().put(buf, 0x0134, Rom.TITLE_LENGTH);
            r.getNewLicenseeCode().put(buf, 0x0144, Rom.NEW_LICENSEE_CODE_LENGTH);
            r.setSgbFlag(buf[0x0146]);
            r.setCartidgeType(buf[0x0147]);
            r.setRomSize(buf[0x0148]);
            r.setRamSize(buf[0x0149]);
            r.setDestinationCode(buf[0x014A]);
            r.setOldLicenseeCode(buf[0x014B]);
            r.setMaskRomVersionNumber(buf[0x014C]);
            r.setHeaderChecksum(buf[0x014D]);
            r.getGlobalChecksum().put(buf, 0x014E, Rom.GLOBAL_CHECKSUM_LENGTH);
            r.getRom().put(buf);
            r.getRom().rewind();
            
            System.out.println("Loading game: " + new String(r.getTitle().array()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
                
        return r;
    }
}
