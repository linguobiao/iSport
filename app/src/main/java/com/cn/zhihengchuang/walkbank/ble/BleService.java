/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cn.zhihengchuang.walkbank.ble;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.cn.zhihengchuang.walkbank.activity.call.CallListener;
import com.cn.zhihengchuang.walkbank.entity.BltModel;
import com.cn.zhihengchuang.walkbank.entity.PedometerModel;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.cn.zhihengchuang.walkbank.util.FormatTransfer;
import com.cn.zhihengchuang.walkbank.util.SystemConfig;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lingb.global.Global;
import com.lingb.helper.StringHelper;

/**
 * @author longke 蓝牙服务管理
 */
public class BleService extends Service {
	private final static String TAG = BleService.class.getSimpleName();
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	public String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private BluetoothGattService mGattService;
	public int mConnectionState = STATE_DISCONNECTED;
	private Runnable mCurrentTask;
	public static final int STATE_DISCONNECTED = 0;
	public static final int STATE_CONNECTING = 1;
	public static final int STATE_CONNECTED = 2;
	private int TIME = 10000;
	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_ERROR = "com.example.bluetooth.le.ACTION_GATT_ERROR";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_GATT_NOTIFICATION_OPEN = "com.example.bluetooth.le.ACTION_GATT_NOTIFICATION_OPEN";
	public final static String ACTION_GATT_NOTIFICATION_INEXISTENCE = "com.example.bluetooth.le.ACTION_GATT_NOTIFICATION_INEXISTENCE";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String ACTION_DEVICE_FOUND = "com.example.bluetooth.le.DEVICE_FOUND";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
	public final static String ACTION_STATUS_WRONG = "com.example.bluetooth.le.ACTION_STATUS_WRONG";
	public final static String ACTION_SETTING_OK = "com.example.bluetooth.le.ACTION_SETTING_OK";

	public static final UUID MAIN_SERVICE_DEFAULT = UUID.fromString("d0a2ff00-2996-d38b-e214-86515df5a1df");
	public static final UUID SEND_DATA_CHAR_DEFAULT = UUID.fromString("d0a2ff01-2996-d38b-e214-86515df5a1df");
	public static final UUID RECEIVE_DATA_CHAR_DEFAULT = UUID.fromString("d0a2ff02-2996-d38b-e214-86515df5a1df");
	// public static final UUID REALTIME_RECEIVE_DATA_CHAR = UUID
	// .fromString("d0a2ff03-2996-d38b-e214-86515df5a1df");
	public static final UUID REALTIME_RECEIVE_DATA_CHAR_DEFAULT = UUID.fromString("d0a2ff04-2996-d38b-e214-86515df5a1df");

	public static final UUID MAIN_SERVICE_ANCS = UUID.fromString("d0a2ff00-2996-d38b-0e14-86515df5a1df");
	public static final UUID SEND_DATA_CHAR_ANCS = UUID.fromString("7905FF01-B5CE-4E99-A40F-4B1E122D00D0");
	public static final UUID RECEIVE_DATA_CHAR_ANCS = UUID.fromString("7905FF02-B5CE-4E99-A40F-4B1E122D00D0");
	public static final UUID REALTIME_RECEIVE_DATA_CHAR_ANCS = UUID.fromString("7905FF04-B5CE-4E99-A40F-4B1E122D00D0");

	// ��֪ͨUUID
	public static final UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	
	public static UUID MAIN_SERVICE = MAIN_SERVICE_DEFAULT;
	public static UUID SEND_DATA_CHAR = SEND_DATA_CHAR_DEFAULT;
	public static UUID RECEIVE_DATA_CHAR = RECEIVE_DATA_CHAR_DEFAULT;
	// public static final UUID REALTIME_RECEIVE_DATA_CHAR = REALTIME_RECEIVE_DATA_CHAR;
	public static UUID REALTIME_RECEIVE_DATA_CHAR = REALTIME_RECEIVE_DATA_CHAR_DEFAULT;
	
	public int mCommand; // 当前的指令

	public static final int COMMAND_SYNC_SAVE = 0;
	public static final int COMMAND_ALARM = 1;
	public static final int COMMAND_EVENT = 2;
	public static final int COMMAND_WEARINFO = 3;

	// 产品出厂时清除
	public static final int COMMAND_CLEAR = 4;
	// 步距
	public static final int STEP_LENGTH = 5;
	// 久坐提醒
	public static final int LONG_TIME_SLEEP = 6;
	// 打开实时传输
	public static final int OPEN_REAL_TIME = 7;
	// 关闭实时传输
	public static final int CLOSE_REAL_TIME = 8;
	// 发送未接电话的个数
	public static final int UNREADPHONE_NUM = 9;
	// 发送未接短信的个数
	public static final int UNREADSMS_NUM = 10;
	// 发送来电号码
	public static final int COMMING_PHONE_NUBER = 11;
	public static final int isAutoConnect = 12;
	// 自动睡眠
	public static final int COMMAND_SLEEP = 111;
	// 设备显示
	public static final int COMMAND_DISPLAY = 112;
	private int checkSum;
	
	private String mBluetoothDeviceName = null;

	public String getDeviceName() {
		return mBluetoothDeviceName;
	}

	
	private String getDeviceName(String deviceName) {
		if (!TextUtils.isEmpty(deviceName)) {
			if (deviceName.contains("_")) {
				deviceName = deviceName.split("_")[0];
			}
			deviceName = StringHelper.replaceDeviceNameToCC431(deviceName);
		}
		return deviceName;
	}
	
	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		// 连接状态
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					intentAction = ACTION_GATT_CONNECTED;
					mConnectionState = STATE_CONNECTED;
					DbUtils db = DbUtils.create(getApplicationContext());
					try {
						db.deleteAll(BltModel.class);

					} catch (DbException e) {
						e.printStackTrace();
					}
					broadcastUpdate(intentAction);
					Log.i(TAG, "Connected to GATT server.");
					Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
					MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue(gatt.getDevice().getAddress());
					String deviceName = getDeviceName(gatt.getDevice().getName());
					MyApp.getIntance().getAppSettings().LAST_CONNECT_NAME.setValue(deviceName);
					db.configAllowTransaction(true);
					db.configDebug(true);
					BltModel blt = new BltModel();
					blt.setHardtype(deviceName);
					blt.setBltname(deviceName);
					blt.setBltid(gatt.getDevice().getAddress());

