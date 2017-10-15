package com.cn.zhihengchuang.walkbank.entity;

import java.io.Serializable;

import android.bluetooth.BluetoothDevice;

public class DeviceEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String mac;
	private int rssi;
	private boolean is_bind;
	private boolean is_device;
	public BluetoothDevice getDevice() {
		return device;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}

	private BluetoothDevice device;

	public boolean isIs_bind() {
		return is_bind;
	}

	public void setIs_bind(boolean is_bind) {
		this.is_bind = is_bind;
	}

	public boolean isIs_device() {
		return is_device;
	}

	public void setIs_device(boolean is_device) {
		this.is_device = is_device;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
