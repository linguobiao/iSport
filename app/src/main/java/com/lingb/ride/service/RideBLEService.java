package com.lingb.ride.service;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.lingb.global.Global;
import com.lingb.helper.CalculateHelper;

public class RideBLEService extends Service {

	private String TAG = "BLEService";

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;

	private String mBluetoothDeviceAddress;
	private String mBluetoothDeviceName = null;

	public String getDeviceAddress() {
		return mBluetoothDeviceAddress;
	}
	public String getDeviceName() {
		return mBluetoothDeviceName;
	}

	private int mConnectionState = STATE_DISCONNECTED;

	public int getConnectionState() {
		return mConnectionState;
	}
	
	private final UUID UUID_SERVICE = UUID.fromString("00001816-0000-1000-8000-00805f9b34fb");
	private final UUID UUID_CHARACTERISTIC_NOTI = UUID.fromString("00002a5b-0000-1000-8000-00805f9b34fb");
	private final UUID UUID_CHARACTERISTIC_WRITE_AND_READ = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
	private final UUID UUID_DESCRIPTOR_CONFIGURATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	private final UUID UUID_BATTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
	private final UUID UUID_BATTERY_CHARACTRISTIC = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

	public static final int STATE_DISCONNECTED = 0;
	public static final int STATE_CONNECTING = 1;
	public static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = Global.PACKAGE_NAME + ".bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_CONNECTED_FAIL = Global.PACKAGE_NAME + ".bluetooth.le.ACTION_GATT_CONNECTED_FAIL";
	public final static String ACTION_GATT_DISCONNECTED = Global.PACKAGE_NAME + ".bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_DEVICE_FOUND = Global.PACKAGE_NAME + ".bluetooth.le.ACTION_DEVICE_FOUND";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = Global.PACKAGE_NAME + ".bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = Global.PACKAGE_NAME + ".bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String ACTION_READ_REMOVE_RSSI = Global.PACKAGE_NAME + ".bluetooth.le.READ_REMOVE_RSSI";
	public final static String ACTION_WRITE_DESCRIPTOR = Global.PACKAGE_NAME + ".bluetooth.le.WRITE_DESCRIPTOR";
	public final static String ACTION_RECEIVE_DATA = Global.PACKAGE_NAME + ".bluetooth.le.ACTION_RECEIVE_DATA";
	public final static String ACTION_RECEIVE_BATTERY= Global.PACKAGE_NAME + ".bluetooth.le.ACTION_RECEIVE_BATTERY";
	public final static String ACTION_BOUND_MAC_CHANGED = Global.PACKAGE_NAME + ".bluetooth.le.ACTION_BOUND_MAC_CHANGED";

	public final static String ACTION_WRITE_DATE_TIME_SUCCESS = Global.PACKAGE_NAME + "ACTION_WRITE_DATE_TIME_SUCCESS";

	public final static String ACTION_RETURN_ACTIVITY_COUNT = Global.PACKAGE_NAME + "ACTION_RETURN_ACTIVITY_COUNT";
	public final static String ACTION_RETURN_ACTIVITY = Global.PACKAGE_NAME + "ACTION_RETURN_ACTIVITY";
	public final static String ACTION_RETURN_PRESS_KEY = Global.PACKAGE_NAME + "ACTION_RETURN_PRESS_KEY";
	public final static String ACTION_RETURN_BEACON_ZOON = Global.PACKAGE_NAME + "ACTION_RETURN_BEACON_ZOON";
	public final static String ACTION_RETURN_VERSION_RESULT = Global.PACKAGE_NAME + "bluetooth.le.ACTION_RETURN_VERSION_RESULT";

	public final static String KEY_RECEIVE_DATA = "UPADTEDATA";
	public final static String KEY_RSSI_VALUE = "KEY_RSSI_VALUE";
	

