package com.gabby.core.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gabby.core.Ram;

public class RamTest {
	private Ram easyRam;
	
	@Before
	public void setUp() {
		easyRam = new Ram();
	}
	
	@Test
	public void testRead() {
		easyRam.getMemory().put(0x0100, (byte) 0xEF);
		Assert.assertEquals(0xEF, easyRam.read(0x100));
	}

	@Test
	public void testRead16() {
		easyRam.getMemory().putShort(0xF000, (short) 0xFECD);
		Assert.assertEquals(0xFECD, easyRam.read16(0xF000));
		
		easyRam.getMemory().put(0xEF01, (byte) 0xEF);
		easyRam.getMemory().put(0xEF00, (byte) 0xAB);
		Assert.assertEquals(0xEFAB, easyRam.read16(0xEF00));
	}

	@Test
	public void testWriteIntInt() {
		easyRam.write(0xFD00, 0xEC);
		Assert.assertEquals(0xEC, easyRam.getMemory().get(0xFD00) & 0xFF); 
	}

	@Test
	public void testWriteIntIntInt() {
		easyRam.write(0xDF00, 0xF3, 0x2C);
		Assert.assertEquals(0xF3, easyRam.getMemory().get(0xDF01) & 0xFF);
		Assert.assertEquals(0x2C, easyRam.getMemory().get(0xDF00) & 0xFF);
		Assert.assertEquals(0xF32C, easyRam.getMemory().getShort(0xDF00) & 0xFFFF);
	}

	@Test
	public void testWrite16() {
		easyRam.write16(0xFFF0, 0xFECF);
		Assert.assertEquals(0xFECF, easyRam.getMemory().getShort(0xFFF0) & 0xFFFF);
		Assert.assertEquals(0xFE, easyRam.getMemory().get(0xFFF1) & 0xFF);
		Assert.assertEquals(0xCF, easyRam.getMemory().get(0xFFF0) & 0xFF);
	}

}
