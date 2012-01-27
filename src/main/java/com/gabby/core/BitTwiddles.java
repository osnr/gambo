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

import java.awt.Color;

public class BitTwiddles {


    /**
     * @param b The byte to unsign.
     * @return The unsigned number in the form of an int, because Java is dumb.
     */
    public static int unsign(byte b) {
        if (b < 0)
            return b + 256;
        else
            return b;
    }


    /**
     * @param i The number of bits from the left to return (0 - 7)
     * @param b The byte
     * @return The bit as an int. If i is not in the range 0 to 7, then -1 is returned.
     */
    public static int getBit(int i, byte b) {
        switch (i) {
        case 0:
            return (b >> 7) & 0x1;
        case 1:
            return (b >> 6) & 0x1;
        case 2:
            return (b >> 5) & 0x1;
        case 3:
            return (b >> 4) & 0x1;
        case 4:
            return (b >> 3) & 0x1;
        case 5:
            return (b >> 2) & 0x1;
        case 6:
            return (b >> 1) & 0x1;
        case 7:
            return b & 0x1;
        }
		
        return -1;
    }

    /**
     * @param i The number of bits from the left to return (0 - 7)
     * @param b The byte
     * @return The bit as an int. If i is not in the range 0 to 7, then -1 is returned.
     */
    public static int getBit(int i, int b) {
        return getBit(i, b & 0xFF);
    }

    /** 
     * @param i The distance from the significant bit to the target bit (from 0 to 7).
     * @param second The first byte.
     * @param first The second byte.
     * @return The color for the pixel. If i is not within the specified range, then WHITE is returned.
     */
    public static Color getColorFromBytePair(int i, byte first, byte second) {

        int a = getBit(i, first);
        int b = getBit(i, second);

        // System.out.println(first + ", " + second + ", " + i);

        int c = (a << 1) + b;

        switch(c) {
        case 0:
            return Color.WHITE;
        case 1:
            return Color.LIGHT_GRAY;
        case 2:
            return Color.DARK_GRAY;
        case 3:
            return Color.BLACK;
        default:
            return Color.BLACK;
        }
    }

    /**
     * @param i The distance from the significant bit to the target bit (from 0 to 7).
     * @param second The first byte.
     * @param first The second byte.
     * @return The color for the pixel. If i is not within the specified range, then WHITE is returned.
     */
    public static Color getColorFromBytePair(int i, int first, int second) {
        return getColorFromBytePair(i, first & 0xFF, second & 0xFF);
    }
}
