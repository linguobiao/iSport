package com.cn.zhihengchuang.walkbank.util;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;

@SuppressLint("NewApi")
public class BluetoothManagerUtil {
	private Context context;
	private static BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;
	private static final long SCAN_PERIOD = 6300;
	private LeScanCallback mLeScanCallback;
	private boolean flag = false;
	private BlueCallBack callBack;
	private boolean isStart = false;
	public static final String SERVICE_UUID = "DE5BF728D7114E47AF2665E3012A5DC7";
	private SharedPreferences share = null;

	public BluetoothManagerUtil(Context context, BlueCallBack callBack,Handler handler) {
		this.mHandler = handler;
		this.context = context;
		this.callBack = callBack;
		if (this.context != null) {
			initBle();
		}
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	private void initBle() {
		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			setFlag(false);
			return;
		}
		// 初始化 Bluetooth adapter,
		// 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 检查设备上是否支持蓝牙
		if (mBluetoothAdapter == null) {
			setFlag(false);
			return;
		}
		setFlag(true);
		// 设备扫描回调。
		mLeScanCallback = new LeScanCallback() {
			@Override
			public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
				StringBuilder builder = new StringBuilder();
				for(int i = scanRecord.length - 1; i >= 0; i--){
					builder = builder.append(String.format("%02X", scanRecord[i]));
				}
				/*share = context.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE);
				if(share.getString(Constants.DEVICE_MAC, null) != device.getAddress()){
					callBack.blueCallBack(null);
					return;
				}*/
				//if(builder.toString().contains(SERVICE_UUID)){
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("device_name", device.getName());
					hashMap.put("device_address", device.getAddress());
					hashMap.put("device_RSSI", "" + rssi);
					callBack.blueCallBack(hashMap,device);
				//}
			}
		};
	}

	// 扫描
	public void scanLeDevice(boolean enable) {
		if (enable && !isStart) {
			mBluetoothAdapter.startLeScan(mLeScanCallback);
			isStart = true;
			// 经过一个预定义的扫描期间停止扫描。
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if(isStart){
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						isStart = false;
						callBack.blueCallBack();
					}
				}
			}, SCAN_PERIOD);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			isStart = false;
		}
	}

	public interface BlueCallBack {
		public void blueCallBack(HashMap<String, String> hashMap,BluetoothDevice device);
		public void blueCallBack();
	}
}