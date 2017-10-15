package com.cn.zhihengchuang.walkbank.ble;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.cn.zhihengchuang.walkbank.activity.DisplayActivity;
import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.cn.zhihengchuang.walkbank.activity.SleepActivity;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.IsChineseOrNot;
import com.cn.zhihengchuang.walkbank.util.SystemConfig;
import com.cn.zhihengchuang.walkbank.util.Tools;
import com.lingb.helper.SpHelper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

/**
 * 一系列蓝牙指令
 * 
 * @author longke
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("NewApi")
public class ComMandContoller {
	private final static String TAG = BleService.class.getSimpleName();
	private static Byte reminderSwitch;
	private static byte startHour;
	private static byte startMinute;
	private static int endHour;
	private static int endMinute;
	private static int checkedItem;
	private static int alarm1Hour;
	private static int alarm1Minute;
	private static int alarm2Minute;
	private static int alarm2Hour;
	private static int alarm3Hour;
	private static int alarm3Minute;
	private static int alarm4Hour;
	private static int alarm4Minute;
	private static int alarm5Hour;
	private static int alarm5Minute;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressLint("NewApi")
	public static void sendCommand(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, byte[] bytes) {
		BluetoothGattCharacteristic chara = mGattService.getCharacteristic(characteristicID);
		if (chara == null) {
			return;
		}
		chara.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
		chara.setValue(bytes);
		if (mBluetoothGatt != null) {
			boolean status = mBluetoothGatt.writeCharacteristic(chara);
		}

		if (bytes != null) {

		}
	}// end of sendCommand

	// 每天第一次同步数据，都会发送同步时间信息的一些指令
	public static void sendTimeCommand(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt) {
		int week = DateUtil.getWeek(DateUtil.getCurrentDate());
		String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
		String[] date2 = dates[0].split("-");
		String[] hms = dates[1].split(":");
		byte[] time = new byte[] { (byte) 0xbe, 0x01, 0x02, (byte) 0xfe, (byte) (Integer.parseInt(date2[0]) >> 8), (byte) Integer.parseInt(date2[0]), (byte) Integer.parseInt(date2[1]), (byte) Integer.parseInt(date2[2]), (byte) week, (byte) 0x10, (byte) Integer.parseInt(hms[0]), (byte) Integer.parseInt(hms[1]),
				(byte) Integer.parseInt(hms[2]) };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);

	}

	// 第一次安装程序1.1指令
	public static void sendFristTimeCommand(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, SharedPreferences share) {
		byte metric;
		if (share.getInt("metric", 0) == 0) {
			metric = 0x00;
		} else {
			metric = 0x01;
		}
		ContentResolver cv = MyApp.getIntance().getApplicationContext().getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24);
		byte timeFormat;
		if (strTimeFormat != null && strTimeFormat.equals("24")) {
			timeFormat = 0x00;
		} else {
			timeFormat = 0x01;
		}
		int week = DateUtil.getWeek(DateUtil.getCurrentDate());
		String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
		String[] date2 = dates[0].split("-");
		String[] hms = dates[1].split(":");
		byte[] time = new byte[] { (byte) 0xbe, 0x01, 0x01, (byte) 0xfe, metric, timeFormat, (byte) 0x10, (byte) 0x10, (byte) (Integer.parseInt(date2[0]) >> 8), (byte) Integer.parseInt(date2[0]), (byte) Integer.parseInt(date2[1]), (byte) Integer.parseInt(date2[2]), (byte) week, (byte) Integer.parseInt(hms[0]),
				(byte) Integer.parseInt(hms[1]), (byte) Integer.parseInt(hms[2]) };
		final StringBuilder stringBuilder = new StringBuilder(time.length);

		for (byte byteChar : time) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		Log.i(TAG, "发送：" + stringBuilder.toString().trim());
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);

	}

	// 获取设备中历史数据天数
	public static void sendGetHostoryDateCommand(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt) {
		byte[] time = new byte[] { (byte) 0xbe, 0x02, 0x05, (byte) 0xed };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 获取历史数据
	public static void sendGetFromDateDataCommand(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, String date) {
		// String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
		Log.i(TAG, "date" + date);
		String[] date2 = date.split("-");
		byte[] time = new byte[] { (byte) 0xbe, 0x02, 0x01, (byte) 0xfe, (byte) (Integer.parseInt(date2[0]) >> 8), (byte) Integer.parseInt(date2[0]), (byte) Integer.parseInt(date2[1]), (byte) Integer.parseInt(date2[2]), 0x00 };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 发送睡眠闹钟提醒
	public static void sendSleepAlertCommand(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt) {
		// String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
		byte[] time = new byte[] { (byte) 0xbe, 0x01, 0x07, (byte) 0xfe, (byte) 0x01, (byte) 0x0c, (byte) 0x00, (byte) 0x0c, (byte) 0x00 };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机发送自定义的显示界面及顺序
	public static void sendCustomInterfaceCommand(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt) {
		// String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
		byte[] time = new byte[] { (byte) 0xbe, 0x01, 0x08, (byte) 0xfe, (byte) 0x03, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机发送出厂模式功能
	public static void sendFactoryPatternCommand(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt) {
		// String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
		byte[] time = new byte[] { (byte) 0xbe, 0x01, 0x0d, (byte) 0xed };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机发送待显示的名字给设备
	public static void sendDisplayNameCommand(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt) {
		// String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
		byte[] time = new byte[] { (byte) 0xbe, 0x01, 0x0d, (byte) 0xed };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机发送左手
	public static void sendLeftOrRight(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, SharedPreferences share) {

		// String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
		boolean isLeft = share.getBoolean(mBluetoothGatt.getDevice().getAddress() + "left_hand", true);
		byte[] time;
		if (isLeft) {

			time = new byte[] { (byte) 0xbe, 0x01, 0x0b, (byte) 0xfe, 0x00 };
		} else {
			time = new byte[] { (byte) 0xbe, 0x01, 0x0b, (byte) 0xfe, 0x01 };
		}
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机发送步距
	public static void sendStepLength(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, SharedPreferences share) {
		int sleepTargetHour = SpHelper.getInt(SleepActivity.KEY_TARGET_HOUR, 8);
		int sleepTargetMinute = SpHelper.getInt(SleepActivity.KEY_TARGET_MINUTE, 0);
		// String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
		String footDistance = share.getString(mBluetoothGatt.getDevice().getAddress() + "foot_distance", "60");
		int foot = Integer.parseInt(footDistance) * 100;
		String targetDistance = share.getString(mBluetoothGatt.getDevice().getAddress() + "target_distance", "10000");
		byte[] time = new byte[] { (byte) 0xbe, 0x01, 0x03, (byte) 0xfe, 0x07, (byte) 0xc6, 0x01, 0x01, 0x00, 0x3c, (byte) (Integer.parseInt(targetDistance) >> 16), (byte) (Integer.parseInt(targetDistance) >> 8), (byte) Integer.parseInt(targetDistance), (byte) (foot >> 8), (byte) foot, (byte) sleepTargetHour, (byte) sleepTargetMinute };

		final StringBuilder stringBuilder = new StringBuilder(time.length);

		for (byte byteChar : time) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		Log.i(TAG, "发送：" + stringBuilder.toString().trim());

		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 久坐提醒设定
	public static void sendLongSleep(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, SharedPreferences share) {
		String reminder_switch = share.getString(mBluetoothGatt.getDevice().getAddress() + SystemConfig.KEY_REMINDER_SWITCH, "1");
		if (!Tools.isEmpty(reminder_switch)) {
			reminderSwitch = Byte.valueOf(reminder_switch);
		} else {
			reminderSwitch = 0;
		}
		String reminder_startTime = share.getString(mBluetoothGatt.getDevice().getAddress() + SystemConfig.KEY_REMINDER_STARTTIME, "");
		if (!Tools.isEmpty(reminder_startTime)) {
			String[] s = reminder_startTime.split(":");
			if (s != null && s.length == 2) {
				startHour = (byte) Integer.parseInt((s[0]));
				startMinute = (byte) Integer.parseInt((s[1]));
			}
		} else {
			startHour = 8;
			startMinute = 0;
		}
		String reminder_endTime = share.getString(mBluetoothGatt.getDevice().getAddress() + SystemConfig.KEY_REMINDER_ENDTIME, "");
		if (!Tools.isEmpty(reminder_endTime)) {
			String[] s = reminder_endTime.split(":");
			if (s != null && s.length == 2) {
				endHour = Integer.parseInt(s[0]);
				endMinute = Integer.parseInt(s[1]);
			}
		} else {
			endHour = 18;
			endMinute = 0;
		}
		String reminder_time = share.getString(mBluetoothGatt.getDevice().getAddress() + SystemConfig.KEY_REMINDER_TIME, "");
		if (!Tools.isEmpty(reminder_time)) {
			checkedItem = Integer.parseInt(reminder_time);
		} else {
			checkedItem = 0;
		}
		String[] NETITEMS = new String[] { "15", "30", "45", "60", "90", "120" };
		String time = NETITEMS[checkedItem];
		byte[] data = new byte[] { (byte) 0xbe, 0x01, 0x0c, (byte) 0xfe, reminderSwitch, startHour, startMinute, (byte) endHour, (byte) endMinute, startHour, startMinute, (byte) endHour, (byte) endMinute, startHour, startMinute, (byte) endHour, (byte) endMinute, 0x00, (byte) Integer.parseInt(time) };

		final StringBuilder stringBuilder = new StringBuilder(data.length);

		for (byte byteChar : data) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		Log.i(TAG, "发送：" + stringBuilder.toString().trim());

		sendCommand(characteristicID, mGattService, mBluetoothGatt, data);
	}

	/**
	 * 发送自动睡眠
	 * 
	 * @param characteristicID
	 * @param mGattService
	 * @param mBluetoothGatt
	 */
	public static void sendAutoSleep(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt) {

//		15Byte BE+01+07+FE+开关控制(1byte)
//		+计划睡眠小时(1byte)+计划睡眠分(1byte) 
//		+提前提醒睡眠小时(1byte) )+提前提醒睡眠分(1byte) 
//		+计划起床小时(1byte) +计划起床分(1byte)
//		+计划午休小时(1byte) +计划午休分(1byte)
//		+结束午休小时(1byte) +结束午休分(1byte)
		
		boolean isOpen = SpHelper.getBoolean(SleepActivity.KEY_IS_SLEEP_AUTO, false);
		boolean isSleep = SpHelper.getBoolean(SleepActivity.KEY_IS_SLEEP_TIME, false);
		int beginHour = SpHelper.getInt(SleepActivity.KEY_TIME_BEGIN_HOUR, 22);
		int beginMinute = SpHelper.getInt(SleepActivity.KEY_TIME_BEGIN_MINUTE, 0);
		int endHour = SpHelper.getInt(SleepActivity.KEY_TIME_END_HOUR, 6);
		int endMinute = SpHelper.getInt(SleepActivity.KEY_TIME_END_MINUTE, 0);
		
		boolean isLunch = SpHelper.getBoolean(SleepActivity.KEY_IS_SLEEP_LUNCH, false);
		int beginHourLunch = SpHelper.getInt(SleepActivity.KEY_LUNCH_BEGIN_HOUR, 13);
		int beginMinuteLunch = SpHelper.getInt(SleepActivity.KEY_LUNCH_BEGIN_MINUTE, 0);
		int endHourLunch = SpHelper.getInt(SleepActivity.KEY_LUNCH_END_HOUR, 14);
		int endMinuteLunch = SpHelper.getInt(SleepActivity.KEY_LUNCH_END_MINUTE, 0);
		
		boolean isRemind = SpHelper.getBoolean(SleepActivity.KEY_IS_SLEEP_REMIND, false);
		int remind = SpHelper.getInt(SleepActivity.KEY_REMINDER, 15);
		int remindTotalMinute = beginHour * 60 + beginMinute - remind;
		if (remindTotalMinute < 0) {
			remindTotalMinute = 24 * 60 - remindTotalMinute;
		}
		int remindHour = remindTotalMinute / 60;
		int remindMinute = remindTotalMinute % 60;
		byte[] value = new byte[15];
		value[0] = (byte) 0xbe;
		value[1] = (byte) 0x01;
		value[2] = (byte) 0x07;
		value[3] = (byte) 0xfe;
		if (isOpen) 
			value[4] = (byte) 0x01;
		else
			value[4] = (byte) 0x00;
		if(isSleep) {
			value[5] = (byte) beginHour;
			value[6] = (byte) beginMinute;
			value[9] = (byte) endHour;
			value[10] = (byte) endMinute;
		} else {
			value[5] = (byte) 0xff;
			value[6] = (byte) 0xff;
			value[9] = (byte) 0xff;
			value[10] = (byte) 0xff;
		}
		if (isRemind && remind != 0) {
			value[7] = (byte) remindHour;
			value[8] = (byte) remindMinute;
		} else {
			value[7] = (byte) 0xff;
			value[8] = (byte) 0xff;
		}
		if(isLunch) {
			value[11] = (byte) beginHourLunch;
			value[12] = (byte) beginMinuteLunch;
			value[13] = (byte) endHourLunch;
			value[14] = (byte) endMinuteLunch;
		} else {
			value[11] = (byte) 0xff;
			value[12] = (byte) 0xff;
			value[13] = (byte) 0xff;
			value[14] = (byte) 0xff;
		}

		final StringBuilder stringBuilder = new StringBuilder(value.length);

		for (byte byteChar : value) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		Log.i(TAG, "发送自动睡眠：" + stringBuilder.toString().trim());

		sendCommand(characteristicID, mGattService, mBluetoothGatt, value);
	}
	/**
	 * 发送设备显示
	 * 
	 * @param characteristicID
	 * @param mGattService
	 * @param mBluetoothGatt
	 */
	public static void sendDeviceDisplay(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt) {
		
		boolean isCalories = SpHelper.getBoolean(DisplayActivity.KEY_SHOW_CALORIES, true);
		boolean isDistance = SpHelper.getBoolean(DisplayActivity.KEY_SHOW_DISTANCE, true);
		boolean isSportTime = SpHelper.getBoolean(DisplayActivity.KEY_SHOW_TIME, true);
		boolean isPercent = SpHelper.getBoolean(DisplayActivity.KEY_SHOW_PERCENT, true);
		boolean isFace = SpHelper.getBoolean(DisplayActivity.KEY_SHOW_FACE, true);
		boolean isAlarm = SpHelper.getBoolean(DisplayActivity.KEY_SHOW_ALARM, true);
		boolean isSms = SpHelper.getBoolean(DisplayActivity.KEY_SHOW_SMS, true);
		boolean isCall = SpHelper.getBoolean(DisplayActivity.KEY_SHOW_CALL, true);
		
		
		byte[] value = new byte[20];
		value[0] = (byte) 0xbe;
		value[1] = (byte) 0x01;
		value[2] = (byte) 0x08;
		value[3] = (byte) 0xfe;
		value[4] = (byte) 0x01;
		value[5] = (byte) 0x02;
		int index = 6;
		if (isCalories) {
			value[index] = 0x03;
			index ++;
		} 
		if (isDistance) {
			value[index] = 0x04;
			index ++;
		} 
		if (isSportTime) {
			value[index] = 0x05;
			index ++;
		}
		if (isPercent) {
			value[index] = 0x06;
			index ++;
		}
		if (isFace) {
			value[index] = 0x07;
			index ++;
		}
		if (isAlarm) {
			value[index] = 0x08;
			index ++;
		}
		if (isSms) {
			value[index] = 0x0a;
			index ++;
		}
		if (isCall) {
			value[index] = 0x0b;
			index ++;
		}
		for (int i = index; i < 20; i ++) {
			value[i] = (byte) 0xff;
		}
		
		final StringBuilder stringBuilder = new StringBuilder(value.length);
		for (byte byteChar : value) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		Log.i(TAG, "发送设备显示：" + stringBuilder.toString().trim());
		
		sendCommand(characteristicID, mGattService, mBluetoothGatt, value);
	}

	// 手机发送闹铃时间goatent
	public static void sendAlarmTime(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, SharedPreferences share) {

		String alarm1_switch = share.getString(SystemConfig.KEY_ALARM1_SWITCH + mBluetoothGatt.getDevice().getAddress(), "0");

		String alarm2_Switch = share.getString(SystemConfig.KEY_ALARM2_SWITCH + mBluetoothGatt.getDevice().getAddress(), "0");

		String alarm3_Switch = share.getString(SystemConfig.KEY_ALARM3_SWITCH + mBluetoothGatt.getDevice().getAddress(), "0");

		String alarm4_Switch = share.getString(SystemConfig.KEY_ALARM4_SWITCH + mBluetoothGatt.getDevice().getAddress(), "0");
		String alarm5_Switch = share.getString(SystemConfig.KEY_ALARM5_SWITCH + mBluetoothGatt.getDevice().getAddress(), "0");

		String alarm1_repeat = share.getString(SystemConfig.KEY_ALARM1_REPEAT + mBluetoothGatt.getDevice().getAddress(), "");
		boolean[] alarm1Repeat = new boolean[7];
		String alarm1s = "";
		if (!Tools.isEmpty(alarm1_repeat)) {
			byte i = Byte.valueOf(alarm1_repeat);
			byte n = (byte) 0x01;
			for (int j = alarm1Repeat.length - 1; j >= 0; j--) {
				alarm1Repeat[j] = (i & (n << j)) != 0 ? true : false;
				if (alarm1Repeat[j]) {
					alarm1s = alarm1s + "1";
				} else {
					alarm1s = alarm1s + "0";
				}
			}
		}

		String alarm2_repeat = share.getString(SystemConfig.KEY_ALARM2_REPEAT + mBluetoothGatt.getDevice().getAddress(), "");
		String alarm2s = "";
		boolean[] alarm2Repeat = new boolean[7];
		if (!Tools.isEmpty(alarm2_repeat)) {
			byte i = Byte.valueOf(alarm2_repeat);
			byte n = (byte) 0x01;
			for (int j = alarm2Repeat.length - 1; j >= 0; j--) {
				alarm2Repeat[j] = (i & (n << j)) != 0 ? true : false;
				if (alarm2Repeat[j]) {
					alarm2s = alarm2s + "1";
				} else {
					alarm2s = alarm2s + "0";
				}
			}
		}
		String alarm3_repeat = share.getString(SystemConfig.KEY_ALARM3_REPEAT + mBluetoothGatt.getDevice().getAddress(), "");
		String alarm3s = "";
		boolean[] alarm3Repeat = new boolean[7];
		if (!Tools.isEmpty(alarm3_repeat)) {
			byte i = Byte.valueOf(alarm3_repeat);
			byte n = (byte) 0x01;
			for (int j = alarm3Repeat.length - 1; j >= 0; j--) {
				alarm3Repeat[j] = (i & (n << j)) != 0 ? true : false;
				if (alarm3Repeat[j]) {
					alarm3s = alarm3s + "1";
				} else {
					alarm3s = alarm3s + "0";
				}
			}
		}
		String alarm4_repeat = share.getString(SystemConfig.KEY_ALARM4_REPEAT + mBluetoothGatt.getDevice().getAddress(), "");
		String alarm4s = "";
		boolean[] alarm4Repeat = new boolean[7];
		if (!Tools.isEmpty(alarm4_repeat)) {
			byte i = Byte.valueOf(alarm4_repeat);
			byte n = (byte) 0x01;
			for (int j = alarm4Repeat.length - 1; j >= 0; j--) {
				alarm4Repeat[j] = (i & (n << j)) != 0 ? true : false;
				if (alarm4Repeat[j]) {
					alarm4s = alarm4s + "1";
				} else {
					alarm4s = alarm4s + "0";
				}
			}
		}
		String alarm5_repeat = share.getString(SystemConfig.KEY_ALARM5_REPEAT + mBluetoothGatt.getDevice().getAddress(), "");
		String alarm5s = "";
		boolean[] alarm5Repeat = new boolean[7];
		if (!Tools.isEmpty(alarm5_repeat)) {
			byte i = Byte.valueOf(alarm5_repeat);
			byte n = (byte) 0x01;
			for (int j = alarm5Repeat.length - 1; j >= 0; j--) {
				alarm5Repeat[j] = (i & (n << j)) != 0 ? true : false;
				if (alarm5Repeat[j]) {
					alarm5s = alarm5s + "1";
				} else {
					alarm5s = alarm5s + "0";
				}
			}
		}
		String switch1 = alarm1s.contains("1") ? "1" : "0";
		String switch2 = alarm1s.contains("1") ? "1" : "0";
		String switch3 = alarm1s.contains("1") ? "1" : "0";
		String switch4 = alarm1s.contains("1") ? "1" : "0";
		String switch5 = alarm1s.contains("1") ? "1" : "0";
		Byte alarm1 = (byte) Integer.parseInt(switch1 + alarm1s, 2);
		Byte alarm2 = (byte) Integer.parseInt(switch2 + alarm2s, 2);
		Byte alarm3 = (byte) Integer.parseInt(switch3 + alarm3s, 2);
		Byte alarm4 = (byte) Integer.parseInt(switch4 + alarm4s, 2);
		Byte alarm5 = (byte) Integer.parseInt(switch5 + alarm5s, 2);
		Byte open = (byte) Integer.parseInt("000" + alarm5_Switch + alarm4_Switch + alarm3_Switch + alarm2_Switch + alarm1_switch, 2);
		String alarm1_time = share.getString(SystemConfig.KEY_ALARM1_TIME + mBluetoothGatt.getDevice().getAddress(), "");
		if (!Tools.isEmpty(alarm1_time)) {
			String[] s = alarm1_time.split(":");
			if (s != null && s.length == 2) {
				alarm1Hour = Integer.parseInt(s[0]);
				alarm1Minute = Integer.parseInt(s[1]);
			}
		} else {
			alarm1Hour = 8;
			alarm1Minute = 0;
		}
		String alarm2_time = share.getString(SystemConfig.KEY_ALARM2_TIME + mBluetoothGatt.getDevice().getAddress(), "");
		if (!Tools.isEmpty(alarm2_time)) {
			String[] s = alarm2_time.split(":");
			if (s != null && s.length == 2) {
				alarm2Hour = Integer.parseInt(s[0]);
				alarm2Minute = Integer.parseInt(s[1]);
			}
		} else {
			alarm2Hour = 8;
			alarm2Minute = 0;
		}
		String alarm3_time = share.getString(SystemConfig.KEY_ALARM3_TIME + mBluetoothGatt.getDevice().getAddress(), "");
		if (!Tools.isEmpty(alarm3_time)) {
			String[] s = alarm3_time.split(":");
			if (s != null && s.length == 2) {
				alarm3Hour = Integer.parseInt(s[0]);
				alarm3Minute = Integer.parseInt(s[1]);
			}
		} else {
			alarm3Hour = 8;
			alarm3Minute = 0;
		}
		String alarm4_time = share.getString(SystemConfig.KEY_ALARM4_TIME + mBluetoothGatt.getDevice().getAddress(), "");
		if (!Tools.isEmpty(alarm4_time)) {
			String[] s = alarm4_time.split(":");
			if (s != null && s.length == 2) {
				alarm4Hour = Integer.parseInt(s[0]);
				alarm4Minute = Integer.parseInt(s[1]);
			}
		} else {
			alarm4Hour = 8;
			alarm4Minute = 0;
		}
		String alarm5_time = share.getString(SystemConfig.KEY_ALARM5_TIME + mBluetoothGatt.getDevice().getAddress(), "");
		if (!Tools.isEmpty(alarm5_time)) {
			String[] s = alarm5_time.split(":");
			if (s != null && s.length == 2) {
				alarm5Hour = Integer.parseInt(s[0]);
				alarm5Minute = Integer.parseInt(s[1]);
			}
		} else {
			alarm5Hour = 8;
			alarm5Minute = 0;
		}
		byte[] data = new byte[] { (byte) 0xbe, 0x01, 0x09, (byte) 0xfe, open, (byte) alarm1Hour, (byte) alarm1Minute, alarm1, (byte) alarm2Hour, (byte) alarm2Minute, alarm2, (byte) alarm3Hour, (byte) alarm3Minute, alarm3, (byte) alarm4Hour, (byte) alarm4Minute, alarm4, (byte) alarm5Hour, (byte) alarm5Minute, alarm5 };

		final StringBuilder stringBuilder = new StringBuilder(data.length);

		for (byte byteChar : data) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		Log.i(TAG, "发送：" + stringBuilder.toString().trim());

		sendCommand(characteristicID, mGattService, mBluetoothGatt, data);
	}

	// 恢复出厂设置
	public static void sendClearAll(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt) {
		byte[] time = new byte[] { (byte) 0xbe, 0x01, 0x0b, (byte) 0xfe, 0x00 };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机未读短信的数量给设备
	public static void sendUnreadSmsCount(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, SharedPreferences share) {
		int unReadSmsCount = share.getInt(SystemConfig.KEY_UNREADSMSCOUNT, 0);
		Byte smsCount = (byte) unReadSmsCount;
		byte[] time = new byte[] { (byte) 0xbe, 0x06, 0x04, (byte) 0xfe, smsCount };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机未读电话的数量给设备
	public static void sendUnreadPhoneCount(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, SharedPreferences share) {
		int unReadPhoneCount = share.getInt(SystemConfig.KEY_UNREADPHONE, 0);
		Byte smsPhone = (byte) unReadPhoneCount;
		byte[] time = new byte[] { (byte) 0xbe, 0x06, 0x03, (byte) 0xfe, smsPhone };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机发送来电的电话号码给设备
	public static void sendPhoneNum(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, SharedPreferences share) {
		byte[] time = new byte[20];
		int c = 0;
		time[c++] = (byte) 0xbe;
		time[c++] = (byte) 0x06;
		time[c++] = (byte) 0x02;
		time[c++] = (byte) 0xfe;
		String comming_phone = share.getString(SystemConfig.KEY_COMMING_PHONE, "");
		Byte phoneLength = (byte) comming_phone.length();
		time[c++] = phoneLength;
		for (int i = 0; i < comming_phone.length(); i++) {
			char p = comming_phone.charAt(i);
			byte b = (byte) p;
			time[c++] = b;
		}
		if (c < 20) {
			for (int t = c; t < 20; t++) {
				time[c++] = (byte) 0xff;
			}
		}
		final StringBuilder stringBuilder = new StringBuilder(time.length);

		for (byte byteChar : time) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		Log.i(TAG, "发送：" + stringBuilder.toString().trim());
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机发送来电的姓名给设备
	public static void sendPhoneName(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, SharedPreferences share) {
		byte[] time = new byte[20];
		int c = 0;
		time[c++] = (byte) 0xbe;
		time[c++] = (byte) 0x06;
		time[c++] = (byte) 0x01;
		time[c++] = (byte) 0xfe;
		String comming_phone = share.getString(SystemConfig.KEY_COMMING_PHONE_NAME, "");
		Byte phoneLength = (byte) comming_phone.length();
		time[c++] = phoneLength;
		if (!IsChineseOrNot.isChinese(comming_phone)) {
			for (int i = 0; i < comming_phone.length(); i++) {
				char p = comming_phone.charAt(i);
				byte b = (byte) p;
				time[c++] = b;
			}
		} else {
			time[c++] = 0x00;
		}

		if (c < 20) {
			for (int t = c; t < 20; t++) {
				time[c++] = (byte) 0xff;
			}
		}
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 启用实时传输
	public static void sendRealTime(UUID characteristicID, BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt, boolean isOpen) {
		byte[] time;
		if (isOpen) {
			time = new byte[] { (byte) 0xbe, 0x02, 0x03, (byte) 0xed };
		} else {
			time = new byte[] { (byte) 0xbe, 0x02, 0x04, (byte) 0xed };
		}
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	/*
	 * // 手机发送待显示的名字给设备 public static void sendDisplayNameCommand(UUID
	 * characteristicID, BluetoothGattService mGattService, BluetoothGatt
	 * mBluetoothGatt) { // String[] dates =
	 * DateUtil.getCurrentDateymdhms().split(" "); byte[] time = new byte[] {
	 * (byte) 0xbe, 0x01, 0x0d, (byte) 0xed }; sendCommand(characteristicID,
	 * mGattService, mBluetoothGatt, time); }
	 */
}
