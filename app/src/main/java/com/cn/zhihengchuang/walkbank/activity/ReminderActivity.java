package com.cn.zhihengchuang.walkbank.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.ble.BleController;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.entity.DeviceEntity;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.SystemConfig;
import com.cn.zhihengchuang.walkbank.util.Tools;
import com.cn.zhihengchuang.walkbank.util.UiTools;
import com.cn.zhihengchuang.walkbank.view.MyDialog;
import com.lingb.global.Global;

/**
 * @author admin �˶����ѽ���
 */
public class ReminderActivity extends Activity implements OnClickListener {
	public static final CharSequence[] NETITEMS = new CharSequence[] { "15",
			"30", "45", "60", "90", "120" };

	private static final int STARTTIME = 0;
	private static final int ENDTIME = 1;
	private byte whichTime;
	private int startHour, startMinute, endHour, endMinute;
	private byte reminderSwitch;
	private int checkedItem;// ��ǰѡ��Ĳ��˶�ʱ���±�

	private TextView tv_startTime, tv_endTime, tv_net;
	// private SwitchButton buSwitch;
	private CheckBox reminder_switch;
	private BleService mBleservice;

	private int unit;

	private MyDialog mMyDialog;

	private MyBroadCastReceiver mReceiver;

	private SharedPreferences share;

	private Editor edit;

	private DeviceEntity entity;

	private class MyBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleController.ACTION_REMINDER_OK.equals(action)) {
				UiTools.showAlert(getApplicationContext(), getResources()
						.getString(R.string.setting_seccess));
			} else if (BleController.ACTION_FAIL.equals(action)) {

			}
			mMyDialog.dismiss();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_reminder);
		initData();
		initView();
		setListener();
		registerReceiver(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		registerReceiver(false);
	}

	private void registerReceiver(boolean flag) {
		if (flag) {
			mReceiver = new MyBroadCastReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(BleController.ACTION_REMINDER_OK);
			filter.addAction(BleController.ACTION_FAIL);
			filter.addAction(BleService.ACTION_GATT_DISCONNECTED);
			filter.addAction(BleService.ACTION_GATT_ERROR);
			registerReceiver(mReceiver, filter);
		} else {
			if (mReceiver != null) {
				unregisterReceiver(mReceiver);
			}
		}
	}

	private void setListener() {
		findViewById(R.id.reminder_starttime).setOnClickListener(this);
		findViewById(R.id.reminder_endtime).setOnClickListener(this);
		findViewById(R.id.reminder_net).setOnClickListener(this);
		findViewById(R.id.reminder_back).setOnClickListener(this);
		findViewById(R.id.reminder_save).setOnClickListener(this);
		reminder_switch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean flag) {
						// TODO Auto-generated method stub
						reminderSwitch = (byte) (flag ? 1 : 0);
					}
				});

	}

	private void initView() {
		tv_startTime = (TextView) findViewById(R.id.reminder_starttime_tv);
		tv_endTime = (TextView) findViewById(R.id.reminder_endtime_tv);
		tv_net = (TextView) findViewById(R.id.reminder_net_tv);
		// buSwitch.setValue(reminderSwitch == 0 ? 1 : 0);
		reminder_switch = (CheckBox) findViewById(R.id.reminder_switch);
		reminder_switch.setChecked(reminderSwitch == 1 ? true : false);
		tv_startTime.setText(getTime(startHour, startMinute));
		tv_endTime.setText(getTime(endHour, endMinute));
		tv_net.setText(NETITEMS[checkedItem]);
	}

	private void initData() {
		// 判断手机制式
		ContentResolver cv = this.getContentResolver(); // 获取当前系统设置
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);
		if ("24".equals(strTimeFormat))
			unit = 0;
		else
			unit = 1;

		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME,
				Activity.MODE_PRIVATE); // 指定操作的文件名
		edit = share.edit();
		entity = (DeviceEntity) getIntent().getSerializableExtra("device");

