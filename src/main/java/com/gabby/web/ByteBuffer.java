package com.gabby.web.util;

public class ByteBuffer extends Object {
	byte[] buf;
	
	public static ByteBuffer allocate(int capacity) {
		return new ByteBuffer(new byte[capacity]);
	}
	
	private ByteBuffer(byte[] arr) {
		buf = arr;
	}
	
	public byte[] array() {
		return buf;
	}
	
	public byte get(int index) {
		return buf[index];
	}
	
	public void put(int index, byte b) {
		buf[index] = b;
	}
	
	public void put(byte[] src) {
		for (int i = 0; i < src.length; i++) {
			buf[i] = src[i];
		}
	}
	
	public void order() {
		
	}
	
	public void clear() {
		
	}
	
	public void rewind() {
		
	}
}
