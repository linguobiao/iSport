package com.cn.zhihengchuang.walkbank.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityGroup;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.entity.BltModel;
import com.cn.zhihengchuang.walkbank.util.BitmapUtil;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.cn.zhihengchuang.walkbank.util.ExitManager;
import com.cn.zhihengchuang.walkbank.util.ToastUtil;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lingb.global.Global;
import com.lingb.helper.SpHelper;
import com.lingb.ride.service.RideBLEService;

@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("NewApi")
public class MainActivityGroup extends ActivityGroup {
	private RelativeLayout re_main;
	private RadioGroup m_f_radiogroup_menu;// 运动与睡眠单选按钮
	private RadioButton radio;
	private View img_bg;// 单选按钮下的滑动图片
	private float currentIndicatorLeft = 0;// 滑动图片的初始位置
	private float indicatorWidth;// 滑动图片的宽度
	private ToastUtil toast_util;
	private SharedPreferences share = null;
	private Editor edit;
	private int position;
	private ImageView userInformation;
	View exercise;
//	private BleService mBleService;
	private PopupWindow mPopupWindow;
	private TextView device;
	List<BltModel> blts;
	private long currentShow;
	private boolean isBounding = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_);
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名称
		edit = share.edit();
		if (share.getBoolean("frist_start", true)) {
			share.edit().putBoolean("frist_start", false).commit();
			Intent intent = new Intent(MainActivityGroup.this, PersonalSettingActivity.class);
			startActivityForResult(intent, 0);

		}
		ExitManager.getInstance().addActivity(this);
		init();
		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (MainActivityGroup.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			initData();
			// service_init();
		} else {
			toast_util.getToast(this.getString(R.string.manage_device_cant_conn_blue));
		}
		radio.setChecked(true);
		currentIndicatorLeft = indicatorWidth;
		Intent intent = new Intent(MainActivityGroup.this, ExerciseFragmentActivity.class);
		exercise = getLocalActivityManager().startActivity("activity1", intent).getDecorView();
		re_main.removeAllViews();
		re_main.addView(exercise);
		initBroadcastReceiver();
		
		SpHelper.putInt(Global.KEY_DEVICE, Global.TYPE_DEVICE_BAND);

	}

	@Override
	protected void onStart() {
		updateBluetoothScan();
		super.onStart();
	}
	@Override
	protected void onResume() {
		super.onResume();
		MyApp.getIntance().getAppSettings().IS_NEED_CONNECT.setValue(true);
//		share.edit().putInt("metric", 1).commit();
		m_f_radiogroup_menu.setFocusable(true);
//		updateBluetoothScan();
	}

	@Override
	protected void onDestroy() {
		Log.i("bleservices", "mainactivity ondestrou");
		super.onDestroy();
		if (MyApp.getIntance().mService != null) {
			
			MyApp.getIntance().mService.disconnect();
		}
		stopService(new Intent(MainActivityGroup.this, BleService.class));
//		unbindService(mServiceConnection);
		unregisterReceiver(myBLEBroadcastReceiver);
	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			toast_util.getToast(MainActivityGroup.this.getResources().getString(R.string.menu_main_exit));
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {
			edit.putBoolean(Constants.CONNECTION_STATE, false);
			edit.commit();
			sendBroadcast(new Intent(Constants.BROADCAST_ACTION_USER_SERVICE_STOP));
			finish();
//			System.exit(0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Global.REQUEST_ENABLE_BLUETOOTH) {
			if (resultCode == Activity.RESULT_OK) {
//				updateBluetoothScan();
			}
		} else {
			if (resultCode == RESULT_OK) {
				Bitmap bitmap = BitmapUtil.getBitmap(getApplicationContext().getFilesDir().getAbsolutePath() + Constants.SAVEUSERIMAGE);
				if (bitmap != null)
					userInformation.setImageBitmap(bitmap);
			}
		}
	}

	private void setListener() {
		m_f_radiogroup_menu.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Intent intent = null;
				switch (checkedId) {

				case R.id.radio_button_two:
					position = 0;
					intent = new Intent(MainActivityGroup.this, ExerciseFragmentActivity.class);
					TranslateAnimation animation = new TranslateAnimation(currentIndicatorLeft, ((View) m_f_radiogroup_menu.getChildAt(position)).getLeft(),
							0f, 0f);
					animation.setInterpolator(new LinearInterpolator());
					animation.setDuration(100);
					animation.setFillAfter(true);
					// 执行位移动画
					img_bg.startAnimation(animation);
					// 记录当前 下标的距最左侧的 距离
					currentIndicatorLeft = ((View) m_f_radiogroup_menu.getChildAt(position)).getLeft();
					if (intent != null) {
						View page = getLocalActivityManager().startActivity("activity1", intent).getDecorView();
						re_main.removeAllViews();
						re_main.addView(page);
					}
					break;
				case R.id.radio_button_three:
					position = 1;
					intent = new Intent(MainActivityGroup.this, SleepFragmentActivity.class);
					TranslateAnimation animation1 = new TranslateAnimation(currentIndicatorLeft, ((View) m_f_radiogroup_menu.getChildAt(position)).getLeft(),
							0f, 0f);
					animation1.setInterpolator(new LinearInterpolator());
					animation1.setDuration(100);
					animation1.setFillAfter(true);
					// 执行位移动画
					img_bg.startAnimation(animation1);
					// 记录当前 下标的距最左侧的 距离
					currentIndicatorLeft = ((View) m_f_radiogroup_menu.getChildAt(position)).getLeft();
					if (intent != null) {
						View page = getLocalActivityManager().startActivity("activity1", intent).getDecorView();
						re_main.removeAllViews();
						re_main.addView(page);
					}
					break;
				/*
				 * case R.id.radio_button_six: showPopwindow(more); return;
				 */
				/*
				 * case R.id.radio_button_four: position = 2; intent = new Intent(MainActivityGroup.this, LocationAcitivity.class); break; case R.id.radio_button_five: position = 3; intent = new Intent(MainActivityGroup.this, FootActivity.class); break; case R.id.radio_button_six: position = 4; intent = new
				 * Intent(MainActivityGroup.this, MenuSetActivity.class); break;
				 */
				}

			}
		});
	}

	/**
	 * 开始扫描设备(前台,一直扫描，直到扫到)
	 */
	private void beginScanDevice() {
		if (MyApp.getIntance().mService != null) {
			int connectState = MyApp.getIntance().mService.mConnectionState;
			// 蓝牙没有连接设备
			if (connectState != BleService.STATE_CONNECTED) {
				// 开始搜索
				MyApp.getIntance().mService.scan(true, null);
			}
		}
	}

	private final int BLE_STATE_NULL = -1;
	private final int BLE_STATE_CONNECTING = 1;
	private int ble_state = BLE_STATE_NULL;

	private synchronized void actionFoundBoundDevice(Intent intent) {
		Bundle data = intent.getExtras();
		BluetoothDevice device = data.getParcelable(BluetoothDevice.EXTRA_DEVICE);
		String addressBounded = MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString();
		Log.i("test", "addressBounded = " + addressBounded);
		if (addressBounded != "") {
			if (device != null) {
				String address = device.getAddress();
				if (address != null) {
					if (address.startsWith(addressBounded)) {
						boolean isNeedConnect = MyApp.getIntance().getAppSettings().IS_NEED_CONNECT.getValue();
						if (isNeedConnect) {
							if (ble_state != BLE_STATE_CONNECTING) {
								ble_state = BLE_STATE_CONNECTING;
								if (MyApp.getIntance().mService != null) {
									MyApp.getIntance().mService.scan(false, null);
									MyApp.getIntance().mService.connect(address);
								}
							}
						}
						
					}
				}
			}
		}
	}

	private void initData() {
		startService(new Intent(MainActivityGroup.this, BleService.class));
		stopService(new Intent(MainActivityGroup.this, RideBLEService.class));
//		Intent i = new Intent(this, BleService.class);
//		bindService(i, mServiceConnection, BIND_AUTO_CREATE);

		// mAddress =
		// MySpUtil.getInstance(getApplicationContext()).getAddress();

//		BluetoothAdapter.getDefaultAdapter().enable();

	}

	/**
	 * 初始化蓝牙
	 * 
	 * @param context
	 */
	public static BluetoothAdapter initBluetooth_manual(Activity context) {
		BluetoothManager mBluetoothManager = null;

		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

			// 没有蓝牙模块
			if (mBluetoothManager == null) {
				return null;
			}
		}
		BluetoothAdapter mBtAdapter = mBluetoothManager.getAdapter();

		// 判断机器是否有蓝牙
		if (!mBtAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			context.startActivityForResult(enableIntent, Global.REQUEST_ENABLE_BLUETOOTH);
		}
		return mBtAdapter;
	}

	private void updateBluetoothScan() {
		boolean isBounded = !TextUtils.isEmpty(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString());
		ble_state = BLE_STATE_NULL;
		// 没有绑定
		if (!isBounded) {
			if (MyApp.getIntance().mService != null) {
				MyApp.getIntance().mService.scan(false, null);
			}
		} else {
			if (MyApp.getIntance().mService != null && MyApp.getIntance().mService.mConnectionState != BleService.STATE_CONNECTED) {
				BluetoothAdapter mBtAdapter = initBluetooth_manual(MainActivityGroup.this);
				if (mBtAdapter != null) {
					if (mBtAdapter.isEnabled()) {
						
//						if ("W240N".equals(MyApp.getIntance().getAppSettings().LAST_CONNECT_NAME.getValue().toString()) || "CC431".equals(MyApp.getIntance().getAppSettings().LAST_CONNECT_NAME.getValue().toString())) {
//						if (deviceName.contains("_")) {
//							deviceName = deviceName.split("_")[0];
//						}	
						Log.i("test", "bound name = " + MyApp.getIntance().getAppSettings().LAST_CONNECT_NAME.getValue().toString());
						if (MyApp.getIntance().list_device_new.contains(MyApp.getIntance().getAppSettings().LAST_CONNECT_NAME.getValue().toString())) {
//								if (share.getBoolean(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + "open_real_time", true)) {
									MyApp.getIntance().mService.mCommand = BleService.COMMAND_SYNC_SAVE;
									beginScanDevice();
//								} else {
//									mBleService.mCommand = BleService.OPEN_REAL_TIME;
//									beginScanDevice();
//								}
//							}
							
						} else {
							MyApp.getIntance().mService.disconnect();
							MyApp.getIntance().mService.mCommand = BleService.COMMAND_SYNC_SAVE;
							beginScanDevice();
							// mBleService.connect(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString());
						}

					}
				}
			}
		}
	}

	// Code to manage Service lifecycle.
