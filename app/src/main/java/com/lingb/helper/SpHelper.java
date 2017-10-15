package com.lingb.helper;

import com.cn.zhihengchuang.walkbank.activity.MyApp;

public class SpHelper {
	
	public static boolean getBoolean(String key, boolean defValue) {
		return MyApp.getIntance().getSharedPreferences().getBoolean(key, defValue);
	}
	
	public static void putBoolean(String key, boolean value) {
		MyApp.getIntance().getEditor().putBoolean(key, value).commit();
	}
	
	public static int getInt(String key, int defValue) {
		return MyApp.getIntance().getSharedPreferences().getInt(key, defValue);
	}
	
	public static void putInt(String key, int value) {
		MyApp.getIntance().getEditor().putInt(key, value).commit();
	}
	
	public static String getString(String key, String defValue) {
		return MyApp.getIntance().getSharedPreferences().getString(key, defValue);
	}
	
	public static void putString(String key, String value) {
		MyApp.getIntance().getEditor().putString(key, value).commit();
	}
	
	public static long getLong(String key, long defValue) {
		return MyApp.getIntance().getSharedPreferences().getLong(key, defValue);
	}
	
	public static void putlong(String key, long value) {
		MyApp.getIntance().getEditor().putLong(key, value).commit();
	}

}
