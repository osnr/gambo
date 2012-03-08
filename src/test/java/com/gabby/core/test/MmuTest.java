package com.gabby.core.test;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gabby.core.Mmu;

public class MmuTest {
	private Mmu easyMmu;
	
	@Before
	public void setUp() {
		easyMmu = new Mmu(ByteBuffer.allocate(0x20000));
	}
	
	@Test
	public void testRead() {
		easyMmu.getCartridge().put(0x0100, (byte) 0xEF);
		Assert.assertEquals(0xEF, easyMmu.read(0x100));
	}

	@Test
	public void testRead16() {
		easyMmu.getCartridge().putShort(0xF000, (short) 0xFECD);
		Assert.assertEquals(0xFECD, easyMmu.read16(0xF000));
		
		easyMmu.getCartridge().put(0xEF01, (byte) 0xEF);
		easyMmu.getCartridge().put(0xEF00, (byte) 0xAB);
		Assert.assertEquals(0xEFAB, easyMmu.read16(0xEF00));
	}

	@Test
	public void testWriteIntInt() {
		easyMmu.write(0xFD00, 0xEC);
		Assert.assertEquals(0xEC, easyMmu.getCartridge().get(0xFD00) & 0xFF); 
	}

	@Test
	public void testWriteIntIntInt() {
		easyMmu.write(0xDF00, 0xF3, 0x2C);
		Assert.assertEquals(0xF3, easyMmu.getCartridge().get(0xDF01) & 0xFF);
		Assert.assertEquals(0x2C, easyMmu.getCartridge().get(0xDF00) & 0xFF);
		Assert.assertEquals(0xF32C, easyMmu.getCartridge().getShort(0xDF00) & 0xFFFF);
	}

	@Test
	public void testWrite16() {
		easyMmu.write16(0xFFF0, 0xFECF);
		Assert.assertEquals(0xFECF, easyMmu.getCartridge().getShort(0xFFF0) & 0xFFFF);
		Assert.assertEquals(0xFE, easyMmu.getCartridge().get(0xFFF1) & 0xFF);
		Assert.assertEquals(0xCF, easyMmu.getCartridge().get(0xFFF0) & 0xFF);
	}

}
