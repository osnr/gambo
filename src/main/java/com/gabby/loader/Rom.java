package com.gabby.loader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Rom {
    private ByteBuffer entry;
    private ByteBuffer logo;
    private ByteBuffer title;
    private ByteBuffer newLicenseeCode;
    private byte sgbFlag;
    private byte cartidgeType;
    private byte romSize;
    private byte ramSize;
    private byte destinationCode;
    private byte oldLicenseeCode;
    private byte maskRomVersionNumber;
    private byte headerChecksum;
    private ByteBuffer globalChecksum;
    private ByteBuffer rom;
	
    // Subtraction equations because I can't count in hex well
    public static final int ENTRY_LENGTH = 0x104 - 0x0100;
    public static final int LOGO_LENGTH = 0x0134 - 0x104;
    public static final int TITLE_LENGTH = 0x0144 - 0x0134;
    public static final int NEW_LICENSEE_CODE_LENGTH = 0x0146 - 0x0144;
    public static final int GLOBAL_CHECKSUM_LENGTH = 0x0150 - 0x014E;
    public static final int ROM_LENGTH = 0x8000;
    
    public Rom() {
        entry = ByteBuffer.allocate(ENTRY_LENGTH);
        logo = ByteBuffer.allocate(LOGO_LENGTH);
        title = ByteBuffer.allocate(TITLE_LENGTH);
        newLicenseeCode = ByteBuffer.allocate(NEW_LICENSEE_CODE_LENGTH);
        globalChecksum = ByteBuffer.allocate(GLOBAL_CHECKSUM_LENGTH);
        rom = ByteBuffer.allocate(ROM_LENGTH);
		
        entry.order(ByteOrder.LITTLE_ENDIAN);
        logo.order(ByteOrder.LITTLE_ENDIAN);
        title.order(ByteOrder.LITTLE_ENDIAN);
        newLicenseeCode.order(ByteOrder.LITTLE_ENDIAN);
        globalChecksum.order(ByteOrder.LITTLE_ENDIAN);
        rom.order(ByteOrder.LITTLE_ENDIAN);
    }
	
    public ByteBuffer getEntry() {
        return entry;
    }
	
    public void setEntry(ByteBuffer entry) {
        this.entry = entry;
    }
	
    public ByteBuffer getLogo() {
        return logo;
    }
	
    public void setLogo(ByteBuffer logo) {
        this.logo = logo;
    }
	
    public ByteBuffer getTitle() {
        return title;
    }
	
    public void setTitle(ByteBuffer title) {
        this.title = title;
    }
	
    public ByteBuffer getNewLicenseeCode() {
        return newLicenseeCode;
    }
	
    public void setNewLicenseeCode(ByteBuffer newLicenseeCode) {
        this.newLicenseeCode = newLicenseeCode;
    }
	
    public byte getSgbFlag() {
        return sgbFlag;
    }
	
    public void setSgbFlag(Byte sgbFlag) {
        this.sgbFlag = sgbFlag;
    }
	
    public byte getCartidgeType() {
        return cartidgeType;
    }
	
    public void setCartidgeType(Byte cartidgeType) {
        this.cartidgeType = cartidgeType;
    }
	
    public byte getRomSize() {
        return romSize;
    }
	
    public void setRomSize(Byte romSize) {
        this.romSize = romSize;
    }
	
    public byte getRamSize() {
        return ramSize;
    }
	
    public void setRamSize(Byte ramSize) {
        this.ramSize = ramSize;
    }
	
    public byte getDestinationCode() {
        return destinationCode;
    }
	
    public void setDestinationCode(Byte destinationCode) {
        this.destinationCode = destinationCode;
    }
	
    public byte getOldLicenseeCode() {
        return oldLicenseeCode;
    }
	
    public void setOldLicenseeCode(byte oldLicenseeCode) {
        this.oldLicenseeCode = oldLicenseeCode;
    }
	
    public byte getMaskRomVersionNumber() {
        return maskRomVersionNumber;
    }
	
    public void setMaskRomVersionNumber(Byte maskRomVersionNumber) {
        this.maskRomVersionNumber = maskRomVersionNumber;
    }
	
    public byte getHeaderChecksum() {
        return headerChecksum;
    }
	
    public void setHeaderChecksum(Byte headerChecksum) {
        this.headerChecksum = headerChecksum;
    }
	
    public ByteBuffer getGlobalChecksum() {
        return globalChecksum;
    }
	
    public void setGlobalChecksum(ByteBuffer globalChecksum) {
        this.globalChecksum = globalChecksum;
    }
	
    public ByteBuffer getRom() {
        return rom;
    }

    public void setRom(ByteBuffer rom) {
        this.rom = rom;
    }
}