					try {
						WhereBuilder builder = WhereBuilder.b("bltid", "==", gatt.getDevice().getAddress());
						BltModel pedometer = db.findFirst(Selector.from(BltModel.class).where(builder));
						if (pedometer == null) {
							db.saveBindingId(blt);
							broadcastUpdate(Constants.CONNECTING_DEVICE);
						}

					} catch (DbException e) {
						e.printStackTrace();
					}

					// Attempts to discover services after successful
					// connection.

				} else {
					mBluetoothDeviceName = null;
					// ���ִ�����������жϴ������͡�һ����Ҫ����
					// 133����һ�����������������Ҫ�Ļ���
					Log.w(TAG, "onConnectionStateChange: " + status);
					operateStatusWrong();
					disconnect();
					// initialize();
					/*
					 * if(mBluetoothAdapter!=null){ mBluetoothAdapter.disable();
					 * mBluetoothAdapter.enable(); }
					 */

					// connect(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString());
				}
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				mBluetoothDeviceName = null;
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				close();
				Log.i(TAG, "Disconnected from GATT server.");
				broadcastUpdate(intentAction);
			}
		}

		// 发现服务
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
				mGattService = null;
				if (mBluetoothGatt.getService(MAIN_SERVICE_ANCS) != null) {
					MAIN_SERVICE = MAIN_SERVICE_ANCS;
					mGattService = mBluetoothGatt.getService(MAIN_SERVICE_ANCS);
				} else if (mBluetoothGatt.getService(MAIN_SERVICE_DEFAULT) != null) {
					MAIN_SERVICE = MAIN_SERVICE_DEFAULT;
					mGattService = mBluetoothGatt.getService(MAIN_SERVICE_DEFAULT);
				} else {
					broadcastUpdate(ACTION_GATT_NOTIFICATION_INEXISTENCE);
					disconnect();
				}

				if (mGattService != null) {
					// ComMandContoller.sendGetHostoryDateCommand(SEND_DATA_CHAR,
					// mGattService, gatt);
					BluetoothGattCharacteristic mCharac;
					mCharac = mGattService.getCharacteristic(SEND_DATA_CHAR_ANCS);
					if (mCharac == null) {
						SEND_DATA_CHAR = SEND_DATA_CHAR_DEFAULT;
						mCharac = mGattService.getCharacteristic(SEND_DATA_CHAR);
					} else {
						SEND_DATA_CHAR = SEND_DATA_CHAR_ANCS;
					}
					enableNotification(mCharac);

					BluetoothGattCharacteristic mCharac_RECEIVE_DATA_CHAR;
					mCharac_RECEIVE_DATA_CHAR = mGattService.getCharacteristic(RECEIVE_DATA_CHAR_ANCS);
					if (mCharac_RECEIVE_DATA_CHAR == null) {
						RECEIVE_DATA_CHAR = RECEIVE_DATA_CHAR_DEFAULT;
					} else {
						RECEIVE_DATA_CHAR = RECEIVE_DATA_CHAR_ANCS;
					}

					BluetoothGattCharacteristic mCharac_REALTIME_RECEIVE_DATA_CHAR;
					mCharac_REALTIME_RECEIVE_DATA_CHAR = mGattService.getCharacteristic(REALTIME_RECEIVE_DATA_CHAR_ANCS);
					if (mCharac_REALTIME_RECEIVE_DATA_CHAR == null) {
						REALTIME_RECEIVE_DATA_CHAR = REALTIME_RECEIVE_DATA_CHAR_DEFAULT;
					} else {
						REALTIME_RECEIVE_DATA_CHAR = REALTIME_RECEIVE_DATA_CHAR_ANCS;
					}

					Log.i(TAG, "onServicesDiscovered*********** 1");
				}
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
				operateStatusWrong();

			}
		}

		// 电量特征数据读取
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				// broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic,gatt);
			} else {
				operateStatusWrong();
			}
		}

		// 特征数据读取
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			/*
			 * Log.i(TAG, "特征"); final StringBuilder stringBuilder = new
			 * StringBuilder( characteristic.getValue().length);
			 * 
			 * for (byte byteChar : characteristic.getValue()) {
			 * stringBuilder.append(String.format("%02X ", byteChar)); }
			 * if(!tempData.equals(stringBuilder.toString().trim())){
			 * tempData=stringBuilder.toString().trim();
			 */
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, gatt);
			/*
			 * }else{ tempData=stringBuilder.toString().trim(); }
			 */

		}

		public void onDescriptorWrite(final BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			Log.w(TAG, "**** onDescriptorWrite  ****" + BluetoothGatt.GATT_SUCCESS + "==" + status);

			if (status == BluetoothGatt.GATT_SUCCESS) {
				final UUID uuid = descriptor.getCharacteristic().getUuid();
				// mBluetoothGatt = gatt;
//				BleService1.mLogList.add(Tools.defaultLongDateFormat(new Date()) + " :onDescriptorWrite----status=" + status);
				if (status == 0) {
					// ��ʼ����notify�ȿ���
					mCurrentTask = null;
					mCurrentTask = new Thread() {
						public void run() {

							String deviceName = getDeviceName(gatt.getDevice().getName());
//							if ("W240N".equals(gatt.getDevice().getName()) || "W194N".equals(gatt.getDevice().getName()) || "CC431".equals(gatt.getDevice().getName())) {
								if (MyApp.getIntance().list_device_new.contains(deviceName) || "W194N".equals(getDeviceName(deviceName))) {
									if (uuid.equals(SEND_DATA_CHAR)) {
										Log.i(TAG, "打开通道");
										if (mCache != null) {
											mCache.clear();
										}
										if (mCommand == BleService.COMMAND_WEARINFO) {
											ComMandContoller.sendLeftOrRight(SEND_DATA_CHAR, mGattService, gatt, share);
										} else if (mCommand == BleService.COMMAND_CLEAR) {
											ComMandContoller.sendClearAll(SEND_DATA_CHAR, mGattService, gatt);
										} else if (mCommand == BleService.LONG_TIME_SLEEP) {
											sendLongTimeSleep();
										} else if (mCommand == BleService.OPEN_REAL_TIME) {
											sendCommandRealTime(true);
										} else if (mCommand == BleService.CLOSE_REAL_TIME) {
											sendCommandRealTime(false);
										} else if (mCommand == BleService.STEP_LENGTH) {
											sendStepLength();
										} else if (mCommand == BleService.COMMAND_ALARM) {
											sendAlarmTime();
										} else if (mCommand == BleService.UNREADSMS_NUM) {
											// sendAlarmTime();
											sendUnreadSmsCount();
										} else if (mCommand == BleService.UNREADPHONE_NUM) {
											// sendAlarmTime();
											sendUnReadPhoneCount();
										} else if (mCommand == BleService.COMMING_PHONE_NUBER) {
											// sendAlarmTime();
											sendCommingPhoneNumber();
										} else if (mCommand == BleService.COMMAND_SYNC_SAVE) {
											if (share.getBoolean(gatt.getDevice().getAddress() + "isFirstInstall", true)) {
												ComMandContoller.sendFristTimeCommand(SEND_DATA_CHAR, mGattService, mBluetoothGatt, share);
												share.edit().putBoolean(gatt.getDevice().getAddress() + "isFirstInstall", false).commit();
											} else {

												boolean isNewBound = share.getBoolean(Global.KEY_IS_NEW_BOUND, true);
												if (isNewBound) {
													ComMandContoller.sendFristTimeCommand(SEND_DATA_CHAR, mGattService, mBluetoothGatt, share);
													share.edit().putBoolean(Global.KEY_IS_NEW_BOUND, false).commit();
												} else {
													SendTimeCommand();
												}

											}
											/*
											 * if(System.currentTimeMillis()-tempTime
											 * >2000 ){ BluetoothGattCharacteristic
											 * mCharac = mGattService
											 * .getCharacteristic
											 * (RECEIVE_DATA_CHAR);
											 * enableNotification(mCharac); }
											 * tempTime=System.currentTimeMillis();
											 */
										}

										// ComMandContoller.sendGetHostoryDateCommand(SEND_DATA_CHAR,
										// mGattService, gatt);

									} else if (uuid.equals(RECEIVE_DATA_CHAR)) {
										// mCache = new ArrayList<Byte>();
										Log.i(TAG, "haode");
										String hostoryDate = share.getString(gatt.getDevice().getAddress() + "hostory", DateUtil.getCurrentDate());
										ComMandContoller.sendGetFromDateDataCommand(SEND_DATA_CHAR, mGattService, gatt, hostoryDate);

									} else if (uuid.equals(REALTIME_RECEIVE_DATA_CHAR)) {
										if (mCache != null) {
											mCache.clear();
										}
										// SendTimeCommand();
									}
//								}
								
							} else if ("BW001".equals(deviceName)) {

							} else {
								if (uuid.equals(SEND_DATA_CHAR)) {
									if (mCommand == BleService.COMMAND_SYNC_SAVE) {
										oldSyncSaveTime();
									} else if (mCommand == BleService.LONG_TIME_SLEEP) {
										sendLongTimeSleep();
									} else if (mCommand == BleService.COMMAND_ALARM) {
										sendAlarmTime();
									} else if (mCommand == BleService.COMMAND_CLEAR) {
										OldClear();
										/*
										 * ComMandContoller.sendClearAll(
										 * SEND_DATA_CHAR, mGattService, gatt);
										 */
									} else if (mCommand == BleService.COMMAND_WEARINFO) {
										OldComMandContoller.sendLeftOrRight(SEND_DATA_CHAR, mGattService, gatt, share);
									}
								} else if (uuid.equals(RECEIVE_DATA_CHAR)) {
									mCache = null;
									ComMandContoller.sendCommand(SEND_DATA_CHAR, mGattService, gatt, new byte[] { 0x06, 0x01, 0x00 });
								}

							}
							/*
							 * if
							 * (uuid.equals(SEND_DATA_CHAR)&&TextUtils.isEmpty
							 * (MyApp
							 * .getIntance().getAppSettings().CALIBRATION_TIME
							 * .getValue().toString())) {
							 * ComMandContoller.sendTimeCommand(uuid,
							 * mGattService, gatt); }else
							 * if(uuid.equals(SEND_DATA_CHAR
							 * )&&MyApp.getIntance()
							 * .getAppSettings().FRIST_HOSTORY.getValue()){
							 * ComMandContoller.sendGetHostoryDateCommand(uuid,
							 * mGattService, gatt); }
							 */
							// ComMandContoller.sendSleepAlertCommand(uuid,
							// mGattService, gatt);
							/*
							 * ComMandContoller.sendTimeCommand(uuid,
							 * mGattService, gatt);
							 */
							/*
							 * ComMandContoller.sendGetHostoryDateCommand(uuid,
							 * mGattService, gatt);
							 */

						}

					};
					mHandler.postDelayed(mCurrentTask, 300);
				} else {
					// Log.e("onDescriptorWrite  enable error : " + status);
					// sendErrorMessage(status);
				}
			} else {
				operateStatusWrong();
			}
		};

		// write data success callback
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

		};

		// ��ȡ���ź�ǿ��
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

		};

	};

	private boolean isSync = false;

	public void setIsSync(boolean isSync) {
		this.isSync = isSync;
	}

	public void SendTimeCommand() {
		isSync = true;
		ComMandContoller.sendTimeCommand(SEND_DATA_CHAR, mGattService, mBluetoothGatt);
	}

	/**
	 * 恢复出厂设置
	 * 
	 * @param gatt
	 */
	public void OldClear() {
		ComMandContoller.sendCommand(SEND_DATA_CHAR, mGattService, mBluetoothGatt, new byte[] { 0x0e, 0x01, 0x00 });
	}

	/**
	 * 发送步距
	 */
	public void sendStepLength() {
		ComMandContoller.sendStepLength(SEND_DATA_CHAR, mGattService, mBluetoothGatt, share);
	}

	/**
	 * 发送未读短信数量
	 */
	public void sendUnreadSmsCount() {
		ComMandContoller.sendUnreadSmsCount(SEND_DATA_CHAR, mGattService, mBluetoothGatt, share);
	}

	/**
	 * 发送未读电话数量
	 */
	public void sendUnReadPhoneCount() {
		ComMandContoller.sendUnreadPhoneCount(SEND_DATA_CHAR, mGattService, mBluetoothGatt, share);
	}

	/**
	 * 发送来电号码
	 */
	public void sendCommingPhoneNumber() {
		ComMandContoller.sendPhoneNum(SEND_DATA_CHAR, mGattService, mBluetoothGatt, share);
	}

	/**
	 * 打开实时开关
	 */
	public void sendCommandRealTime(boolean isOpen) {
		ComMandContoller.sendRealTime(SEND_DATA_CHAR, mGattService, mBluetoothGatt, isOpen);
	}

	// /**
	// * 关闭实时开关
	// */
	// public void CloseRealTime() {
	// ComMandContoller.sendRealTime(SEND_DATA_CHAR, mGattService,
	// mBluetoothGatt, false);
	// }

	/**
	 * 久坐提醒
	 * 
	 * @param gatt
	 */
	public void sendLongTimeSleep() {
		// if ("W240N".equals(mBluetoothGatt.getDevice().getName()) ||
		// "CC431".equals(mBluetoothGatt.getDevice().getName())) {
		if (MyApp.getIntance().list_device_new.contains(getDeviceName(mBluetoothGatt.getDevice().getName()))) {
			ComMandContoller.sendLongSleep(SEND_DATA_CHAR, mGattService, mBluetoothGatt, share);
		}

		// }
		else {
			OldComMandContoller.sendLongSleep(SEND_DATA_CHAR, mGattService, mBluetoothGatt, share);
		}

	}
	
	/**
	 * 自动睡眠
	 * @param gatt
	 */
	public boolean sendAutoSleep() {
		if (MyApp.getIntance().list_device_new.contains(getDeviceName(mBluetoothGatt.getDevice().getName()))) {
			ComMandContoller.sendAutoSleep(SEND_DATA_CHAR, mGattService, mBluetoothGatt);
			return true;
		} else{
			return false;
		}
	}
	
	/**
	 * 设备显示
	 * @param gatt
	 */
	public boolean sendDeviceDisplay() {
		if (MyApp.getIntance().list_device_new.contains(getDeviceName(mBluetoothGatt.getDevice().getName()))) {
			ComMandContoller.sendDeviceDisplay(SEND_DATA_CHAR, mGattService, mBluetoothGatt);
			return true;
		} else{
			return false;
		}

	}

	/**
	 * 发送闹钟提醒时间
	 * 
	 */
	public void sendAlarmTime() {

		// if ("W240N".equals(mBluetoothGatt.getDevice().getName()) ||
		// "CC431".equals(mBluetoothGatt.getDevice().getName())) {
		if (MyApp.getIntance().list_device_new.contains(getDeviceName(mBluetoothGatt.getDevice().getName()))) {
			ComMandContoller.sendAlarmTime(SEND_DATA_CHAR, mGattService, mBluetoothGatt, share);
			// }

		} else {
			OldComMandContoller.sendAlarmTime(SEND_DATA_CHAR, mGattService, mBluetoothGatt, share);
		}

	}
 
	/**
	 * 老协议同步时间
	 * 
	 * @param gatt
	 */
	public void oldSyncSaveTime() {
		OldComMandContoller.sendTimeCommand(SEND_DATA_CHAR, mGattService, mBluetoothGatt, share);
	}

	// 蓝牙扫描回调.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			Log.i(TAG, "find device=" + device.getAddress() + ", rssi=" + rssi);
			Bundle data = new Bundle();
			data.putParcelable(BluetoothDevice.EXTRA_DEVICE, device);
			data.putInt("rssi", rssi);
			Intent i = new Intent(ACTION_DEVICE_FOUND);
			i.putExtras(data);
			sendBroadcast(i);
		}
	};

	private Handler mHandler;
	private ArrayList<Byte> mCache;
	private SharedPreferences share;

	private void operateStatusWrong() {
		broadcastUpdate(ACTION_GATT_ERROR);
		close();

	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("bleservice", "bleservice oncreadte");
		MyApp.getIntance().mService = this;
		initialize();
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
//		mHandler = new Handler();
//		registerObserver();
//		registerBoradcastReceiver();
//		handler.postDelayed(runnable, TIME); // 每隔1s执行
//		TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		CallListener customPhoneListener = new CallListener(BleService.this);
//		telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	@Override
	public void onDestroy() {
		Log.i("bleservice", "bleservice ondestory");
		disconnect();
		MyApp.getIntance().mService = null;
//		unregisterReceiver(mBroadcastReceiver);
		mBluetoothDeviceName = null;
		super.onDestroy();
	}


	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic, final BluetoothGatt gatt) {
		final byte[] data = characteristic.getValue();
		String deviceName = getDeviceName(mBluetoothGatt.getDevice().getName());
//		if ("W240N".equals(gatt.getDevice().getName()) || "CC431".equals(gatt.getDevice().getName())) {
			if (MyApp.getIntance().list_device_new.contains(deviceName)) {
				if (characteristic.getUuid().equals(SEND_DATA_CHAR)) {
					// processData(data);
					if (data != null && data.length > 0) {
						final StringBuilder stringBuilder = new StringBuilder(data.length);

						for (byte byteChar : data) {
							stringBuilder.append(String.format("%02X ", byteChar));
						}
						Log.i(TAG, "第一通道" + stringBuilder.toString().trim());
						// 发送完计步/睡眠数据后
						if ("DE 02 01 ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "发送完计步/睡眠数据后");
							PedometerModel ped = null;
							if (mCache != null) {
								ped = ParserLoader.processData(mCache, getApplicationContext(), gatt);
								mCache.clear();
							}
							mCache = null;
							if (!share.getBoolean(gatt.getDevice().getAddress() + DateUtil.getCurrentDate() + "istongbu", false)) {
								if (ped != null && ped.getDatestring().equals(DateUtil.getCurrentDate())) {

									share.edit().putBoolean(gatt.getDevice().getAddress() + DateUtil.getCurrentDate() + "istongbu", true).commit();
								}

								String hostoryDate = share.getString(gatt.getDevice().getAddress() + "hostory", DateUtil.getCurrentDate());
								ComMandContoller.sendGetFromDateDataCommand(SEND_DATA_CHAR, mGattService, gatt, hostoryDate);

							}
						}
						// 无计步/睡眠数据数据
						if ("DE 02 01 06".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "无计步/睡眠数据数据");
							/*
							 * Intent intent = new Intent(Constants.UPDATE_OK);
							 * sendBroadcast(intent);
							 */
							String hostoryDate = share.getString(gatt.getDevice().getAddress() + "hostory", DateUtil.getCurrentDate());
							int state = DateUtil.compare_date(hostoryDate, DateUtil.getCurrentDate());
							if (state == -1) {
								SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
								try {
									share.edit().putString(gatt.getDevice().getAddress() + "hostory", df.format(DateUtil.dateAddDay(df.parse(hostoryDate), 1))).commit();
								} catch (ParseException e) {
									e.printStackTrace();
								}
							} else if (state == 0) {
								share.edit().putBoolean(gatt.getDevice().getAddress() + DateUtil.getCurrentDate() + "istongbu", true).commit();
							}
							hostoryDate = share.getString(gatt.getDevice().getAddress() + "hostory", DateUtil.getCurrentDate());
							ComMandContoller.sendGetFromDateDataCommand(SEND_DATA_CHAR, mGattService, gatt, hostoryDate);
						}
						// 设置左右手成功
						if ("DE 01 0B ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "设置左右手成功");
							broadcastUpdate(ACTION_SETTING_OK);
						}
						// 关闭实时同步成功
						if ("DE 02 04 ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "关闭实时同步成功");
							broadcastUpdate(ACTION_SETTING_OK);
						}
						// 开户实时同步成功 **********************
						if ("DE 02 03 ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "开户实时同步成功");
							broadcastUpdate(ACTION_SETTING_OK);
						}
						// 发送事件信息成功
						if ("DE 01 0C ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "发送事件信息成功");
							broadcastUpdate(BleController.ACTION_REMINDER_OK);
						}
						if ("DE 01 03 ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "发送目标步数、步距、睡眠目标成功");
							MyApp.getIntance().mService.sendAutoSleep();
						}
						// 发送自动睡眠成功
						if ("DE 01 07 ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "发送自动睡眠成功");
							broadcastUpdate(BleController.ACTION_AUTO_SLEEP_OK);
						}
						// 发送设备显示成功
						if ("DE 01 08 ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "发送设备显示成功");
							broadcastUpdate(BleController.ACTION_DISPLAY_OK);
						}
						// 设置日期、时间、时区成功 或者 设置公英制成功
						if ("DE 01 02 ED".equals(stringBuilder.toString().trim()) || "DE 01 01 ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "设置日期、时间、时区成功 或者 设置公英制成功");
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									// ComMandContoller.sendGetHostoryDateCommand(
									// SEND_DATA_CHAR, mGattService, gatt);
									// 开始同步
									startSync(gatt);
								}
							}, 300);
						}
						// 开户实时同步成功 ***********************
						if ("DE 02 03 ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "开户实时同步成功");
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									BluetoothGattCharacteristic mCharac1 = mGattService.getCharacteristic(REALTIME_RECEIVE_DATA_CHAR);
									enableNotification4(mCharac1);
								}
							}, 1000);
						}
						// 设置闹钟成功
						if ("DE 01 09 ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "设置闹钟成功");
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									broadcastUpdate(BleController.ACTION_ALARM_OK);
								}
							}, 1000);
						}
						// 发送来电提醒成功
						if ("DE 06 02 ED".equals(stringBuilder.toString().trim())) {
							Log.i("sync", "发送来电提醒成功");
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									ComMandContoller.sendPhoneName(SEND_DATA_CHAR, mGattService, gatt, share);
								}
							}, 1000);
						}
						// 请求传输历史数据开始日期和结束日期成功
						if (stringBuilder.toString().trim().startsWith("DE 02 05 FB")) {
							Log.i("sync", "请求传输历史数据开始日期和结束日期成功");
							ArrayList<Byte> arrayData = new ArrayList<Byte>();
							int rlength = data.length;
							for (int i = 0; i < rlength; i++) {
								arrayData.add(data[i]);
							}
							PedometerModel model = new PedometerModel();
							model = ParserLoader.parserDate(arrayData, getApplicationContext(), gatt, false, model);
							if (DateUtil.compare_date(model.getDatestring(), share.getString(gatt.getDevice().getAddress() + "hostory", model.getDatestring())) != -1) {
								share.edit().putString(gatt.getDevice().getAddress() + "hostory", model.getDatestring()).commit();
							}
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									BluetoothGattCharacteristic mCharac = mGattService.getCharacteristic(RECEIVE_DATA_CHAR);
									enableNotification(mCharac);
									Log.i(TAG, "broadcastUpdate*********** 2");
								}
							}, 300);
						}
						/*
						 * if
						 * ("DE 02 05 ED".equals(stringBuilder.toString().trim())) {
						 * mHandler.postDelayed(new Runnable() {
						 * 
						 * @Override public void run() {
						 * broadcastUpdate(BleController.ACTION_ALARM_OK); }
						 * },1000); }
						 */
					}
					// bytesToHexString(data);
				} else if (characteristic.getUuid().equals(RECEIVE_DATA_CHAR)) {

					if (mCache == null)
						mCache = new ArrayList<Byte>();
					if (data != null) {
						int rlength = data.length;
						for (int i = 0; i < rlength; i++) {
							mCache.add(data[i]);
						}
						// ˢ�½���
						// sendMessage(BleController.RECEIVE_DATA_CURRENT, 1);
						// }
					}
					if (data != null && data.length > 0) {
						final StringBuilder stringBuilder = new StringBuilder(data.length);

						for (byte byteChar : data) {
							stringBuilder.append(String.format("%02X ", byteChar));
						}

						/*
						 * intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
						 * stringBuilder.toString());
						 */

//						String[] ss = stringBuilder.toString().split("");
						Log.i(TAG, "data.length" + data.length);
						Log.i(TAG, "重复** " + stringBuilder.toString().trim());
					}
					// mStatus = STATUS_DONE;
				} else if (characteristic.getUuid().equals(REALTIME_RECEIVE_DATA_CHAR)) {
					if (isSync)
						return;
					final StringBuilder stringBuilder = new StringBuilder(data.length);
					for (byte byteChar : data) {
						stringBuilder.append(String.format("%02X ", byteChar));
					}
					Log.i(TAG, "第三通道" + stringBuilder.toString().trim());
					if (stringBuilder.toString().trim().startsWith("DE 02 01 FE")) {

						mCache = new ArrayList<Byte>();
						Log.i("chart", "********** data = " + Arrays.toString(data));
						int rlength = data.length;
						for (int i = 0; i < rlength; i++) {
							mCache.add(data[i]);
						}
						if (mCache != null) {
							ParserLoader.processRealTimeData(mCache, getApplicationContext(), gatt);
							mCache.clear();
						}
						mCache = null;

					}
				}

