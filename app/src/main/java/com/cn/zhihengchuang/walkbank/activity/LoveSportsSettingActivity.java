package com.cn.zhihengchuang.walkbank.activity;

import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetAge;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetSex;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetTargetActivity;
import com.cn.zhihengchuang.walkbank.entity.BltModel;
import com.cn.zhihengchuang.walkbank.entity.DeviceEntity;
import com.cn.zhihengchuang.walkbank.entity.PedometerModel;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.cn.zhihengchuang.walkbank.view.MyDialog;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lingb.global.Global;
import com.lingb.helper.StringHelper;

public class LoveSportsSettingActivity extends Activity implements OnClickListener {
	private TextView back_tv;
	private TextView foot_distance;
	private TextView left_right_text;
	private TextView title_text;
	private TextView target_distance;
	private LinearLayout target_layout;
	private LinearLayout left_right_device;
	private LinearLayout foot_layout;
	private LinearLayout long_time_alert;
	private LinearLayout alarm_layout;
	private LinearLayout layout_sleep;
	private LinearLayout layout_display;
	// private LinearLayout restore_settings_layout;
	private LinearLayout unBond_layout;
	// private LinearLayout ly_firmware_upgrade;
	private SharedPreferences share;
	private Editor edit;
	private BleService mBleService;
	private MyDialog mMyDialog;
	private DeviceEntity entity;
	private CheckBox taget_cb;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
		// registerBoradcastReceiver();
		edit = share.edit();
		setContentView(R.layout.activity_settings_connected);
//		initData();
		entity = (DeviceEntity) getIntent().getSerializableExtra("device");
		position = getIntent().getIntExtra("position", 0);
		back_tv = (TextView) findViewById(R.id.back_tv);
		back_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				finish();
			}
		});
		foot_distance = (TextView) findViewById(R.id.foot_distance);
		left_right_text = (TextView) findViewById(R.id.left_right_text);
		target_layout = (LinearLayout) findViewById(R.id.target_layout);
		left_right_device = (LinearLayout) findViewById(R.id.left_right_device);
		foot_layout = (LinearLayout) findViewById(R.id.foot_layout);
		long_time_alert = (LinearLayout) findViewById(R.id.long_time_alert);
		alarm_layout = (LinearLayout) findViewById(R.id.alarm_layout);
		layout_sleep = (LinearLayout) findViewById(R.id.layout_sleep);
		layout_display = (LinearLayout) findViewById(R.id.layout_display);
		// ly_firmware_upgrade = (LinearLayout) findViewById(R.id.ly_firmware_upgrade);
		// restore_settings_layout = (LinearLayout) findViewById(R.id.restore_settings_layout);
		unBond_layout = (LinearLayout) findViewById(R.id.unBond_layout);
		title_text = (TextView) findViewById(R.id.title_text);
		target_distance = (TextView) findViewById(R.id.target_distance);
		taget_cb = (CheckBox) findViewById(R.id.taget_cb);
		title_text.setText(StringHelper.replaceDeviceNameToCC431(entity.getName()) + " " + (position + 1));
		target_distance.setText(share.getString(entity.getMac() + "target_distance", "10000") + " " + getResources().getString(R.string.steps_day));
		if (share.getInt("metric", 0) == 0) {
			foot_distance.setText(share.getString(entity.getMac() + "foot_distance", "60") + "cm");
		} else {
			foot_distance.setText(share.getString(entity.getMac() + "foot_distance", "30") + "inch");
		}
		taget_cb.setChecked(share.getBoolean(entity.getMac() + "open_real_time", false));
		if (share.getBoolean(entity.getMac() + "left_hand", true)) {
			left_right_text.setText(getResources().getString(R.string.left_hand));
		} else {
			left_right_text.setText(getResources().getString(R.string.right_hand));
		}
		taget_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// sendBroadcast(new Intent(Global.ACTION_SYNC_TYPE_CHANGED));
				if (MyApp.getIntance().mService != null) {
					if (MyApp.getIntance().mService != null) {
						if (MyApp.getIntance().mService.mConnectionState == MyApp.getIntance().mService.STATE_CONNECTED) {
							MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.OPEN_REAL_TIME;
							MyApp.getIntance().mService.sendCommandRealTime(arg1);
							edit.putBoolean(entity.getMac() + "open_real_time", arg1).commit();
						} else {

							Toast.makeText(LoveSportsSettingActivity.this, getResources().getString(R.string.please_connect), 1000).show();
						}
					}
				}
			}
		});
		registerBoradcastReceiver();
		// ly_firmware_upgrade.setOnClickListener(this);
		target_layout.setOnClickListener(this);
		left_right_device.setOnClickListener(this);
		foot_layout.setOnClickListener(this);
		long_time_alert.setOnClickListener(this);
		alarm_layout.setOnClickListener(this);
		layout_display.setOnClickListener(this);
		layout_sleep.setOnClickListener(this);
		// restore_settings_layout.setOnClickListener(this);
		unBond_layout.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.unBond_layout) {
			DbUtils db = DbUtils.create(getApplicationContext());
			WhereBuilder builder = WhereBuilder.b("bltid", "==", entity.getMac());
			try {
				db.delete(BltModel.class, builder);
				sendBroadcast(new Intent(Constants.CONNECTING_DEVICE));
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue("");
			if (MyApp.getIntance().mService != null) {
				MyApp.getIntance().mService.disconnect();
			}
			
			finish();
		} else {
			if (MyApp.getIntance().mService != null) {
				if (MyApp.getIntance().mService.mConnectionState == MyApp.getIntance().mService.STATE_CONNECTED) {
					Intent dialog_intent;

					switch (arg0.getId()) {

					case R.id.target_layout:
						dialog_intent = new Intent(LoveSportsSettingActivity.this, DialogSetTargetActivity.class);
						int target;
						if (target_distance.getText().toString().startsWith("10000")) {
							target = Integer.parseInt(target_distance.getText().toString().split(" ")[0]);
						} else {
							target = Integer.parseInt(target_distance.getText().toString().split(" ")[0]);
						}
						dialog_intent.putExtra("age", target);
						startActivityForResult(dialog_intent, 101);
						break;
					case R.id.left_right_device:
						dialog_intent = new Intent(LoveSportsSettingActivity.this, DialogSetSex.class);
						dialog_intent.putExtra("is_from_left", true);
						dialog_intent.putExtra("mac", entity.getMac());
						/*
						 * dialog_intent.putExtra(entity.getMac()+"is_left", share.getBoolean("left_hand", true));
						 */
						startActivityForResult(dialog_intent, 101);
						break;
					case R.id.foot_layout:
						dialog_intent = new Intent(LoveSportsSettingActivity.this, DialogSetAge.class);
						dialog_intent.putExtra("is_from_foot", true);
						dialog_intent.putExtra("age", Integer.parseInt(foot_distance.getText().toString().replace("cm", "").replace("inch", "")));
						startActivityForResult(dialog_intent, 101);
						break;
					case R.id.long_time_alert:
						dialog_intent = new Intent(LoveSportsSettingActivity.this, ReminderActivity.class);
						dialog_intent.putExtra("device", entity);
						startActivityForResult(dialog_intent, 101);
						break;
					case R.id.alarm_layout:
						dialog_intent = new Intent(LoveSportsSettingActivity.this, AlarmActivity.class);
						dialog_intent.putExtra("device", entity);
						startActivityForResult(dialog_intent, 101);
						break;
					case R.id.layout_sleep:
						dialog_intent = new Intent(LoveSportsSettingActivity.this, SleepActivity.class);
						startActivity(dialog_intent);
						break;
					case R.id.layout_display:
						dialog_intent = new Intent(LoveSportsSettingActivity.this, DisplayActivity.class);
						startActivity(dialog_intent);
						break;
					/*
					 * case R.id.restore_settings_layout: new AlertDialog.Builder(LoveSportsSettingActivity.this) .setMessage( getResources() .getString( R.string.setting_the_default_setting)) .setPositiveButton( getResources().getString( R.string.user_info_done), new AlertDialog.OnClickListener() {
					 * 
					 * @Override public void onClick( DialogInterface arg0, int arg1) { clear(); } }) .setNegativeButton( getResources().getString( R.string.user_info_cancle), new AlertDialog.OnClickListener() {
					 * 
					 * @Override public void onClick( DialogInterface arg0, int arg1) {
					 * 
					 * } }).show();
					 * 
					 * break;
					 */

					/*
					 * case R.id.ly_firmware_upgrade:
					 * 
					 * dialog_intent = new Intent(LoveSportsSettingActivity.this, DfuActivity.class); startActivity(dialog_intent);
					 * 
					 * break;
					 */
					}
				} else {
					Toast.makeText(LoveSportsSettingActivity.this, getResources().getString(R.string.please_connect), 1000).show();
				}

			}
		}

	}

	private void clear() {
		share.edit().putString(entity.getMac() + "target_distance", "10000").commit();
		if (share.getInt("metric", 0) == 0) {
			share.edit().putString(entity.getMac() + "foot_distance", "60").commit();

		} else {
			share.edit().putString(entity.getMac() + "foot_distance", "30").commit();

		}
		share.edit().putBoolean(entity.getMac() + "left_hand", true).commit();
		showDialog(getString(R.string.please_wait));
		DbUtils db = DbUtils.create(LoveSportsSettingActivity.this);
		db.configAllowTransaction(true);
		db.configDebug(true);
		WhereBuilder builder = WhereBuilder.b("uuid", "==", entity.getMac());
		try {
			db.delete(PedometerModel.class, builder);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMyDialog.dismiss();
		Toast.makeText(LoveSportsSettingActivity.this, getResources().getString(R.string.clear_data_seccess), 1000).show();
		sendBroadcast(new Intent(Constants.CLEAR_AlL));

	}

	private void showDialog(String msg) {
		if (mMyDialog == null)
			mMyDialog = new MyDialog(this, msg, false);
		mMyDialog.setMsg(msg);

		if (mMyDialog.isShowing() == false)
			mMyDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		if (resultCode == 200) {// 目标步数
			if (!data.getStringExtra("age").equals("0")) {
				target_distance.setText(data.getStringExtra("age") + " " + getResources().getString(R.string.steps_day));
				edit.putString(entity.getMac() + "target_distance", data.getStringExtra("age")).commit();
			}

			if (MyApp.getIntance().mService != null) {
				if (MyApp.getIntance().mService.mConnectionState == MyApp.getIntance().mService.STATE_CONNECTED) {
					MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.STEP_LENGTH;
					MyApp.getIntance().mService.sendStepLength();
				} else {
					// MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.STEP_LENGTH;
					// MyApp.getIntance().mService.connect(entity.getMac());
				}
			}
		} else if (resultCode == 201) {// 步距
			if (share.getInt("metric", 0) == 0) {
				foot_distance.setText(data.getStringExtra("age") + "cm");
			} else {
				foot_distance.setText(data.getStringExtra("age") + "inch");
			}
			edit.putString(entity.getMac() + "foot_distance", data.getStringExtra("age")).commit();
			if (MyApp.getIntance().mService != null) {
				if (MyApp.getIntance().mService.mConnectionState == MyApp.getIntance().mService.STATE_CONNECTED) {
					MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.STEP_LENGTH;
					MyApp.getIntance().mService.sendStepLength();
				} else {
					// MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.STEP_LENGTH;
					// MyApp.getIntance().mService.connect(entity.getMac());
				}
			}

		} else if (resultCode == 204) {// 左右手
			// Toast.makeText(LoveSportsSettingActivity.this,
			// getResources().getString(R.string.setting_seccess), 1000).show();
			if (data.getBooleanExtra("is_man", true)) {
				left_right_text.setText(getResources().getString(R.string.left_hand));
				edit.putBoolean(entity.getMac() + "left_hand", true).commit();
				String[] date2 = DateUtil.getCurrentDate().split("-");
				byte[] time = new byte[] { (byte) 0xbe, 0x01, 0x0b, (byte) 0xfe, 0x00 };
				SendCommandLeftAndRight(time);
			} else {
				left_right_text.setText(getResources().getString(R.string.right_hand));
				edit.putBoolean(entity.getMac() + "left_hand", false).commit();
				String[] date2 = DateUtil.getCurrentDate().split("-");
				byte[] time = new byte[] { (byte) 0xbe, 0x01, 0x0b, (byte) 0xfe, 0x01 };
				SendCommandLeftAndRight(time);

			}

		}
	}

	/**
	 * 发送佩戴信息指令
	 * 
	 * @param time
	 */
	private void SendCommandLeftAndRight(byte[] time) {
		// sendBroadcast(new Intent(Global.ACTION_HAND_TYPE_CHANGED));
		if (MyApp.getIntance().mService != null) {
			if (MyApp.getIntance().mService.mConnectionState == MyApp.getIntance().mService.STATE_CONNECTED) {
				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMAND_WEARINFO;
				MyApp.getIntance().mService.writeData(time, BleService.MAIN_SERVICE, BleService.SEND_DATA_CHAR);
			} else {
//				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMAND_WEARINFO;
//				MyApp.getIntance().mService.connect(entity.getMac());
			}
		}
	}

	private void initData() {

		Intent i = new Intent(this, BleService.class);
		bindService(i, mServiceConnection, BIND_AUTO_CREATE);
		// mAddress =
		// MySpUtil.getInstance(getApplicationContext()).getAddress();

		BluetoothAdapter.getDefaultAdapter().enable();

	}

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBleService = ((BleService.LocalBinder) service).getService();
			MyApp.getIntance().mService = mBleService;
			if (MyApp.getIntance().mService.mConnectionState == MyApp.getIntance().mService.STATE_CONNECTED) {

			} else {
				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMAND_SYNC_SAVE;
				MyApp.getIntance().mService.connect(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString());
			}
			;
			if (!mBleService.initialize()) {
				// Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			// mBleService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBleService = null;
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
		/*
		 * if (mBleService != null) { mBleService.close(); } // unregisterReceiver(mReceiver); mBleService = null;
		 */
	}

	private void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.CONNECTING_DEVICE);
		myIntentFilter.addAction(BleService.ACTION_SETTING_OK);
		// 注册广播
		registerReceiver(mReceiver, myIntentFilter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleService.ACTION_SETTING_OK.equals(action)) {
				Toast.makeText(LoveSportsSettingActivity.this, getResources().getString(R.string.setting_seccess), 1000).show();
			}

		}
	};

}
