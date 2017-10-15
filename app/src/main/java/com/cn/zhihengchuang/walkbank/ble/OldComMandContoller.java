package com.cn.zhihengchuang.walkbank.ble;

import java.util.Date;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.FormatTransfer;
import com.cn.zhihengchuang.walkbank.util.SystemConfig;
import com.cn.zhihengchuang.walkbank.util.Tools;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2) @SuppressLint("NewApi") public class OldComMandContoller {
	private static Byte reminderSwitch;
	private static byte startHour;
	private static byte startMinute;
	private static int endHour;
	private static int endMinute;
	private static int checkedItem;
	private static int alarm1Hour;
	private static int alarm1Minute;
	private static int alarm2Hour;
	private static int alarm2Minute;
	private static int alarm3Hour;
	private static int alarm3Minute;
	private static int alarm4Hour;
	private static int alarm4Minute;
	private static int alarm5Hour;
	private static int alarm5Minute;
	private final static String TAG = BleService.class.getSimpleName();

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressLint("NewApi")
	public static void sendCommand(UUID characteristicID,
			BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt,
			byte[] bytes) {
		BluetoothGattCharacteristic chara = mGattService
				.getCharacteristic(characteristicID);
		if (chara == null) {
			return;
		}
		chara.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
		chara.setValue(bytes);
		boolean status = mBluetoothGatt.writeCharacteristic(chara);
		if (bytes != null) {

		}
	}// end of sendCommand
		// 每天第一次同步数据，都会发送同步时间信息的一些指令

	public static void sendTimeCommand(UUID characteristicID,
			BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt,SharedPreferences share) {
		int week = DateUtil.getWeek(DateUtil.getCurrentDate());
		String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
		String[] date2 = dates[0].split("-");
		String[] hms = dates[1].split(":");
		String yearStr = String.valueOf(date2[0]).substring(2);
		int twoBityear = Tools.parseInt(yearStr);
		String weight=share.getString("weight", "75");
		int iStirde=60;
		int iWieght=750;
		byte[] foot=new byte[2];
		if(share.getInt("metric", 0)==0){
			iStirde=Integer.parseInt(share.getString(mBluetoothGatt.getDevice().getAddress() + "foot_distance",
					"60"));
			foot[0] = FormatTransfer.hexStringToBytes(Integer
					.toHexString(iStirde))[0];
			foot[1] = FormatTransfer.hexStringToBytes(Integer
					.toHexString(0))[0];
			iWieght = (int) (Float.parseFloat(weight) * 10); //fixed to 0.1kg
			
		}else{
			iStirde=Integer.parseInt(share.getString(mBluetoothGatt.getDevice().getAddress() + "foot_distance",
					"24"));
			iWieght =  Tools.roundHalfUp(Float.parseFloat(weight) * 0.45359237d);
			iWieght = iWieght * 10; //fixed to 0.1kg
			foot[0] = FormatTransfer.hexStringToBytes(Integer
					.toHexString( Tools.roundHalfUp(iStirde * 2.54d)))[0];
			foot[1] = FormatTransfer.hexStringToBytes(Integer
					.toHexString(1))[0];
		}
		String currTarget = share.getString(mBluetoothGatt.getDevice().getAddress() + "target_distance",
				"7000");
		byte[] bt = FormatTransfer.hexStringToDWordBytes(Integer
				.toHexString(Integer.parseInt(currTarget)));
		if (bt != null && bt.length == 4) {
		}else{
			bt[3]=(byte) 0x58;
			bt[2]=(byte) 0x1B;
			bt[1]=(byte) 0x00;
			bt[0]=(byte) 0x00;
		}
		
		byte[] bw = FormatTransfer.hexStringToWordBytes(Integer
				.toHexString(iWieght));
		
		byte[] time = new byte[] { (byte) 0x01, 0x01, 0x00, (byte) twoBityear,
				(byte) Integer.parseInt(date2[1]),
				(byte) Integer.parseInt(date2[2]), (byte) 0xce, (byte) 0x07,
				(byte) 0x0a, bw[1], bw[0],bt[3] ,bt[2]
				,bt[1] ,bt[0] , foot[0], foot[1],
				(byte) Integer.parseInt(hms[0]),
				(byte) Integer.parseInt(hms[1]),
				(byte) Integer.parseInt(hms[2]) };
		final StringBuilder stringBuilder = new StringBuilder(time.length);
		for (byte byteChar : time) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		 Log.i(TAG, "发送：" + stringBuilder.toString().trim());

		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);

	}
	// 手机发送左手
		public static void sendLeftOrRight(UUID characteristicID,
				BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt,
				SharedPreferences share) {

			// String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
			boolean isLeft = share.getBoolean(mBluetoothGatt.getDevice()
					.getAddress() + "left_hand", true);
			byte[] time;
			if (isLeft) {
				time = new byte[] { (byte) 0x0b, 0x01, 0x00};
			} else {
				time = new byte[] { (byte) 0x0c, 0x01, 0x00};
			}
			sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
		}
    
	// 久坐提醒设定
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static void sendLongSleep(UUID characteristicID,
			BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt,
			SharedPreferences share) {
		String reminder_switch = share.getString(mBluetoothGatt.getDevice()
				.getAddress() + SystemConfig.KEY_REMINDER_SWITCH, "1");
		if (!Tools.isEmpty(reminder_switch)) {
			reminderSwitch = Byte.valueOf(reminder_switch);
		} else {
			reminderSwitch = 0;
		}
		String reminder_startTime = share.getString(mBluetoothGatt.getDevice()
				.getAddress() + SystemConfig.KEY_REMINDER_STARTTIME, "");
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
		String reminder_endTime = share.getString(mBluetoothGatt.getDevice()
				.getAddress() + SystemConfig.KEY_REMINDER_ENDTIME, "");
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
		String reminder_time = share.getString(mBluetoothGatt.getDevice()
				.getAddress() + SystemConfig.KEY_REMINDER_TIME, "");
		if (!Tools.isEmpty(reminder_time)) {
			checkedItem = Integer.parseInt(reminder_time);
		} else {
			checkedItem = 0;
		}
		String[] NETITEMS = new String[] { "15", "30", "45", "60", "90", "120" };
		String sTime = NETITEMS[checkedItem];
		int time = Integer.valueOf(sTime);
		byte sHour = (byte) (time / 60);
		byte sMinute = (byte) (time % 60);
		byte[] data = new byte[] { (byte) 0x0d, 0x01, 0x00,
				 startHour, startMinute, (byte) endHour,
				(byte) endMinute,sHour,sMinute, reminderSwitch};
		final StringBuilder stringBuilder = new StringBuilder(data.length);

		for (byte byteChar : data) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		 Log.i(TAG, "发送：" + stringBuilder.toString().trim());

		sendCommand(characteristicID, mGattService, mBluetoothGatt, data);
	}
	// 手机发送闹铃时间goatent
	@SuppressLint("NewApi") @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2) 
	public static void sendAlarmTime(UUID characteristicID,
				BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt,
				SharedPreferences share) {
			
			String alarm1_switch = share.getString(SystemConfig.KEY_ALARM1_SWITCH+mBluetoothGatt.getDevice()
					.getAddress() , "0");
			
			String alarm2_Switch = share.getString(SystemConfig.KEY_ALARM2_SWITCH+mBluetoothGatt.getDevice()
					.getAddress() , "0");
			
			String alarm3_Switch = share.getString(SystemConfig.KEY_ALARM3_SWITCH+mBluetoothGatt.getDevice()
					.getAddress() , "0");
			
			String alarm4_Switch = share.getString(SystemConfig.KEY_ALARM4_SWITCH+mBluetoothGatt.getDevice()
					.getAddress() , "0");
			byte[] swits = new byte[4];
			swits[0] = Byte.valueOf(alarm1_switch);
			swits[1] = Byte.valueOf(alarm2_Switch);
			swits[2] = Byte.valueOf(alarm3_Switch);
			swits[3] = Byte.valueOf(alarm4_Switch);
			byte swit = 0;
			byte flag = 0x01;
			for (int i = 0; i < swits.length; i++) {
				if (swits[i] == 1) {
					swit = (byte) (swit | (flag << i));
				}
			}
			
			String alarm1_repeat = share.getString(SystemConfig.KEY_ALARM1_REPEAT +mBluetoothGatt.getDevice()
					.getAddress() , "");
			boolean[] alarm1Repeat = new boolean[7];
			if (!Tools.isEmpty(alarm1_repeat)) {
				byte i = Byte.valueOf(alarm1_repeat);
				byte n = (byte) 0x01;
				for (int j = 0; j < alarm1Repeat.length; j++) {
					alarm1Repeat[j] = (i & (n << j)) != 0 ? true : false;
					
				}
			}
			byte a1 = (byte) 0x01;
			Byte alarm1 = 0;
			for (int i = 0; i < alarm1Repeat.length; i++) {
				if (alarm1Repeat[i]){
					alarm1 = (byte) (alarm1 | (a1<<i));
				}
			}
			String alarm2_repeat = share.getString(SystemConfig.KEY_ALARM2_REPEAT +mBluetoothGatt.getDevice()
					.getAddress() , "");
			boolean[] alarm2Repeat = new boolean[7];
			if (!Tools.isEmpty(alarm2_repeat)) {
				byte i = Byte.valueOf(alarm2_repeat);
				byte n = (byte) 0x01;
				for (int j = 0; j < alarm2Repeat.length; j++) {
					alarm2Repeat[j] = (i & (n << j)) != 0 ? true : false;
					
				}
			}
			byte a2 = (byte) 0x01;
			Byte alarm2 = 0;
			for (int i = 0; i < alarm2Repeat.length; i++) {
				if (alarm2Repeat[i]){
					alarm2 = (byte) (alarm2 | (a2<<i));
				}
			}
			String alarm3_repeat = share.getString(SystemConfig.KEY_ALARM3_REPEAT +mBluetoothGatt.getDevice()
					.getAddress() , "");
			String alarm3s="";
			boolean[] alarm3Repeat = new boolean[7];
			if (!Tools.isEmpty(alarm3_repeat)) {
				byte i = Byte.valueOf(alarm3_repeat);
				byte n = (byte) 0x01;
				for (int j = 0; j < alarm3Repeat.length; j++) {
					alarm3Repeat[j] = (i & (n << j)) != 0 ? true : false;
					if (alarm3Repeat[j]) {
						alarm3s=alarm3s+"1";
					}else{
						alarm3s=alarm3s+"0";
					}
				}
			}
			byte a3 = (byte) 0x01;
			Byte alarm3 = 0;
			for (int i = 0; i < alarm3Repeat.length; i++) {
				if (alarm3Repeat[i]){
					alarm3 = (byte) (alarm3 | (a3<<i));
				}
			}
			String alarm4_repeat = share.getString(SystemConfig.KEY_ALARM4_REPEAT +mBluetoothGatt.getDevice()
					.getAddress() , "");
			String alarm4s="";
			boolean[] alarm4Repeat = new boolean[7];
			if (!Tools.isEmpty(alarm4_repeat)) {
				byte i = Byte.valueOf(alarm4_repeat);
				byte n = (byte) 0x01;
				for (int j = 0; j < alarm4Repeat.length; j++) {
					alarm4Repeat[j] = (i & (n << j)) != 0 ? true : false;
					if (alarm4Repeat[j]) {
						alarm4s=alarm4s+"1";
					}else{
						alarm4s=alarm4s+"0";
					}
				}
			}
			byte a4 = (byte) 0x01;
			Byte alarm4 = 0;
			for (int i = 0; i < alarm4Repeat.length; i++) {
				if (alarm4Repeat[i]){
					alarm4 = (byte) (alarm4 | (a4<<i));
				}
			}
			String alarm1_time = share.getString(SystemConfig.KEY_ALARM1_TIME+mBluetoothGatt.getDevice()
					.getAddress(), "");
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
			String alarm2_time = share.getString(SystemConfig.KEY_ALARM2_TIME+mBluetoothGatt.getDevice()
					.getAddress(), "");
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
			String alarm3_time = share.getString(SystemConfig.KEY_ALARM3_TIME+mBluetoothGatt.getDevice()
					.getAddress(), "");
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
			String alarm4_time = share.getString(SystemConfig.KEY_ALARM4_TIME+mBluetoothGatt.getDevice()
					.getAddress(), "");
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
			String alarm5_time = share.getString(SystemConfig.KEY_ALARM5_TIME+mBluetoothGatt.getDevice()
					.getAddress(), "");
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
			byte[] data = new byte[] { (byte) 0x0a, 0x01, 0x00,swit,
					(byte) alarm1Hour, (byte) alarm1Minute, alarm1, (byte) alarm2Hour, (byte) alarm2Minute, alarm2,  (byte) alarm3Hour, (byte) alarm3Minute, alarm3, (byte) alarm4Hour, (byte) alarm4Minute, alarm4};
	        
			final StringBuilder stringBuilder = new StringBuilder(data.length);

			for (byte byteChar : data) {
				stringBuilder.append(String.format("%02X ", byteChar));
			}

			sendCommand(characteristicID, mGattService, mBluetoothGatt, data);
		}


	// 手机发送出厂模式功能
	public static void sendFactoryPatternCommand(UUID characteristicID,
			BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt) {
		// String[] dates = DateUtil.getCurrentDateymdhms().split(" ");
		byte[] time = new byte[] { (byte) 0xbe, 0x01, 0x0d, (byte) 0xed };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

}
