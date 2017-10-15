package com.cn.zhihengchuang.walkbank.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.ble.BleController;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetAge;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetSex;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetTargetActivity;
import com.cn.zhihengchuang.walkbank.entity.BltModel;
import com.cn.zhihengchuang.walkbank.entity.DeviceEntity;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.cn.zhihengchuang.walkbank.view.MyDialog;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class W240SettingActivity extends Activity implements OnClickListener {
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
	private LinearLayout restore_settings_layout;
	private LinearLayout unBond_layout;
	private SharedPreferences share;
	private Editor edit;
	private BleService mBleService;
	private MyDialog mMyDialog;
	private DeviceEntity entity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME,
				Activity.MODE_PRIVATE); // 指定操作的文件名
		edit = share.edit();
		setContentView(R.layout.activity_w240_setting);
		registerBoradcastReceiver();
//		initData();
		entity = (DeviceEntity) getIntent().getSerializableExtra("device");
		back_tv = (TextView) findViewById(R.id.back_tv);
		foot_distance = (TextView) findViewById(R.id.foot_distance);
		left_right_text = (TextView) findViewById(R.id.left_right_text);
		target_layout = (LinearLayout) findViewById(R.id.target_layout);
		left_right_device = (LinearLayout) findViewById(R.id.left_right_device);
		foot_layout = (LinearLayout) findViewById(R.id.foot_layout);
		long_time_alert = (LinearLayout) findViewById(R.id.long_time_alert);
		alarm_layout = (LinearLayout) findViewById(R.id.alarm_layout);
		restore_settings_layout = (LinearLayout) findViewById(R.id.restore_settings_layout);
		unBond_layout = (LinearLayout) findViewById(R.id.unBond_layout);
		title_text = (TextView) findViewById(R.id.title_text);
		target_distance = (TextView) findViewById(R.id.target_distance);
		title_text.setText(entity.getName() + entity.getMac());
		target_distance.setText(share.getString(entity.getMac()
				+ "target_distance", "7000")
				+ getResources().getString(R.string.steps_day));
		if (share.getInt("metric", 0) == 0) {
			foot_distance.setText(share.getString(entity.getMac()
					+ "foot_distance", "60")
					+ "cm");
		} else {
			foot_distance.setText(share.getString(entity.getMac()
					+ "foot_distance", "24")
					+ "inch");
		}

		if (share.getBoolean(entity.getMac() + "left_hand", true)) {
			left_right_text.setText(getResources()
					.getString(R.string.left_hand));
		} else {
			left_right_text.setText(getResources().getString(
					R.string.right_hand));
		}

		back_tv.setOnClickListener(this);
		target_layout.setOnClickListener(this);
		left_right_device.setOnClickListener(this);
		foot_layout.setOnClickListener(this);
		long_time_alert.setOnClickListener(this);
		alarm_layout.setOnClickListener(this);
		restore_settings_layout.setOnClickListener(this);
		unBond_layout.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent dialog_intent;
		switch (arg0.getId()) {
		case R.id.back_tv:
			finish();
			break;
		case R.id.target_layout:
			int target;
			dialog_intent = new Intent(W240SettingActivity.this,
					DialogSetTargetActivity.class);
			if (target_distance.getText().toString().startsWith("10000")) {
				target = Integer.parseInt(target_distance.getText().toString()
						.substring(0, 5));
			} else {
				target = Integer.parseInt(target_distance.getText().toString()
						.substring(0, 4));
			}

			dialog_intent.putExtra("age", target);
			startActivityForResult(dialog_intent, 101);
			break;
		case R.id.left_right_device:
			dialog_intent = new Intent(W240SettingActivity.this,
					DialogSetSex.class);
			dialog_intent.putExtra("is_from_left", true);
			dialog_intent.putExtra("mac", entity.getMac());
			/*
			 * dialog_intent.putExtra(entity.getMac()+"is_left",
			 * share.getBoolean("left_hand", true));
			 */
			startActivityForResult(dialog_intent, 101);
			break;
		case R.id.foot_layout:
			dialog_intent = new Intent(W240SettingActivity.this,
					DialogSetAge.class);
			dialog_intent.putExtra("is_from_foot", true);
			dialog_intent.putExtra(
					"age",
					Integer.parseInt(foot_distance.getText().toString()
							.replace("cm", "").replace("inch", "")));
			startActivityForResult(dialog_intent, 101);
			break;
		case R.id.long_time_alert:
			dialog_intent = new Intent(W240SettingActivity.this,
					ReminderActivity.class);
			dialog_intent.putExtra("device", entity);
			startActivityForResult(dialog_intent, 101);
			break;
		case R.id.alarm_layout:
			dialog_intent = new Intent(W240SettingActivity.this,
					OldAlarmActivity.class);
			dialog_intent.putExtra("device", entity);
			startActivityForResult(dialog_intent, 101);
			break;
		case R.id.restore_settings_layout:
			clear();
			break;
		case R.id.unBond_layout:
			DbUtils db = DbUtils.create(getApplicationContext());
			WhereBuilder builder = WhereBuilder.b("bltid", "==",
					entity.getMac());
			try {
				db.delete(BltModel.class, builder);
				sendBroadcast(new Intent(Constants.CONNECTING_DEVICE));
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (mBleService != null) {
				mBleService.disconnect();
			}
			
			finish();
			break;

		}
	}

	private void clear() {

		if (MyApp.getIntance().mService != null) {
			/*
			 * if (MyApp.getIntance().mService.mConnectionState == MyApp
			 * .getIntance().mService.STATE_CONNECTED) {
			 * MyApp.getIntance().mService.mCommand =
			 * MyApp.getIntance().mService.COMMAND_CLEAR;
			 * MyApp.getIntance().mService.OldClear(); }else{
			 */
			MyApp.getIntance().mService.disconnect();
			MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMAND_CLEAR;
			MyApp.getIntance().mService.connect(entity.getMac());
			// }

			showDialog("请按手环后，稍等片刻！");
		}

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
			showDialog("请稍后");
			if(!data.getStringExtra("age").equals("0")){
				target_distance.setText(data.getStringExtra("age") + "步");
				edit.putString(entity.getMac() + "target_distance",
						data.getStringExtra("age")).commit();
			}
			if (MyApp.getIntance().mService != null) {
				/*
				 * if (MyApp.getIntance().mService.mConnectionState == MyApp
				 * .getIntance().mService.STATE_CONNECTED) {
				 * MyApp.getIntance().mService.mCommand =
				 * MyApp.getIntance().mService.STEP_LENGTH;
				 * MyApp.getIntance().mService.sendStepLength(); } else {
				 */
				MyApp.getIntance().mService.disconnect();
				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMAND_SYNC_SAVE;
				MyApp.getIntance().mService.connect(entity.getMac());
				// }
			}
		} else if (resultCode == 201) {// 步距
			showDialog("请稍后");
			if (share.getInt("metric", 0) == 0) {
				foot_distance.setText(data.getStringExtra("age") + "cm");
			} else {
				foot_distance.setText(data.getStringExtra("age") + "inch");
			}
			edit.putString(entity.getMac() + "foot_distance",
					data.getStringExtra("age")).commit();
			if (MyApp.getIntance().mService != null) {
				/*
				 * if (MyApp.getIntance().mService.mConnectionState == MyApp
				 * .getIntance().mService.STATE_CONNECTED) {
				 * MyApp.getIntance().mService.mCommand =
				 * MyApp.getIntance().mService.STEP_LENGTH;
				 * MyApp.getIntance().mService.sendStepLength(); } else {
				 */
				MyApp.getIntance().mService.disconnect();
				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMAND_SYNC_SAVE;
				MyApp.getIntance().mService.connect(entity.getMac());
				// }
			}

		} else if (resultCode == 204) {// 左右手
			showDialog("请稍后");
			if (data.getBooleanExtra("is_man", true)) {

				left_right_text.setText(getResources().getString(
						R.string.left_hand));
				edit.putBoolean(entity.getMac() + "left_hand", true).commit();
				String[] date2 = DateUtil.getCurrentDate().split("-");
				byte[] time = new byte[] { (byte) 0x0b, 01, 0x00 };
				SendCommandLeftAndRight(time);
			} else {
				left_right_text.setText(getResources().getString(
						R.string.right_hand));
				edit.putBoolean(entity.getMac() + "left_hand", false).commit();
				String[] date2 = DateUtil.getCurrentDate().split("-");
				byte[] time = new byte[] { (byte) 0x0c, 01, 0x00 };
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
		if (MyApp.getIntance().mService != null) {
			MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMAND_WEARINFO;
			MyApp.getIntance().mService.connect(entity.getMac());
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
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBleService = ((BleService.LocalBinder) service).getService();
			MyApp.getIntance().mService = mBleService;
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
		 * if (mBleService != null) { mBleService.close(); } //
		 * unregisterReceiver(mReceiver); mBleService = null;
		 */
	}

	private void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.CLEAR_DEVICE);
		myIntentFilter.addAction(BleService.ACTION_GATT_ERROR);
		myIntentFilter.addAction(BleController.ACTION_WEARINFO_OK);
		myIntentFilter.addAction(Constants.OLD_UPDATE_OK);
		// 注册广播
		registerReceiver(mReceiver, myIntentFilter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.CLEAR_DEVICE.equals(action)) {
				if (mMyDialog != null) {
					mMyDialog.dismiss();
				}
			} else if (BleService.ACTION_GATT_ERROR.equals(action)) {
				if (mMyDialog != null) {

					mMyDialog.dismiss();
				}
				Toast.makeText(W240SettingActivity.this, "操作失败,请重试！", 1000)
						.show();
			} else if (BleController.ACTION_WEARINFO_OK.equals(action)) {
				if (mMyDialog != null) {

					mMyDialog.dismiss();
				}
			}else if(Constants.OLD_UPDATE_OK.equals(action)){
				if (mMyDialog != null) {

					mMyDialog.dismiss();
				}
			}

		}
	};

}
