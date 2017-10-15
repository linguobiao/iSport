package com.cn.zhihengchuang.walkbank.ble;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.SharedPreferences;
import android.os.Build;

public class TonometerComMandController {
	/*
	 * // 手机向设备请求心率数据 public static void sendTonometer(UUID characteristicID,
	 * BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt,
	 * SharedPreferences share) { byte[] time = new byte[] { (byte) 0xbe, 0x03,
	 * 0x01, (byte) 0xed }; sendCommand(characteristicID, mGattService,
	 * mBluetoothGatt, time); }
	 */

	// 手机向设备请求体重数据
	public static void sendWeight(UUID characteristicID,
			BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt,
			SharedPreferences share) {
		byte[] time = new byte[] { (byte) 0xbe, 0x03, 0x02, (byte) 0xed };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机向设备请求身高数据
	public static void sendHeight(UUID characteristicID,
			BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt,
			SharedPreferences share) {
		byte[] time = new byte[] { (byte) 0xbe, 0x03, 0x03, (byte) 0xed };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机向设备请求血压数据
	public static void sendBloodPressure(UUID characteristicID,
			BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt,
			SharedPreferences share) {
		byte[] time = new byte[] { (byte) 0xbe, 0x03, 0x04, (byte) 0xed };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

	// 手机向设备请求血氧数据
	public static void sendOxygen(UUID characteristicID,
			BluetoothGattService mGattService, BluetoothGatt mBluetoothGatt,
			SharedPreferences share) {
		byte[] time = new byte[] { (byte) 0xbe, 0x03, 0x05, (byte) 0xed };
		sendCommand(characteristicID, mGattService, mBluetoothGatt, time);
	}

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
	}//

}
