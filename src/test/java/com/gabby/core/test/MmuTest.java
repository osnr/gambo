package com.gabby.core.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gabby.core.Mmu;

public class MmuTest {
	private Mmu easyMmu;
	
	@Before
	public void setUp() {
		easyMmu = new Mmu(new byte[0x200000]);
	}
	
	@Test
	public void testRead() {
		easyMmu.getRom().put(0x0100, (byte) 0xEF);
		Assert.assertEquals(0xEF, easyMmu.read(0x100));
	}

	@Test
	public void testRead16() {
		easyMmu.getRom().putShort(0xF000, (short) 0xFECD);
		Assert.assertEquals(0xFECD, easyMmu.read16(0xF000));
		
		easyMmu.getRom().put(0xEF01, (byte) 0xEF);
		easyMmu.getRom().put(0xEF00, (byte) 0xAB);
		Assert.assertEquals(0xEFAB, easyMmu.read16(0xEF00));
	}

	@Test
	public void testWriteIntInt() {
		easyMmu.write(0xFD00, 0xEC);
		Assert.assertEquals(0xEC, easyMmu.getRom().get(0xFD00) & 0xFF); 
	}

	@Test
	public void testWriteIntIntInt() {
		easyMmu.write(0xDF00, 0xF3, 0x2C);
		Assert.assertEquals(0xF3, easyMmu.getRom().get(0xDF01) & 0xFF);
		Assert.assertEquals(0x2C, easyMmu.getRom().get(0xDF00) & 0xFF);
		Assert.assertEquals(0xF32C, easyMmu.getRom().getShort(0xDF00) & 0xFFFF);
	}

	@Test
	public void testWrite16() {
		easyMmu.write16(0xFFF0, 0xFECF);
		Assert.assertEquals(0xFECF, easyMmu.getRom().getShort(0xFFF0) & 0xFFFF);
		Assert.assertEquals(0xFE, easyMmu.getRom().get(0xFFF1) & 0xFF);
		Assert.assertEquals(0xCF, easyMmu.getRom().get(0xFFF0) & 0xFF);
	}

}
