package com.gabby.core;

public class BitTwiddles {

	public static int unsign(byte b) {
		if (b < 0)
			return b + 256;
		else
			return b;
	}

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
	 * @param i The distance from the significant bit to the target bit (from 0 to 7).
	 * @param second The first byte.
	 * @param first The second byte.
	 * @return The color code for the pixel. If i is not within the specified range, then -1 is returned.
	 */
	public static byte getColorFromBytePair(int i, byte first, byte second) {
		switch (i) {
		case 0:
			return (byte) (((second >> 6) & 0x2) | ((first >> 7) & 0x1));
		case 1:
			return (byte) ((((second >> 5) & 0x2) | (first >> 6) & 0x1));
		case 2:
			return (byte) ((((second >> 4) & 0x2) | (first >> 5) & 0x1));
		case 3:
			return (byte) ((((second >> 3) & 0x2) | (first >> 4) & 0x1));
		case 4:
			return (byte) ((((second >> 2) & 0x2) | (first >> 3) & 0x1));
		case 5:
			return (byte) ((((second >> 1) & 0x2) | (first >> 2) & 0x1));
		case 6:
			return (byte) ((((second & 0x2) | ((first >> 1) & 0x1))));
		case 7:
			return (byte) (((second << 1) & 0x2) | (first & 0x1));
		}
		
		return -1;
	}

}
