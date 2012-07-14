package com.gambo.core.test;
import org.junit.Test;
import org.junit.Assert;

import com.gambo.core.BitTwiddles;

import java.awt.Color;

public class BitTwiddlesTest {

    @Test
    public void testUnsign() {
        Assert.assertEquals(255, BitTwiddles.unsign((byte) -1));
    }

    @Test
    public void testGetBit() {
        byte b = 0x55;
        int[] actual = new int[8];

        for (int i = 0; i < 8; i++)
            actual[i] = BitTwiddles.getBit(i, b);
        int[] expected = {0, 1, 0, 1, 0, 1, 0, 1};
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testToUnisgnedByte() {
        int a = BitTwiddles.toUnsignedByte(-16);
        int b = BitTwiddles.toUnsignedByte(16);

        Assert.assertEquals(240, a);
        Assert.assertEquals(16, b);
    }

    @Test
    public void testToSignedByte() {
        int a = BitTwiddles.toSignedByte(128);
        int b = BitTwiddles.toSignedByte(127);

        Assert.assertEquals(-128, a);
        Assert.assertEquals(127, b);
    }
}