	public class LocalBinder extends Binder {
		public RideBLEService getService() {
			return RideBLEService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		close();
		MyApp.getIntance().mRideService = null;
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public void onCreate() {

		MyApp.getIntance().mRideService = this;
		initialize();
	}
	
	@Override
	public void onDestroy() {
		disconnect();
		MyApp.getIntance().mRideService = null;
		mBluetoothDeviceName = null;
		super.onDestroy();
	}

	/**
	 * 蓝牙回调函数
	 */
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			if (status == BluetoothGatt.GATT_SUCCESS) {

			}
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					Log.i(TAG, "Connected to GATT server.");
					mConnectionState = STATE_CONNECTED;
					broadcastUpdate(ACTION_GATT_CONNECTED);

					// Attempts to discover services after successful
					// connection.
					Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
				} else {
					mConnectionState = STATE_DISCONNECTED;
					mBluetoothDeviceName = null;
					disconnect();
					broadcastUpdate(ACTION_GATT_CONNECTED_FAIL);
				}
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				Log.i(TAG, "Disconnected from GATT server.");
				mConnectionState = STATE_DISCONNECTED;
				mBluetoothDeviceName = null;
				broadcastUpdate(ACTION_GATT_DISCONNECTED);
				close();
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.i(TAG, "onServicesDiscovered received: " + status);

			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
				set_notify_true(true);
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

			// Log.i(TAG, "onCharacteristicWrite");

