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

package com.gambo.core;

import java.awt.Color;

public class BitTwiddles {
	public static final int bx00000000 = 0;
	public static final int bx00000001 = 1;
	public static final int bx00000010 = 2;
	public static final int bx00000011 = 3;
	public static final int bx00000100 = 4;
	public static final int bx00000101 = 5;
	public static final int bx00000110 = 6;
	public static final int bx00000111 = 7;
	public static final int bx00001000 = 8;
	public static final int bx00001001 = 9;
	public static final int bx00001010 = 10;
	public static final int bx00001011 = 11;
	public static final int bx00001100 = 12;
	public static final int bx00001101 = 13;
	public static final int bx00001110 = 14;
	public static final int bx00001111 = 15;
	public static final int bx00010000 = 16;
	public static final int bx00010001 = 17;
	public static final int bx00010010 = 18;
	public static final int bx00010011 = 19;
	public static final int bx00010100 = 20;
	public static final int bx00010101 = 21;
	public static final int bx00010110 = 22;
	public static final int bx00010111 = 23;
	public static final int bx00011000 = 24;
	public static final int bx00011001 = 25;
	public static final int bx00011010 = 26;
	public static final int bx00011011 = 27;
	public static final int bx00011100 = 28;
	public static final int bx00011101 = 29;
	public static final int bx00011110 = 30;
	public static final int bx00011111 = 31;
	public static final int bx00100000 = 32;
	public static final int bx00100001 = 33;
	public static final int bx00100010 = 34;
	public static final int bx00100011 = 35;
	public static final int bx00100100 = 36;
	public static final int bx00100101 = 37;
	public static final int bx00100110 = 38;
	public static final int bx00100111 = 39;
	public static final int bx00101000 = 40;
	public static final int bx00101001 = 41;
	public static final int bx00101010 = 42;
	public static final int bx00101011 = 43;
	public static final int bx00101100 = 44;
	public static final int bx00101101 = 45;
	public static final int bx00101110 = 46;
	public static final int bx00101111 = 47;
	public static final int bx00110000 = 48;
	public static final int bx00110001 = 49;
	public static final int bx00110010 = 50;
	public static final int bx00110011 = 51;
	public static final int bx00110100 = 52;
	public static final int bx00110101 = 53;
	public static final int bx00110110 = 54;
	public static final int bx00110111 = 55;
	public static final int bx00111000 = 56;
	public static final int bx00111001 = 57;
	public static final int bx00111010 = 58;
	public static final int bx00111011 = 59;
	public static final int bx00111100 = 60;
	public static final int bx00111101 = 61;
	public static final int bx00111110 = 62;
	public static final int bx00111111 = 63;
	public static final int bx01000000 = 64;
	public static final int bx01000001 = 65;
	public static final int bx01000010 = 66;
	public static final int bx01000011 = 67;
	public static final int bx01000100 = 68;
	public static final int bx01000101 = 69;
	public static final int bx01000110 = 70;
	public static final int bx01000111 = 71;
	public static final int bx01001000 = 72;
	public static final int bx01001001 = 73;
	public static final int bx01001010 = 74;
	public static final int bx01001011 = 75;
	public static final int bx01001100 = 76;
	public static final int bx01001101 = 77;
	public static final int bx01001110 = 78;
	public static final int bx01001111 = 79;
	public static final int bx01010000 = 80;
	public static final int bx01010001 = 81;
	public static final int bx01010010 = 82;
	public static final int bx01010011 = 83;
	public static final int bx01010100 = 84;
	public static final int bx01010101 = 85;
	public static final int bx01010110 = 86;
	public static final int bx01010111 = 87;
	public static final int bx01011000 = 88;
	public static final int bx01011001 = 89;
	public static final int bx01011010 = 90;
	public static final int bx01011011 = 91;
	public static final int bx01011100 = 92;
	public static final int bx01011101 = 93;
	public static final int bx01011110 = 94;
	public static final int bx01011111 = 95;
	public static final int bx01100000 = 96;
	public static final int bx01100001 = 97;
	public static final int bx01100010 = 98;
	public static final int bx01100011 = 99;
	public static final int bx01100100 = 100;
	public static final int bx01100101 = 101;
	public static final int bx01100110 = 102;
	public static final int bx01100111 = 103;
	public static final int bx01101000 = 104;
	public static final int bx01101001 = 105;
	public static final int bx01101010 = 106;
	public static final int bx01101011 = 107;
	public static final int bx01101100 = 108;
	public static final int bx01101101 = 109;
	public static final int bx01101110 = 110;
	public static final int bx01101111 = 111;
	public static final int bx01110000 = 112;
	public static final int bx01110001 = 113;
	public static final int bx01110010 = 114;
	public static final int bx01110011 = 115;
	public static final int bx01110100 = 116;
	public static final int bx01110101 = 117;
	public static final int bx01110110 = 118;
	public static final int bx01110111 = 119;
	public static final int bx01111000 = 120;
	public static final int bx01111001 = 121;
	public static final int bx01111010 = 122;
	public static final int bx01111011 = 123;
	public static final int bx01111100 = 124;
	public static final int bx01111101 = 125;
	public static final int bx01111110 = 126;
	public static final int bx01111111 = 127;
	public static final int bx10000000 = 128;
	public static final int bx10000001 = 129;
	public static final int bx10000010 = 130;
	public static final int bx10000011 = 131;
	public static final int bx10000100 = 132;
	public static final int bx10000101 = 133;
	public static final int bx10000110 = 134;
	public static final int bx10000111 = 135;
	public static final int bx10001000 = 136;
	public static final int bx10001001 = 137;
	public static final int bx10001010 = 138;
	public static final int bx10001011 = 139;
	public static final int bx10001100 = 140;
	public static final int bx10001101 = 141;
	public static final int bx10001110 = 142;
	public static final int bx10001111 = 143;
	public static final int bx10010000 = 144;
	public static final int bx10010001 = 145;
	public static final int bx10010010 = 146;
	public static final int bx10010011 = 147;
	public static final int bx10010100 = 148;
	public static final int bx10010101 = 149;
	public static final int bx10010110 = 150;
	public static final int bx10010111 = 151;
	public static final int bx10011000 = 152;
	public static final int bx10011001 = 153;
	public static final int bx10011010 = 154;
	public static final int bx10011011 = 155;
	public static final int bx10011100 = 156;
	public static final int bx10011101 = 157;
	public static final int bx10011110 = 158;
	public static final int bx10011111 = 159;
	public static final int bx10100000 = 160;
	public static final int bx10100001 = 161;
	public static final int bx10100010 = 162;
	public static final int bx10100011 = 163;
	public static final int bx10100100 = 164;
	public static final int bx10100101 = 165;
	public static final int bx10100110 = 166;
	public static final int bx10100111 = 167;
	public static final int bx10101000 = 168;
	public static final int bx10101001 = 169;
	public static final int bx10101010 = 170;
	public static final int bx10101011 = 171;
	public static final int bx10101100 = 172;
	public static final int bx10101101 = 173;
	public static final int bx10101110 = 174;
	public static final int bx10101111 = 175;
	public static final int bx10110000 = 176;
	public static final int bx10110001 = 177;
	public static final int bx10110010 = 178;
	public static final int bx10110011 = 179;
	public static final int bx10110100 = 180;
	public static final int bx10110101 = 181;
	public static final int bx10110110 = 182;
	public static final int bx10110111 = 183;
	public static final int bx10111000 = 184;
	public static final int bx10111001 = 185;
	public static final int bx10111010 = 186;
	public static final int bx10111011 = 187;
	public static final int bx10111100 = 188;
	public static final int bx10111101 = 189;
	public static final int bx10111110 = 190;
	public static final int bx10111111 = 191;
	public static final int bx11000000 = 192;
	public static final int bx11000001 = 193;
	public static final int bx11000010 = 194;
	public static final int bx11000011 = 195;
	public static final int bx11000100 = 196;
	public static final int bx11000101 = 197;
	public static final int bx11000110 = 198;
	public static final int bx11000111 = 199;
	public static final int bx11001000 = 200;
	public static final int bx11001001 = 201;
	public static final int bx11001010 = 202;
	public static final int bx11001011 = 203;
	public static final int bx11001100 = 204;
	public static final int bx11001101 = 205;
	public static final int bx11001110 = 206;
	public static final int bx11001111 = 207;
	public static final int bx11010000 = 208;
	public static final int bx11010001 = 209;
	public static final int bx11010010 = 210;
	public static final int bx11010011 = 211;
	public static final int bx11010100 = 212;
	public static final int bx11010101 = 213;
	public static final int bx11010110 = 214;
	public static final int bx11010111 = 215;
	public static final int bx11011000 = 216;
	public static final int bx11011001 = 217;
	public static final int bx11011010 = 218;
	public static final int bx11011011 = 219;
	public static final int bx11011100 = 220;
	public static final int bx11011101 = 221;
	public static final int bx11011110 = 222;
	public static final int bx11011111 = 223;
	public static final int bx11100000 = 224;
	public static final int bx11100001 = 225;
	public static final int bx11100010 = 226;
	public static final int bx11100011 = 227;
	public static final int bx11100100 = 228;
	public static final int bx11100101 = 229;
	public static final int bx11100110 = 230;
	public static final int bx11100111 = 231;
	public static final int bx11101000 = 232;
	public static final int bx11101001 = 233;
	public static final int bx11101010 = 234;
	public static final int bx11101011 = 235;
	public static final int bx11101100 = 236;
	public static final int bx11101101 = 237;
	public static final int bx11101110 = 238;
	public static final int bx11101111 = 239;
	public static final int bx11110000 = 240;
	public static final int bx11110001 = 241;
	public static final int bx11110010 = 242;
	public static final int bx11110011 = 243;
	public static final int bx11110100 = 244;
	public static final int bx11110101 = 245;
	public static final int bx11110110 = 246;
	public static final int bx11110111 = 247;
	public static final int bx11111000 = 248;
	public static final int bx11111001 = 249;
	public static final int bx11111010 = 250;
	public static final int bx11111011 = 251;
	public static final int bx11111100 = 252;
	public static final int bx11111101 = 253;
	public static final int bx11111110 = 254;
	public static final int bx11111111 = 255;

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
        return getBit(i, (byte) (b & 0xFF));
    }
    
    public static int toUnsignedByte(int i) {
        if (i < 0) {
            return 256 + i;
        } else {
            return i;
        }
    }
    
    public static int toSignedByte(int i) {
        if (i > 127) {
            return -128 + (i - 128);
        } else {
            return i;
        }
    }
}