//			}
			
		} else {

			final StringBuilder stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data) {
				stringBuilder.append(String.format("%02X ", byteChar));
			}
			Log.i(TAG, "老协议》》》》》" + stringBuilder.toString().trim());
			if (characteristic.getUuid().equals(SEND_DATA_CHAR)) {

				if ("86 00 01 01".equals(stringBuilder.toString().trim())) {
					BluetoothGattCharacteristic ff02 = mGattService.getCharacteristic(RECEIVE_DATA_CHAR);
					enableNotification(ff02);
					Log.i(TAG, "***********老******3");
				}
				if ("86 00 0D 01".equals(stringBuilder.toString().trim())) {
					disconnect();
					broadcastUpdate(BleController.ACTION_REMINDER_OK);
				}
				if ("86 00 0A 01".equals(stringBuilder.toString().trim())) {
					broadcastUpdate(BleController.ACTION_ALARM_OK);
					disconnect();
				}
				if ("86 AA 07 06".equals(stringBuilder.toString().trim())) {
					disconnect();
				}
				if ("86 00 07 06".equals(stringBuilder.toString().trim())) {
					disconnect();
				}
				if ("86 00 0E 01".equals(stringBuilder.toString().trim())) {
					broadcastUpdate(Constants.OLD_UPDATE_OK);
					disconnect();
				}
				if ("86 00 0B 01".equals(stringBuilder.toString().trim())) {
					sendBroadcast(new Intent(BleController.ACTION_WEARINFO_OK));
					disconnect();
				}
				if ("86 00 0C 01".equals(stringBuilder.toString().trim())) {
					sendBroadcast(new Intent(BleController.ACTION_WEARINFO_OK));
					disconnect();
				}
				if ("86 AA 07 01".equals(stringBuilder.toString().trim())) {
					broadcastUpdate(Constants.CLEAR_DEVICE);
					if (OldParserLoader.processDataUpload(getBaseContext(), gatt, mCache, checkSum, SystemConfig.TYPEW240)) {
						mCache = null;
						this.checkSum = 0;
						ComMandContoller.sendCommand(SEND_DATA_CHAR, mGattService, gatt, new byte[] { 0x0e, 0x01, 0x00 });

					} else {
						// 同步成功 数据有错
						this.disconnect();
						broadcastUpdate(Constants.SYNCHRONOUS_FAILURE);

					}
					;

				}
				if (stringBuilder.toString().trim().startsWith("86 00 06 01")) {
					/*
					 * byte[] bytes = new byte[2]; bytes[0] = data[4]; bytes[1]
					 * = data[5]; this.checkSum=byteArrayToInt(bytes);
					 */
					byte[] checksum = new byte[2];
					System.arraycopy(data, 4, checksum, 0, 2);
					int sum = FormatTransfer.lBytesToShort(checksum) & 0x0000ffff;
					this.checkSum = sum;
					ComMandContoller.sendCommand(SEND_DATA_CHAR, mGattService, gatt, new byte[] { 0x07, 0x01, 0x00 });
					// Log.d("sum-------->" + sum);
				}
				if (stringBuilder.toString().trim().equals("86 00 14 01")) {
					disconnect();
					broadcastUpdate(Constants.CLEAR_DEVICE);
				}
				if (stringBuilder.toString().trim().equals("86 00 0C 01") || stringBuilder.toString().trim().equals("86 00 0B 01")) {
					disconnect();
					broadcastUpdate(Constants.CLEAR_DEVICE);
				}

				// processData(data);
			} else if (characteristic.getUuid().equals(RECEIVE_DATA_CHAR)) {
				/*
				 * final StringBuilder stringBuilder = new
				 * StringBuilder(data.length); for (byte byteChar : data) {
				 * stringBuilder.append(String.format("%02X ", byteChar)); }
				 */
				// Log.i(TAG, "老协议》》》》》" + stringBuilder.toString().trim());

				// ����ͬ�����ݣ����������Ȼ��浽�ڴ��У����д���
				if (mCache == null)
					mCache = new ArrayList<Byte>(checkSum + 20);
				if (data != null) {
					int rlength = data.length;
					// //�������7f ���ýص���һ���ֽ�
					// if ((data[0] & 0xff) > 0x7f){
					// for (int i = 0; i < rlength; i++)
					// mCache.add(data[i]);
					// }else{
					for (int i = 1; i < rlength; i++) {
						mCache.add(data[i]);
					}
					// ˢ�½���
					// }
				}
				// mStatus = STATUS_DONE;
			}

		}

	}

	private void startSync(BluetoothGatt gatt) {
		String hostoryDate = share.getString(gatt.getDevice().getAddress() + "hostory", "");
		if (TextUtils.isEmpty(hostoryDate)) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, -16);
			hostoryDate = DateUtil.getDate(c.getTime());
			share.edit().putString(gatt.getDevice().getAddress() + "hostory", hostoryDate).commit();
		}

		// ComMandContoller.sendGetFromDateDataCommand(
		// SEND_DATA_CHAR, mGattService, gatt,
		// hostoryDate);
		BluetoothGattCharacteristic mCharac = mGattService.getCharacteristic(RECEIVE_DATA_CHAR);
		enableNotification(mCharac);
		Log.i(TAG, "*****************5555");

	}

	/**
	 * byte[]转int
	 * 
	 * @param bytes
	 * @return
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		// 由高位到低位
		for (int i = 0; i < bytes.length; i++) {
			int shift = (bytes.length - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}

	protected String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
			stringBuilder.append(" ");
		}
		return stringBuilder.toString();
	}

	public class LocalBinder extends Binder {
		public BleService getService() {
			return BleService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * 初始化蓝牙适配器.
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

		mBluetoothAdapter = mBluetoothManager.getAdapter();

		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * 连接蓝牙服务
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null || TextUtils.isEmpty(address)) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

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
		System.out.println("device.getBondState==" + device.getBondState());
		return true;
	}

	public void autoConnect(final String address) {
		mCommand = OPEN_REAL_TIME;
		connect(address);
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		Log.i("test", "********** mBluetoothAdapter = " + mBluetoothAdapter + "   mBluetoothGatt = " + mBluetoothGatt);
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initializedf");
			return;
		}
		// mBluetoothGatt.close();
		mBluetoothGatt.disconnect();

		// mConnectionState = STATE_DISCONNECTED;
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
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initializedd");
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
			Log.w(TAG, "BluetoothAdapter not initializeda");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

		// This is specific to Heart Rate Measurement.
		// if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
		// BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
		// UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		// descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		// mBluetoothGatt.writeDescriptor(descriptor);
		// }
	}

	public boolean enableNotification(BluetoothGattCharacteristic characteristic) {
		Log.i(TAG, "enableNotification------------>" + characteristic.getUuid());

		boolean result = mBluetoothGatt.setCharacteristicNotification(characteristic, true);
		Log.i(TAG, "setCharacteristicNotification===>" + result);
		BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID_DESCRIPTOR);

		if (clientConfig == null) {
			Log.e(TAG, "clientConfig is null");
			return false;
		}

		clientConfig.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);

		return mBluetoothGatt.writeDescriptor(clientConfig);
	}

	public boolean enableNotification4(BluetoothGattCharacteristic characteristic) {
		Log.i(TAG, "enableNotification------------>" + characteristic.getUuid());
		if (mBluetoothGatt == null) {
			return false;
		}
		boolean result = mBluetoothGatt.setCharacteristicNotification(characteristic, true);
		Log.i(TAG, "setCharacteristicNotification===>" + result);

		BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID_DESCRIPTOR);

		if (clientConfig == null) {
			Log.e(TAG, "clientConfig is null");
			return false;
		}
		clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		return mBluetoothGatt.writeDescriptor(clientConfig);
	}

	public boolean writeData(byte[] data, UUID servide, UUID charac) {
		BluetoothGattService mainService = mBluetoothGatt.getService(servide);
		if (mainService == null) {
			Log.e(TAG, "service not found!");
			return false;
		}
		BluetoothGattCharacteristic txCharac = mainService.getCharacteristic(charac);
		if (txCharac == null) {
			Log.e(TAG, "HEART RATE MEASUREMENT charateristic not found!");
			return false;
		}
		txCharac.setValue(data);
		return mBluetoothGatt.writeCharacteristic(txCharac);
	}

	public void disableNotification(UUID uService, UUID uCharac) {
		if (mBluetoothGatt == null) {
			return;
		}
		BluetoothGattService mainService = mBluetoothGatt.getService(uService);
		if (mainService == null) {
			// disconnect(device);
			return;
		}
		BluetoothGattCharacteristic mainCharac = mainService.getCharacteristic(uCharac);
		if (mainCharac == null) {
			return;
		}
		if (!mBluetoothGatt.setCharacteristicNotification(mainCharac, false))
			return;
		BluetoothGattDescriptor clientConfig = mainCharac.getDescriptor(UUID_DESCRIPTOR);
		if (clientConfig == null)
			return;
		clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		mBluetoothGatt.writeDescriptor(clientConfig);
	}

	public void scan(boolean start, UUID[] uid) {
		if (start) {
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			// reSetIsConnect();
		}
	}

	private ContentObserver newMmsContentObserver = new ContentObserver(new Handler()) {
		public void onChange(boolean selfChange) {
			Log.i(TAG, "mNewSmsCount" + "hao");
			int mNewSmsCount = getNewSmsCount() + getNewMmsCount();
			share.edit().putInt(SystemConfig.KEY_UNREADSMSCOUNT, mNewSmsCount).commit();
			Log.i(TAG, "mNewSmsCount" + mNewSmsCount);

			if (MyApp.getIntance().mService.mConnectionState == STATE_CONNECTED) {
				MyApp.getIntance().mService.mCommand = UNREADSMS_NUM;
				MyApp.getIntance().mService.sendUnreadSmsCount();
			} else {
				// MyApp.getIntance().mService.mCommand =
				// MyApp.getIntance().mService.UNREADSMS_NUM;
				// MyApp.getIntance().mService.connect(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString());
			}
			;
			Log.i(TAG, "mNewSmsCount>>>" + mNewSmsCount);
		}
	};
	private ContentObserver newCallContentObserver = new ContentObserver(new Handler()) {
		public void onChange(boolean selfChange) {
			readMissCall();
		}
	};

	private int readMissCall() {
		int result = 0;
		Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] { Calls.TYPE }, " type=? and new=?", new String[] { Calls.MISSED_TYPE + "", "1" }, "date desc");
		Log.i(TAG, "weijie" + cursor.getCount());

		if (cursor != null) {
			result = cursor.getCount();
			SharedPreferences share = getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
			share.edit().putInt(SystemConfig.KEY_UNREADPHONE, result).commit();
			if (MyApp.getIntance().mService.mConnectionState == STATE_CONNECTED) {
				MyApp.getIntance().mService.mCommand = UNREADPHONE_NUM;
				MyApp.getIntance().mService.sendUnReadPhoneCount();

			} else {
				// MyApp.getIntance().mService.mCommand =
				// MyApp.getIntance().mService.UNREADPHONE_NUM;
				// MyApp.getIntance().mService.connect(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString());
			}
			;
			Log.i(TAG, "mMissCallCount>>>>" + result);
			cursor.close();
		}
		return result;
	}

	private int getNewSmsCount() {
		int result = 0;
		Cursor csr = getContentResolver().query(Uri.parse("content://sms"), null, "type = 1 and read = 0", null, null);
		if (csr != null) {
			result = csr.getCount();
			csr.close();
		}
		return result;
	}

	private int getNewMmsCount() {
		int result = 0;
		Cursor csr = getContentResolver().query(Uri.parse("content://mms/inbox"), null, "read = 0", null, null);
		if (csr != null) {
			result = csr.getCount();
			csr.close();
		}
		return result;
	}

	private void registerObserver() {
		// unregisterObserver();
		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, newMmsContentObserver);
		getContentResolver().registerContentObserver(Uri.parse("content://mms/inbox"), true, newMmsContentObserver);
		getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, newCallContentObserver);
	}

//	private synchronized void unregisterObserver() {
//		try {
//			if (newMmsContentObserver != null) {
//				getContentResolver().unregisterContentObserver(newMmsContentObserver);
//			}
//			if (newMmsContentObserver != null) {
//				getContentResolver().unregisterContentObserver(newMmsContentObserver);
//			}
//			if (newCallContentObserver != null) {
//				getContentResolver().unregisterContentObserver(newCallContentObserver);
//			}
//		} catch (Exception e) {
//			Log.e(TAG, "unregisterObserver fail");
//		}
//	}

	/*
	 * final IntentFilter filter = new IntentFilter();
	 * filter.addAction("com.android.phone.NotificationMgr.MissedCall_intent");
	 * final Application application = getApplication();
	 * application.registerReceiver(new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { String
	 * action = intent.getAction(); if (action != null &&
	 * "com.android.phone.NotificationMgr.MissedCall_intent".equals(action)) {
	 * int mMissCallCount = intent.getExtras().getInt("MissedCallNumber"); } }
	 * }, filter);
	 */
	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("com.android.phone.NotificationMgr.MissedCall_intent");

		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action != null && "com.android.phone.NotificationMgr.MissedCall_intent".equals(action)) {
				int mMissCallCount = intent.getExtras().getInt("MissedCallNumber");
				Log.i(TAG, "mMissCallCount>>>>" + mMissCallCount);
			}

		}
	};
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// handler自带方法实现定时器
			try {
				boolean isRealTime = share.getBoolean(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + "open_real_time", true);

				handler.postDelayed(this, TIME);
				if (mBluetoothGatt != null) {
					// if ("W240N".equals(mBluetoothGatt.getDevice().getName())
					// || "CC431".equals(mBluetoothGatt.getDevice().getName()))
					// {
					if (MyApp.getIntance().list_device_new.contains(getDeviceName(mBluetoothGatt.getDevice().getName()))) {
						int mNewSmsCount = getNewSmsCount() + getNewMmsCount();
						if (mNewSmsCount != share.getInt(SystemConfig.KEY_UNREADSMSCOUNT, 0)) {
							share.edit().putInt(SystemConfig.KEY_UNREADSMSCOUNT, mNewSmsCount).commit();
							Log.i(TAG, "mNewSmsCount" + mNewSmsCount);

							if (MyApp.getIntance().mService.mConnectionState == STATE_CONNECTED) {
								MyApp.getIntance().mService.mCommand = UNREADSMS_NUM;
								MyApp.getIntance().mService.sendUnreadSmsCount();
							} else {
								// MyApp.getIntance().mService.mCommand =
								// MyApp.getIntance().mService.UNREADSMS_NUM;
								// MyApp.getIntance().mService.connect(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString());
							}
							;
							Log.i(TAG, "mNewSmsCount>>>" + mNewSmsCount);
						} else {
							if (mConnectionState == STATE_DISCONNECTED && !isRealTime) {
								// autoConnect(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString());
							}

						}
					}

					// }

				} else {
					if (!isRealTime) {
						initialize();
						// autoConnect(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString());
					}

				}
				;

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("exception...");
			}
		}
	};

}