			if (status == BluetoothGatt.GATT_SUCCESS) {
				System.out.println("GATT_SUCCESS");
				byte[] value = characteristic.getValue();
				int title = CalculateHelper.getByteValue(value[0]);
			}

		};

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			byte[] value = characteristic.getValue();
			int title = CalculateHelper.getByteValue(value[0]);
			Log.i("test", "value = " + Arrays.toString(value));
			broadcastUpdate(ACTION_RECEIVE_DATA, KEY_RECEIVE_DATA, value);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			byte[] value = characteristic.getValue();
			Log.i("test", "value = " + Arrays.toString(value));
			broadcastUpdate(ACTION_RECEIVE_BATTERY, KEY_RECEIVE_DATA, value);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_WRITE_DESCRIPTOR);
			}
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			// Log.i(TAG, "rssi value: " + rssi);
			broadcastRSSI(rssi);
		}
	};


	/**
	 * 开启广播
	 */
	public void set_notify_true(boolean isNotify) {
		setCharactoristicNotifyAndWriteDescriptor(mBluetoothGatt, UUID_SERVICE, UUID_CHARACTERISTIC_NOTI, UUID_DESCRIPTOR_CONFIGURATION, isNotify);
	}

	/**
	 * 发送广播
	 * 
	 * @param action
	 */
	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(String action, String key, byte[] value) {

		Intent intent = new Intent(action);
		intent.putExtra(key, value);
		sendBroadcast(intent);
	}

	private void broadcastRSSI(int rssi) {
		Intent intent = new Intent(ACTION_READ_REMOVE_RSSI);
		intent.putExtra(KEY_RSSI_VALUE, rssi);
		sendBroadcast(intent);
	}

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		if (mBluetoothAdapter == null) {
			mBluetoothAdapter = mBluetoothManager.getAdapter();
			if (mBluetoothAdapter == null) {
				Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
				return false;
			}
		}

		return true;
	}

	/**
	 * 初始化蓝牙
	 * 
	 * @param context
	 */
	public BluetoothAdapter initBluetooth_manual(Activity context, boolean isResume) {

		if (mBluetoothAdapter == null) {
			boolean isHaveBle = initialize();
			if (isHaveBle == false) {
				return null;
			}
		}

//		// 判断机器是否有蓝牙
//		if (!mBluetoothAdapter.isEnabled()) {
//			if (!isResume) {
//				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//				context.startActivityForResult(enableIntent, Global.REQUEST_ENABLE_BLUETOOTH);
//			}
//		}
		return mBluetoothAdapter;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address, boolean is) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device. Try to reconnect. (先前连接的设备。 尝试重新连接)
		// if (mBluetoothDeviceAddress != null
		// && address.equals(mBluetoothDeviceAddress)
		// && mBluetoothGatt != null) {
		// Log.d(TAG,
		// "Trying to use an existing mBluetoothGatt for connection.");
		// if (mBluetoothGatt.connect()) {
		// mConnectionState = STATE_CONNECTING;
		// return true;
		// } else {
		// return false;
		// }
		// }

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mBluetoothDeviceName = device.getName();
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		Log.i("test", "***********disconnect ");
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized " + mBluetoothAdapter + ",   " + mBluetoothGatt);
			return;
		}
		if (mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
		}
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
//		mBluetoothGatt = null;
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}

	/**
	 * Read the RSSI for a connected remote device.
	 * */
	public boolean getRssiVal() {
		if (mBluetoothGatt == null)
			return false;

		return mBluetoothGatt.readRemoteRssi();
	}

	/**
	 * 写特征值
	 * 
	 * @param characteristic
	 */
	public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {

		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		mBluetoothGatt.writeCharacteristic(characteristic);

	}

	public void writeCharacteristic(BluetoothGatt mBluetoothGatt, UUID uuid_service, UUID uuid_character, byte[] value) {
		// service
		BluetoothGattService mBluetoothGattService = getBluetoothGattService(mBluetoothGatt, uuid_service);
		// characteristic
		BluetoothGattCharacteristic mBluetoothGattCharacteristic = getBluetoothGattCharacteristic(mBluetoothGattService, uuid_character);

		if (mBluetoothGatt != null && mBluetoothGattCharacteristic != null) {
			mBluetoothGattCharacteristic.setValue(value);
			mBluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);

		} else if (mBluetoothGatt == null) {
			Log.i(TAG, "mBluetoothGatt is null");
		} else if (mBluetoothGattCharacteristic == null) {
			Log.i(TAG, "mBluetoothGattCharacteristic is null");
		}
	}

	public void writeDescriptor(BluetoothGatt mBluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
	}
	
	public void get_battery() {// service
				readCharacteristic(mBluetoothGatt, UUID_BATTERY_SERVICE,UUID_BATTERY_CHARACTRISTIC);
	}
	
	public void readCharacteristic(BluetoothGatt mBluetoothGatt, UUID uuid_service, UUID uuid_character) {
		// service
		BluetoothGattService mBluetoothGattService = getBluetoothGattService(mBluetoothGatt, uuid_service);
		// characteristic
		BluetoothGattCharacteristic mBluetoothGattCharacteristic = getBluetoothGattCharacteristic(mBluetoothGattService, uuid_character);

		if (mBluetoothGatt != null && mBluetoothGattCharacteristic != null) {
			mBluetoothGatt.readCharacteristic(mBluetoothGattCharacteristic);

		} else if (mBluetoothGatt == null) {
			Log.i(TAG, "mBluetoothGatt is null");
		} else if (mBluetoothGattCharacteristic == null) {
			Log.i(TAG, "mBluetoothGattCharacteristic is null");
		}
	}

	/**
	 * 读特征值
	 * 
	 * @param characteristic
	 */
	public void readCharacteristic(BluetoothGatt mBluetoothGatt, BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
	}

	/**
	 * 设置通知属性为true，并写上descriptor
	 * 
	 * @param uuid_service
	 * @param uuid_characteristic
	 * @param uuid_descriptor
	 */
	public void setCharactoristicNotifyAndWriteDescriptor(BluetoothGatt mBluetoothGatt, UUID uuid_service, UUID uuid_characteristic, UUID uuid_descriptor, boolean isNotify) {
		// service
		BluetoothGattService mBluetoothGattService = getBluetoothGattService(mBluetoothGatt, uuid_service);
		// characteristic
		BluetoothGattCharacteristic mBluetoothGattCharacteristic = getBluetoothGattCharacteristic(mBluetoothGattService, uuid_characteristic);

		if (mBluetoothGatt != null && mBluetoothGattCharacteristic != null) {
			// 设置
			mBluetoothGatt.setCharacteristicNotification(mBluetoothGattCharacteristic, isNotify);

			BluetoothGattDescriptor bluetoothGattDescriptor = mBluetoothGattCharacteristic.getDescriptor(uuid_descriptor);
			if (bluetoothGattDescriptor != null) {
				bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
				mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
			}

		} else if (mBluetoothGatt == null) {
			Log.i(TAG, "mBluetoothGatt is null");
		} else if (mBluetoothGattCharacteristic == null) {
			Log.i(TAG, "mBluetoothGattCharacteristic is null");
		}
	}

	/**
	 * 设置通知属性为true，并写上descriptor
	 * 
	 * @param uuid_service
	 * @param uuid_characteristic
	 * @param uuid_descriptor
	 */
	public void setCharactoristicNotify(UUID uuid_service, UUID uuid_characteristic) {
		// service
		BluetoothGattService mBluetoothGattService = getBluetoothGattService(mBluetoothGatt, uuid_service);
		// characteristic
		BluetoothGattCharacteristic mBluetoothGattCharacteristic = getBluetoothGattCharacteristic(mBluetoothGattService, uuid_characteristic);

		if (mBluetoothGatt != null && mBluetoothGattCharacteristic != null) {
			// 设置
			mBluetoothGatt.setCharacteristicNotification(mBluetoothGattCharacteristic, true);

		} else if (mBluetoothGatt == null) {
			Log.i(TAG, "mBluetoothGatt is null");
		} else if (mBluetoothGattCharacteristic == null) {
			Log.i(TAG, "mBluetoothGattCharacteristic is null");
		}
	}

	/**
	 * 获取bluetoothGattService
	 * 
	 * @param mBluetoothGatt
	 * @param UUID_SERVICE
	 * @return
	 */
	private BluetoothGattService getBluetoothGattService(BluetoothGatt mBluetoothGatt, UUID UUID_SERVICE) {
		if (mBluetoothGatt != null) {
			BluetoothGattService mBluetoothGattServer = mBluetoothGatt.getService(UUID_SERVICE);
			if (mBluetoothGattServer != null) {
				return mBluetoothGattServer;
			} else {
				Log.i(TAG, "getBluetoothGattService, bluetoothgatt get service uuid:" + UUID_SERVICE + " is null");
			}
		} else {
			Log.i(TAG, "mBluetoothGatt is null");
		}

		return null;
	}

	/**
	 * 获取bluetoothGattCharacteristic
	 * 
	 * @param mBluetoothGattService
	 * @param UUID_CHARACTERISTIC
	 * @return
	 */
	private BluetoothGattCharacteristic getBluetoothGattCharacteristic(BluetoothGattService mBluetoothGattService, UUID UUID_CHARACTERISTIC) {
		if (mBluetoothGattService != null) {
			BluetoothGattCharacteristic mBluetoothGattCharacteristic = mBluetoothGattService.getCharacteristic(UUID_CHARACTERISTIC);

			if (mBluetoothGattCharacteristic != null) {
				return mBluetoothGattCharacteristic;
			} else {
				Log.i(TAG, "getBluetoothGattCharacteristic, bluetoothGattServer get characteristic uuid:" + UUID_CHARACTERISTIC + " is null");
			}
		} else {
			Log.i(TAG, "mBluetoothGattServer is null");
		}

		return null;
	}

	/**
	 * 扫描设备
	 * 
	 * @param start
	 */
	public void scan(boolean start) {
		if (mBluetoothAdapter != null) {
			if (start) {
				mBluetoothAdapter.startLeScan(mLeScanCallback);
			} else {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		} else {
			Log.i(TAG, "bluetoothadapter is null");
		}
	}

	/**
	 * 扫描设备的回调方法
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			Bundle mBundle = new Bundle();
			mBundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, device);
			mBundle.putInt(BluetoothDevice.EXTRA_RSSI, rssi);

			Intent intent = new Intent();
			intent.setAction(ACTION_DEVICE_FOUND);
			intent.putExtras(mBundle);
			sendBroadcast(intent);
		}
	};

}
