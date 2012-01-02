package com.gabby.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.AttributedString;
import java.util.ArrayList;

public class Ram {

    public static final int MEMORY_SIZE = 0xFFFF;
    
    public static final int BACKGROUND_WIDTH = 256;
    public static final int BACKGROUND_HEIGHT = 256;
    public static final int WINDOW_WIDTH = 160;
    public static final int WINDOW_HEIGHT = 144;
	
    public static final int OAM = 0xFE00;
    public static final int LCDC = 0xFF40;
    public static final int SCX = 0xFF43;
    public static final int SCY = 0xff42;
    public static final int WX = 0xFF4B;
    public static final int WY = 0xFF4A;
	
    public static final int TILE_TABLE_ONE = 0x8000;
    public static final int TILE_TABLE_TWO = 0x8800;
    public static final int SPRITE_TABLE = TILE_TABLE_ONE;
    public static final int BACKGROUND_ONE = 0x9800;
    public static final int BACKGROUND_TWO = 0x9C00;

    public static final int INPUT = 0xFF00;
	
    ByteBuffer memory;

    public Ram() {
        memory = ByteBuffer.allocateDirect(0xFFFF);
    }
	
    public ByteBuffer getMemory() {
        return memory;
    }
	
    public void setMemory(ByteBuffer memory) {
        this.memory = memory;
    }
}
