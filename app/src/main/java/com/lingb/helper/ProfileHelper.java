package com.lingb.helper;

public class ProfileHelper {
	
	private static final String KEY_TIRE_SIZE = "KEY_TIRE_SIZE";
	public static final int DEF_TIRE_SIZE = 2050;

	public static int getTireSize() {
		return SpHelper.getInt(KEY_TIRE_SIZE, DEF_TIRE_SIZE);
	}
	
	public static void setTireSize(int size) {
		SpHelper.putInt(KEY_TIRE_SIZE, size);
	}
}
