package com.lingb.global;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;

public class Global {

	public static final String PACKAGE_NAME = "com.isport.tracker.";
	// public static final String PACKAGE_NAME =
	// "com.cn.zhihengchuang.walkbank.activity.";
	// public static final String PACKAGE_NAME = "com.go.activity.";
	// public static final String PACKAGE_NAME = "com.ready.fitness.";

	public static final String PACKAGE_NAME_ISPORT = "com.isport.tracker.";
	public static final String PACKAGE_NAME_CRAIG = "com.cn.zhihengchuang.walkbank.activity.";
	public static final String PACKAGE_NAME_GO = "com.go.activity.";
	public static final String PACKAGE_NAME_READY = "com.ready.fitness.";

	public static final String ACTION_GOAL_CHANGED = PACKAGE_NAME + "ACTION_GOAL_CHANGED";
	public static final String ACTION_STEP_LENGTH_CHANGED = PACKAGE_NAME + "ACTION_STRIKE_CHANGED";
	public static final String ACTION_HAND_TYPE_CHANGED = PACKAGE_NAME + "ACTION_HAND_TYPE_CHANGED";
	public static final String ACTION_REMAIN_CHANGED = PACKAGE_NAME + "ACTION_REMAIN_CHANGED";
	public static final String ACTION_ALARM_CHANGED = PACKAGE_NAME + "ACTION_ALARM_CHANGED";
	public static final String ACTION_SYNC_TYPE_CHANGED = PACKAGE_NAME + "ACTION_SYNC_TYPE_CHANGED";
	public static final String ACTION_BOUND_MAC_CHANGED = PACKAGE_NAME + "ACTION_BOUND_MAC_CHANGED";
	public static final String ACTION_UPDATE_CONNECT = PACKAGE_NAME + "ACTION_UPDATE_CONNECT";
	public static final String ACTION_BOUND_STOP = PACKAGE_NAME + "ACTION_BOUND_STOP";

	public static final String ACTION_VIEWPAGER_STOP = PACKAGE_NAME + "ACTION_VIEWPAGER_STOP";
	public static final int REQUEST_ENABLE_BLUETOOTH = 11;

	public static final String KEY_IS_NEW_BOUND = "KEY_IS_NEW_BOUND";

	public static final String[] DEVICE_NAME = { "W240N", "W240", "ActivityTracker", "MillionPedometer", "W286", "P118S", "W194", "W194N", "CC431", "W285A", "W285D", "W307N", "W301N", "W316" };
	public static final String[] DEVICE_NAME_NEW = { "W240N", "CC431", "W285A", "W285D", "W307N", "W301N", "W316" };
//	public static final String[] DEVICE_NAME_RIDE = { "RB CSC", "PanoBike BLE CSS", "Wahoo BlueSC v1.4", "RBCSC Beta"};
	public static final String[] DEVICE_NAME_RIDE = { "RB CSC", "RBCSC Beta"};
	public static final String DEVICE_CBGT_4_A1 = "CBGT 4 A1";
	/**
	 * yyyy-MM-dd
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_yyyy_MM_dd_birthday = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * yyyy-MM-dd HH:mm
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_yyyy_MM_dd_HH_mm = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * yyyy/MM/dd
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_yyyy_MM_dd = new SimpleDateFormat("yyyy/MM/dd");
	/**
	 * yyyy/MM/dd HH:mm:ss
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy/MM/dd   HH:mm:ss");

	/**
	 * 0
	 */
	public static final DecimalFormat df_0 = new DecimalFormat("0");
	/**
	 * 00
	 */
	public static final DecimalFormat df_00 = new DecimalFormat("00");
	/**
	 * 0.0
	 */
	public static final DecimalFormat df_0_0 = new DecimalFormat("0.0");
	/**
	 * 0.00
	 */
	public static final DecimalFormat df_0_00 = new DecimalFormat("0.00");

	public static final String receive_steps = "DE 02 01 ED";

	// ////////////////////////////////////////////////////////////////////
	public static final String KEY_BOUNDED_DEVICE = "bounded_device_mac";
	public static final String KEY_BOUNDED_NAME = "bounded_device_name";

	public static final String KEY_IS_NEW_UP_RIDE = "KEY_IS_NEW_UP_RIDE_3";
	public static final String KEY_DEVICE = "KEY_DEVICE";
	public static final int TYPE_DEVICE_NULL = 0;
	public static final int TYPE_DEVICE_BAND = 1;
	public static final int TYPE_DEVICE_RIDE = 2;

	public static final int TYPE_GENDER_MALE = 0;
	public static final int TYPE_GENDER_FEMALE = 1;

	public static final int TYPE_UNIT_METRIC = 0;
	public static final int TYPE_UNIT_IMPERIAL = 1;

	public static final String KEY_KEEP_SCREEN_ON = "KEY_KEEP_SCREEN_ON";

	public static final String ACTION_FINISH_RIDE = "ACTION_FINISH_RIDE";
	public static final String ACTION_FINISH_BAND = "ACTION_FINISH_BAND";
	public static final String ACTION_DISCONNECT_BY_USER = "ACTION_DISCONNECT_BY_USER";
	

	public static final int[] TIRE_L = { 935, 940, 1020, 1055, 1185, 1195, 1245, 1290, 1300, 1340, 1340, 1350, 1450, 1460, 1490, 1515, 1565, 1545, 1615, 1770, 1785, 1890, 1925, 1965, 1753, 1785, 1795, 1905, 1913, 1950, 2005, 2010, 2023, 2050, 2068, 2070, 2083, 2170, 1970, 2068, 2100, 1920, 1938, 1944, 1952, 2125,
			2105, 2145, 2155, 2161, 2169, 2079, 2148, 2182, 2070, 2080, 2086, 2096, 2105, 2136, 2146, 2155, 2130, 2168, 2180, 2200, 2224, 2235, 2242, 2268, 2288, 2298, 2326 };

	public static final String[] TIRE_SIZE = { "12x1.75", "12x1.95", "14x1.50", "14x1.75", "16x1.50", "16x1.75", "16x2.00", "16x1-1/8", "16x1-3/8", "17x1-1/4", "18x1.50", "18x1.75", "20x1.25", "20x1.35", " 20x1.50", "20x1.75", "20x1.95", " 20x1-1/8", "20x1-3/8", "22x1-3/8", "22x1-1/2", "24x1.75", "24x2.00",
			"24x2.125", "24x1(520)", "24x3/4 Tubular", "24x1-1/8", "24x1-1/4", "26x1(559)", "26x1.25", "26x1.40", "26x1.50", "26x1.75", "26x1.95", "26x2.10", "26x2.125", "26x2.35", "26x3.00", "26x1-1/8", "26x1-3/8", "26x1-1/2", "650C Tubular 26x7/8", "650x20C", "650x23C", "650x25C 26x1(571)",
			"650x38A", "650x38B", "27x1(630)", "27x1-1/8", "27x1-1/4", "27x1-3/8", "27.5x1.50", "27.5x2.1", "27.5x2.25", "700x18C", "700x19C", "700x20C", "700x23C", "700x25C", "700x28C", "700x30C", "700x32C", "700C Tubular", "700x35C", "700x38C", "700x40C", "700x42C", "700x44C", "700x45C", "700x47C", "29x2.1",
			"29x2.2", "29x2.3" };

}
