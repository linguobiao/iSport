package com.lingb.helper;

import com.lingb.global.Global;

/**
 * 字符串转换助手
 * @author Administrator
 *
 */
public class StringHelper {
	
	/**
	 * 将W370N、W240N、W285D转换成CC431
	 * @param deviceName
	 * @return
	 */
	public static String replaceDeviceNameToCC431(String deviceName) {
		return deviceName.replace("W307N", "CC431").replace("W240N", "CC431").replace("W285D", "CC431").replace("W285A", "CC431");
	}
	
	public static String formatDeviceName(String deviceName) {
		String result = null;
		if (deviceName != null) {
			result = deviceName.trim();
		}
		return result;
	}
	
	public static String getRideDeviceName(String name) {
		if (name == null) {
			return null;
		}
		if (name.equalsIgnoreCase(Global.DEVICE_NAME_RIDE[0]) || name.equalsIgnoreCase(Global.DEVICE_NAME_RIDE[3])) {
			return Global.DEVICE_CBGT_4_A1;
		} else {
			return name;
		}
	}

}
