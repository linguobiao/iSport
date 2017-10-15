package com.cn.zhihengchuang.walkbank.entity;

import android.bluetooth.BluetoothDevice;

public class ScanBleDevice {
	private BluetoothDevice device;
	private int rssi;
	private byte[] scanRecord;
	public BluetoothDevice getDevice() {
		return device;
	}
	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}
	public int getRssi() {
		return rssi;
	}
	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
	public byte[] getScanRecord() {
		return scanRecord;
	}
	public void setScanRecord(byte[] scanRecord) {
		this.scanRecord = scanRecord;
	}
}