//		String unit = share.getString(entity.getMac()
//				+ SystemConfig.KEY_MEASURE, "");
//		this.unit = unit != null && unit.equals("Metric") ? 0 : 1;

		String reminder_switch = share.getString(entity.getMac()
				+ SystemConfig.KEY_REMINDER_SWITCH, "0");
		if (!Tools.isEmpty(reminder_switch)) {
			this.reminderSwitch = Byte.valueOf(reminder_switch);
		} else {
			this.reminderSwitch = 0;
		}

		String reminder_startTime = share.getString(entity.getMac()
				+ SystemConfig.KEY_REMINDER_STARTTIME, "");
		if (!Tools.isEmpty(reminder_startTime)) {
			String[] s = reminder_startTime.split(":");
			if (s != null && s.length == 2) {
				startHour = Integer.valueOf(s[0]);
				startMinute = Integer.valueOf(s[1]);
			}
		} else {
			startHour = 8;
			startMinute = 0;
		}

		String reminder_endTime = share.getString(entity.getMac()
				+ SystemConfig.KEY_REMINDER_ENDTIME, "");
		if (!Tools.isEmpty(reminder_endTime)) {
			String[] s = reminder_endTime.split(":");
			if (s != null && s.length == 2) {
				endHour = Integer.valueOf(s[0]);
				endMinute = Integer.valueOf(s[1]);
			}
		} else {
			endHour = 18;
			endMinute = 0;
		}

		String reminder_time = share.getString(entity.getMac()
				+ SystemConfig.KEY_REMINDER_TIME, "");
		if (!Tools.isEmpty(reminder_time)) {
			this.checkedItem = Byte.valueOf(reminder_time);
		} else {
			this.checkedItem = 0;
		}

	}

	private void setTimeString(TextView tv, String time) {
		// if (this.unit == 0) {
		tv.setText(time);
		/*
		 * } else { String[] times = time.split(":"); int hour =
		 * Integer.valueOf(times[0]); if (hour >= 12) { hour = hour - 12;
		 * tv.setText(String.format("%02d", hour) + ":" + times[1] + "PM"); }
		 * else if (hour < 12) { tv.setText(time + "AM"); } }
		 */
	}

	private void showDialog(String msg) {
		if (mMyDialog == null)
			mMyDialog = new MyDialog(this, msg, false);
		mMyDialog.setMsg(msg);

		if (mMyDialog.isShowing() == false)
			mMyDialog.show();
	}

	private void showTimePickDialog(int time, int minute) {
		TimePickerDialog dialog = new TimePickerDialog(this,
				R.style.dialog_style_light, new OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						if (whichTime == STARTTIME) {
							startHour = hourOfDay;
							startMinute = minute;
							tv_startTime
									.setText(getTime(startHour, startMinute));
						} else {
							endHour = hourOfDay;
							endMinute = minute;
							tv_endTime.setText(getTime(endHour, endMinute));
						}
					}
				}, time, minute, unit == 0?true:false);

		dialog.show();
	}

	// ��ʾѡ���˶��¼��ĶԻ���
	private void showNetDialog() {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this,
				R.style.dialog_style_light);
		builder.setSingleChoiceItems(NETITEMS, checkedItem,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						checkedItem = which;
						tv_net.setText(NETITEMS[checkedItem]);
						dialog.dismiss();
					}
				});
		dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reminder_back:
			finish();
			break;
		case R.id.reminder_starttime:
			whichTime = STARTTIME;
			showTimePickDialog(startHour, startMinute);
			break;
		case R.id.reminder_endtime:
			whichTime = ENDTIME;
			showTimePickDialog(endHour, endMinute);
			break;
		case R.id.reminder_net:
			showNetDialog();
			break;
		case R.id.reminder_save:
			save();
			break;
		default:
			break;
		}
	}

	private void save() {
		store(); // �ȱ��浽���ݿ�
//		sendBroadcast(new Intent(Global.ACTION_REMAIN_CHANGED));
		if (MyApp.getIntance().mService != null) {
			if (MyApp.getIntance().mService.mConnectionState == MyApp
					.getIntance().mService.STATE_CONNECTED) {
				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.LONG_TIME_SLEEP;
				MyApp.getIntance().mService.sendLongTimeSleep();
			} else {
//				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.LONG_TIME_SLEEP;
//				MyApp.getIntance().mService.connect(entity.getMac());
			}

			showDialog(getResources().getString(R.string.please_wait));
		}
		/*
		 * if (mBleservice != null) { mBleservice.autoconnect(1,
		 * BleService.COMMAND_EVENT);
		 * showDialog(getResources().getString(R.string.plzwait)); }
		 */
	}

	private void store() {
		String startTime = startHour + ":" + startMinute;
		String endTime = endHour + ":" + endMinute;
		edit.putString(entity.getMac() + SystemConfig.KEY_REMINDER_SWITCH,
				String.valueOf(reminderSwitch)).commit();
		edit.putString(entity.getMac() + SystemConfig.KEY_REMINDER_TIME,
				String.valueOf(checkedItem)).commit();
		edit.putString(entity.getMac() + SystemConfig.KEY_REMINDER_STARTTIME,
				String.valueOf(startTime)).commit();
		edit.putString(entity.getMac() + SystemConfig.KEY_REMINDER_ENDTIME,
				String.valueOf(endTime)).commit();
	}

	private String getTime(int hour, int minute) {
		String time = null;
		 if (this.unit == 0) {
			 time = String.format("%02d", hour) + ":"
					 + String.format("%02d", minute);
		 } else {
			 time = Tools.time24Totime12(String.format("%02d", hour) + ":"
				+ String.format("%02d", minute));
		 }
		return time;
	}

}