//	private final ServiceConnection mServiceConnection = new ServiceConnection() {
//
//		@Override
//		public void onServiceConnected(ComponentName componentName, IBinder service) {
//			mBleService = ((BleService.LocalBinder) service).getService();
//			MyApp.getIntance().mService = mBleService;
//			if (!mBleService.initialize()) {
//				// Log.e(TAG, "Unable to initialize Bluetooth");
//				finish();
//			}
//			updateBluetoothScan();
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName componentName) {
//			mBleService = null;
//		}
//	};

	/**
	 * my BLE BroadcastReceiver
	 */
	private BroadcastReceiver myBLEBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BleService.ACTION_GATT_CONNECTED)) {
				if (!isBounding) {
					Toast.makeText(MainActivityGroup.this, getResources().getString(R.string.connection_successful), 1000).show();
					isBounding = false;
				}
				
			}
			// 连接断开 ***************************************************************
			else if (action.equals(BleService.ACTION_GATT_DISCONNECTED)) {
				boolean isRealTime = share.getBoolean(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + "open_real_time", false);
				if (!isRealTime) {
					if (!isBounding) {
						Toast.makeText(MainActivityGroup.this, getResources().getString(R.string.disconnect), 1000).show();
						isBounding = false;
					}
				}
				updateBluetoothScan();
			}
			// 找到绑定的设备 ***************************************************************
			else if (action.equals(BleService.ACTION_DEVICE_FOUND)) {
				actionFoundBoundDevice(intent);
			} else if (action.equals(Global.ACTION_UPDATE_CONNECT)) {
				updateBluetoothScan();
			}
			else if (action.equals(Global.ACTION_ALARM_CHANGED)) {

			} else if (action.equals(Global.ACTION_GOAL_CHANGED)) {

				if (MyApp.getIntance().mService != null) {
					if (MyApp.getIntance().mService.mConnectionState == BleService.STATE_CONNECTED) {
						MyApp.getIntance().mService.mCommand = BleService.STEP_LENGTH;
						MyApp.getIntance().mService.sendStepLength();
					} else {

					}
				}
			} else if (action.equals(Global.ACTION_BOUND_MAC_CHANGED)) {
				Log.i("bound", "ACTION_BOUND_MAC_CHANGED mService = " + MyApp.getIntance().mService);
				isBounding = true;
				if (MyApp.getIntance().mService != null) {
					if (MyApp.getIntance().mService.mConnectionState == BleService.STATE_CONNECTED) {
						MyApp.getIntance().mService.disconnect();
					} else {
						updateBluetoothScan();
					}
				}
			} else if (action.equals(Global.ACTION_BOUND_STOP)) {
				if (MyApp.getIntance().mService != null) {
					MyApp.getIntance().mService.scan(false, null);
				}
			} else if (action.equals(Global.ACTION_HAND_TYPE_CHANGED)) {

			} else if (action.equals(Global.ACTION_REMAIN_CHANGED)) {

			} else if (action.equals(Global.ACTION_STEP_LENGTH_CHANGED)) {

			} else if (action.equals(Global.ACTION_SYNC_TYPE_CHANGED)) {

			} else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
				if (state == BluetoothAdapter.STATE_ON) {
					updateBluetoothScan();
				}
			} else if (Constants.UPDATE_OK.equals(action)) {
				long currentTime = System.currentTimeMillis() - currentShow;
				boolean isOld = intent.getBooleanExtra("isOld", false);
				boolean isRealTime = intent.getBooleanExtra("isRealTime", false);
				if (!isRealTime && !isOld) {
					if (currentTime > 5000) {
						Log.i("tests", "***** successful_synchronization " + currentTime);
						currentShow = System.currentTimeMillis();
						Toast.makeText(context, getResources().getString(R.string.successful_synchronization), 300).show();
					}
				}
				
			} else if (Constants.OLD_UPDATE_OK.equals(action)) {
				Toast.makeText(context, getResources().getString(R.string.successful_synchronization), 300).show();
			} else if (action.equals(Global.ACTION_FINISH_BAND)) {
				MainActivityGroup.this.finish();
			}
		}
	};

	/**
	 * 初始化 BroadcastReceiver
	 */
	private void initBroadcastReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Global.ACTION_ALARM_CHANGED);
		intentFilter.addAction(Global.ACTION_GOAL_CHANGED);
		intentFilter.addAction(Global.ACTION_HAND_TYPE_CHANGED);
		intentFilter.addAction(Global.ACTION_REMAIN_CHANGED);
		intentFilter.addAction(Global.ACTION_STEP_LENGTH_CHANGED);
		intentFilter.addAction(Global.ACTION_SYNC_TYPE_CHANGED);
		intentFilter.addAction(Global.ACTION_BOUND_MAC_CHANGED);
		intentFilter.addAction(Global.ACTION_BOUND_STOP);
		intentFilter.addAction(Global.ACTION_UPDATE_CONNECT);

		intentFilter.addAction(BleService.ACTION_DEVICE_FOUND);
		intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
		
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter.addAction(Constants.UPDATE_OK);
		intentFilter.addAction(Constants.OLD_UPDATE_OK);
		
		intentFilter.addAction(Global.ACTION_FINISH_BAND);
		MainActivityGroup.this.registerReceiver(myBLEBroadcastReceiver, intentFilter);

	}

	private void init() {
		currentIndicatorLeft = 0;
		radio = (RadioButton) findViewById(R.id.radio_button_two);
		re_main = (RelativeLayout) findViewById(R.id.main);
		toast_util = new ToastUtil(this);
		img_bg = (View) findViewById(R.id.view_checked_main);
		device = (TextView) findViewById(R.id.device);
		device.setVisibility(View.GONE);
//		device.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				showPopwindow(device);
//			}
//		});
		userInformation = (ImageView) findViewById(R.id.radio_button_one);
		Bitmap bitmap = BitmapUtil.getBitmap(getApplicationContext().getFilesDir().getAbsolutePath() + Constants.SAVEUSERIMAGE);
		if (bitmap != null)
			userInformation.setImageBitmap(bitmap);
		userInformation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivityGroup.this, MenuSetActivity.class);
				startActivity(intent);
			}
		});
		m_f_radiogroup_menu = (RadioGroup) findViewById(R.id.radio_group_menu_main);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		indicatorWidth = dm.widthPixels / 6;
		LayoutParams cursor_Params = img_bg.getLayoutParams();
		cursor_Params.width = (int) indicatorWidth;// 初始化滑动下标的宽
		img_bg.setLayoutParams(cursor_Params);
		setListener();
	}

}