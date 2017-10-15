package com.cn.zhihengchuang.walkbank.activity;

import java.util.ArrayList;
import java.util.List;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OldAlarmActivity extends Activity implements OnClickListener{
	private static final String EVENT = "EVENT";

	private ImageView tag1, tag2, tag3, tag4,tag5;

	private List<ImageView> tagList;

	private TextView detail_time, detail_repeat;

	private EditText detail_name;

	private int unit;

	private CheckBox switchButton1, switchButton2, switchButton3,
			switchButton4, repeatSwitch;

	private TextView tvA1Time, tvA2Time, tvA3Time, tvA4Time, tvA1Name,
			tvA2Name, tvA3Name, tvA4Name;

	private int currentTag = 1; // ��ǰ���ӱ�־λ

	private byte alarm1Switch, alarm2Switch, alarm3Switch, alarm4Switch; // 4�����ӵĿ���

	private byte alarm1SwitchRepeat, alarm2SwitchRepeat, alarm3SwitchRepeat,
			alarm4SwitchRepeat;

	private String alarm1Name, alarm2Name, alarm3Name, alarm4Name; // ���ӱ�ע

	private String alarm1Time, alarm2Time, alarm3Time, alarm4Time; // ����ʱ��

	private boolean[] alarm1Repeat, alarm2Repeat, alarm3Repeat, alarm4Repeat; // �����ظ�

	private StringBuffer alarm1RepeatText, alarm2RepeatText, alarm3RepeatText,
			alarm4RepeatText;

	// private ConfigDAO mDao;

	private String[] weeks;

	private BleService mBleservice;

	private MyDialog mMyDialog;

	private MyBroadCastReceiver mReceiver;

	private SharedPreferences share;

	private Editor edit;

	private DeviceEntity entity;






	private class MyBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleController.ACTION_ALARM_OK.equals(action)) {
				UiTools.showAlert(getApplicationContext(), "闹钟设置成功");
			} else if (BleController.ACTION_FAIL.equals(action)) {

			}else if (BleService.ACTION_GATT_ERROR.equals(action)) {
				UiTools.showAlert(getApplicationContext(), R.string.alarm_clock_set_failed);
			}else if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
				UiTools.showAlert(getApplicationContext(), R.string.alarm_clock_set_failed);
			}
			mMyDialog.dismiss();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_alarm_old);
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME,
				Activity.MODE_PRIVATE); // 指定操作的文件名
		edit = share.edit();
		entity = (DeviceEntity) getIntent().getSerializableExtra("device");
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
			filter.addAction(BleController.ACTION_ALARM_OK);
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

	/**
	 * ��ʼ��������ʾ
	 */
	private void initData() {
		weeks = new String[] { getResources().getString(R.string.sunday),
				getResources().getString(R.string.monday),
				getResources().getString(R.string.tuesday),
				getResources().getString(R.string.wednesday),
				getResources().getString(R.string.thursday),
				getResources().getString(R.string.friday),
				getResources().getString(R.string.saturday) };
		String alarm1_name = share.getString(SystemConfig.KEY_ALARM1_NAME
				+ entity.getMac(), "");
		if (!Tools.isEmpty(alarm1_name)) {
			this.alarm1Name = alarm1_name;
		} else {
			this.alarm1Name = EVENT + 1;
		}
		String alarm2_name = share.getString(SystemConfig.KEY_ALARM2_NAME
				+ entity.getMac(), "");
		if (!Tools.isEmpty(alarm2_name)) {
			this.alarm2Name = alarm2_name;
		} else {
			this.alarm2Name = EVENT + 2;
		}
		String alarm3_name = share.getString(SystemConfig.KEY_ALARM3_NAME
				+ entity.getMac(), "");
		if (!Tools.isEmpty(alarm3_name)) {
			this.alarm3Name = alarm3_name;
		} else {
			this.alarm3Name = EVENT + 3;
		}
		String alarm4_name = share.getString(SystemConfig.KEY_ALARM4_NAME
				+ entity.getMac(), "");
		if (!Tools.isEmpty(alarm4_name)) {
			this.alarm4Name = alarm4_name;
		} else {
			this.alarm4Name = EVENT + 4;
		}
		
		String alarm1_switch = share.getString(SystemConfig.KEY_ALARM1_SWITCH
				+ entity.getMac(), "");

		if (!Tools.isEmpty(alarm1_switch)) {
			this.alarm1Switch = Byte.valueOf(alarm1_switch);
		} else {
			this.alarm1Switch = 0;
		}
		String alarm2_switch = share.getString(SystemConfig.KEY_ALARM2_SWITCH
				+ entity.getMac(), "");
		if (!Tools.isEmpty(alarm2_switch)) {
			this.alarm2Switch = Byte.valueOf(alarm2_switch);
		} else {
			this.alarm2Switch = 0;
		}
		String alarm3_switch = share.getString(SystemConfig.KEY_ALARM3_SWITCH
				+ entity.getMac(), "");
		if (!Tools.isEmpty(alarm3_switch)) {
			this.alarm3Switch = Byte.valueOf(alarm3_switch);
		} else {
			this.alarm3Switch = 0;
		}
		String alarm4_switch = share.getString(SystemConfig.KEY_ALARM4_SWITCH
				+ entity.getMac(), "");

		if (!Tools.isEmpty(alarm4_switch)) {
			this.alarm4Switch = Byte.valueOf(alarm4_switch);
		} else {
			this.alarm4Switch = 0;
		}
		
		String alarm1_time = share.getString(SystemConfig.KEY_ALARM1_TIME
				+ entity.getMac(), "");
		if (!Tools.isEmpty(alarm1_time)) {
			this.alarm1Time = alarm1_time;
		} else {
			this.alarm1Time = "08:00";
		}
		String alarm2_time = share.getString(SystemConfig.KEY_ALARM2_TIME
				+ entity.getMac(), "");
		if (!Tools.isEmpty(alarm2_time)) {
			this.alarm2Time = alarm2_time;
		} else {
			this.alarm2Time = "08:00";
		}
		String alarm3_time = share.getString(SystemConfig.KEY_ALARM3_TIME
				+ entity.getMac(), "");
		if (!Tools.isEmpty(alarm3_time)) {
			this.alarm3Time = alarm3_time;
		} else {
			this.alarm3Time = "08:00";
		}
		String alarm4_time = share.getString(SystemConfig.KEY_ALARM4_TIME
				+ entity.getMac(), "");
		if (!Tools.isEmpty(alarm4_time)) {
			this.alarm4Time = alarm4_time;
		} else {
			this.alarm4Time = "08:00";
		}
		
		String alarm1_repeat = share.getString(SystemConfig.KEY_ALARM1_REPEAT
				+ entity.getMac(), "");

		this.alarm1Repeat = new boolean[7];
		if (!Tools.isEmpty(alarm1_repeat)) {
			byte i = Byte.valueOf(alarm1_repeat);
			byte n = (byte) 0x01;
			for (int j = 0; j < alarm1Repeat.length; j++) {
				alarm1Repeat[j] = (i & (n << j)) != 0 ? true : false;
				if (alarm1Repeat[j]) {
					if (alarm1RepeatText == null)
						alarm1RepeatText = new StringBuffer();
					alarm1RepeatText.append(weeks[j] + " ");
				}
			}
		}

		String alarm2_repeat = share.getString(SystemConfig.KEY_ALARM2_REPEAT
				+ entity.getMac(), "");
		this.alarm2Repeat = new boolean[7];
		if (!Tools.isEmpty(alarm2_repeat)) {
			byte i = Byte.valueOf(alarm2_repeat);
			byte n = (byte) 0x01;
			for (int j = 0; j < alarm2Repeat.length; j++) {
				alarm2Repeat[j] = (i & (n << j)) != 0 ? true : false;
				if (alarm2Repeat[j]) {
					if (alarm2RepeatText == null)
						alarm2RepeatText = new StringBuffer();
					alarm2RepeatText.append(weeks[j] + " ");
				}
			}
		}
		String alarm3_repeat = share.getString(SystemConfig.KEY_ALARM3_REPEAT
				+ entity.getMac(), "");
		this.alarm3Repeat = new boolean[7];
		if (!Tools.isEmpty(alarm3_repeat)) {
			byte i = Byte.valueOf(alarm3_repeat);
			byte n = (byte) 0x01;
			for (int j = 0; j < alarm3Repeat.length; j++) {
				alarm3Repeat[j] = (i & (n << j)) != 0 ? true : false;
				if (alarm3Repeat[j]) {
					if (alarm3RepeatText == null)
						alarm3RepeatText = new StringBuffer();
					alarm3RepeatText.append(weeks[j] + " ");
				}
			}
		}
		String alarm4_repeat = share.getString(SystemConfig.KEY_ALARM4_REPEAT
				+ entity.getMac(), "");
		this.alarm4Repeat = new boolean[7];
		if (!Tools.isEmpty(alarm4_repeat)) {
			byte i = Byte.valueOf(alarm4_repeat);
			byte n = (byte) 0x01;
			for (int j = 0; j < alarm4Repeat.length; j++) {
				alarm4Repeat[j] = (i & (n << j)) != 0 ? true : false;
				if (alarm4Repeat[j]) {
					if (alarm4RepeatText == null)
						alarm4RepeatText = new StringBuffer();
					alarm4RepeatText.append(weeks[j] + " ");
				}
			}
		}
		
		String alarm1_repeatSwitch = share.getString(
				SystemConfig.KEY_ALARM1_REPEATSWITCH + entity.getMac(), "");
		if (!Tools.isEmpty(alarm1_repeatSwitch)) {
			this.alarm1SwitchRepeat = Byte.valueOf(alarm1_repeatSwitch);
		} else {
			this.alarm1SwitchRepeat = 0;
		}
		String alarm2_repeatSwitch = share.getString(
				SystemConfig.KEY_ALARM2_REPEATSWITCH + entity.getMac(), "");
		// String alarm2_repeatSwitch = mDao.getValueByKey();
		if (!Tools.isEmpty(alarm2_repeatSwitch)) {
			this.alarm2SwitchRepeat = Byte.valueOf(alarm2_repeatSwitch);
		} else {
			this.alarm2SwitchRepeat = 0;
		}
		String alarm3_repeatSwitch = share.getString(
				SystemConfig.KEY_ALARM3_REPEATSWITCH + entity.getMac(), "");
		if (!Tools.isEmpty(alarm3_repeatSwitch)) {
			this.alarm3SwitchRepeat = Byte.valueOf(alarm3_repeatSwitch);
		} else {
			this.alarm3SwitchRepeat = 0;
		}
		String alarm4_repeatSwitch = share.getString(
				SystemConfig.KEY_ALARM4_REPEATSWITCH + entity.getMac(), "");
		if (!Tools.isEmpty(alarm4_repeatSwitch)) {
			this.alarm4SwitchRepeat = Byte.valueOf(alarm4_repeatSwitch);
		} else {
			this.alarm4SwitchRepeat = 0;
		}
		
		mBleservice = MyApp.getIntance().mService;

	}

	private void setListener() {
		findViewById(R.id.alarm_back).setOnClickListener(this);
		findViewById(R.id.alarm_save).setOnClickListener(this);
		findViewById(R.id.alarm_e1).setOnClickListener(this);
		findViewById(R.id.alarm_e2).setOnClickListener(this);
		findViewById(R.id.alarm_e3).setOnClickListener(this);
		findViewById(R.id.alarm_e4).setOnClickListener(this);
		findViewById(R.id.alarm_time).setOnClickListener(this);
		findViewById(R.id.alarm_repeat).setOnClickListener(this);
		switchButton1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean flag) {
				int on = flag == false ? 0 : 1;
				switchOnOff(1, on);
			}
		});
		switchButton2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				int on = arg1 == false ? 0 : 1;
				switchOnOff(2, on);
			}
		});
		switchButton3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				int on = arg1 == false ? 0 : 1;
				switchOnOff(3, on);
			}
		});
		switchButton4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				int on = arg1 == false ? 0 : 1;
				switchOnOff(4, on);
			}
		});
		

		// repeatSwitch.setOnSwitchChangeListener(new OnSwitchChangeListener() {
		//
		// @Override
		// public void onSwitchChange(int flag) {
		// switch (AlarmActivity.this.currentTag) {
		// case 1:
		// alarm1SwitchRepeat = (byte) (flag == 0?1:0);
		// break;
		// case 2:
		// alarm2SwitchRepeat = (byte) (flag == 0?1:0);
		// break;
		// case 3:
		// alarm3SwitchRepeat = (byte) (flag == 0?1:0);
		// break;
		// case 4:
		// alarm4SwitchRepeat = (byte) (flag == 0?1:0);
		// break;
		//
		// default:
		// break;
		// }
		// }
		// });

		detail_name.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				switch (currentTag) {
				case 1:
					alarm1Name = s.toString();
					tvA1Name.setText(alarm1Name);
					break;
				case 2:
					alarm2Name = s.toString();
					tvA2Name.setText(alarm2Name);
					break;
				case 3:
					alarm3Name = s.toString();
					tvA3Name.setText(alarm3Name);
					break;
				case 4:
					alarm4Name = s.toString();
					tvA4Name.setText(alarm4Name);
					break;
				
				default:
					break;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void initView() {

		tag1 = (ImageView) findViewById(R.id.alarm_e1_tag);
		tag2 = (ImageView) findViewById(R.id.alarm_e2_tag);
		tag3 = (ImageView) findViewById(R.id.alarm_e3_tag);
		tag4 = (ImageView) findViewById(R.id.alarm_e4_tag);

		tagList = new ArrayList<ImageView>();
		tagList.add(tag1);
		tagList.add(tag2);
		tagList.add(tag3);
		tagList.add(tag4);

		detail_name = (EditText) findViewById(R.id.alarm_tag);
		detail_time = (TextView) findViewById(R.id.alarm_time_tv);
		detail_repeat = (TextView) findViewById(R.id.alarm_repeat_tv);
		// repeatSwitch = (SwitchButton) findViewById(R.id.alarm_repeat_switch);

		switchButton1 = (CheckBox) findViewById(R.id.event1);
		switchButton2 = (CheckBox) findViewById(R.id.event2);
		switchButton3 = (CheckBox) findViewById(R.id.event3);
		switchButton4 = (CheckBox) findViewById(R.id.event4);
		switchButton1.setChecked(alarm1Switch == 0 ? true : false);
		switchButton2.setChecked(alarm2Switch == 0 ? true : false);
		switchButton3.setChecked(alarm3Switch == 0 ? true : false);
		switchButton4.setChecked(alarm4Switch == 0 ? true : false);

		tvA1Name = (TextView) findViewById(R.id.alarm_e1_tv);
		tvA2Name = (TextView) findViewById(R.id.alarm_e2_tv);
		tvA3Name = (TextView) findViewById(R.id.alarm_e3_tv);
		tvA4Name = (TextView) findViewById(R.id.alarm_e4_tv);
		tvA1Name.setText(alarm1Name);
		tvA2Name.setText(alarm2Name);
		tvA3Name.setText(alarm3Name);
		tvA4Name.setText(alarm4Name);
		
		tvA1Time = (TextView) findViewById(R.id.alarm_e1_time);
		tvA2Time = (TextView) findViewById(R.id.alarm_e2_time);
		tvA3Time = (TextView) findViewById(R.id.alarm_e3_time);
		tvA4Time = (TextView) findViewById(R.id.alarm_e4_time);
		tvA1Time.setText(alarm1Time);
		tvA2Time.setText(alarm2Time);
		tvA3Time.setText(alarm3Time);
		tvA4Time.setText(alarm4Time);
		setTimeString(tvA1Time, alarm1Time);
		setTimeString(tvA2Time, alarm2Time);
		setTimeString(tvA3Time, alarm3Time);
		setTimeString(tvA4Time, alarm4Time);

		switchTag(1);
	}

	// ���ݹ�Ӣ����ʾʱ��
	private void setTimeString(TextView tv, String time) {
		tv.setText(time);
		/*
		 * if (this.unit == 0){ tv.setText(time); }else{
		 * tv.setText(Tools.time24Totime12(time)); }
		 */
	}

	private void switchTag(int value) {
		currentTag = value;
		for (int i = 0; i < tagList.size(); i++) {
			if (i == value - 1) {
				tagList.get(i).setVisibility(View.VISIBLE);
			} else {
				tagList.get(i).setVisibility(View.GONE);
			}
		}
		switch (value) {
		case 1:
			detail_name.setText(alarm1Name);
			setTimeString(detail_time, alarm1Time);
			detail_repeat.setText(alarm1RepeatText);
			// repeatSwitch.setValue(alarm1SwitchRepeat == 0?1:0);
			break;
		case 2:
			detail_name.setText(alarm2Name);
			setTimeString(detail_time, alarm2Time);
			detail_repeat.setText(alarm2RepeatText);
			// repeatSwitch.setValue(alarm2SwitchRepeat == 0?1:0);
			break;
		case 3:
			detail_name.setText(alarm3Name);
			setTimeString(detail_time, alarm3Time);
			detail_repeat.setText(alarm3RepeatText);
			// repeatSwitch.setValue(alarm3SwitchRepeat == 0?1:0);
			break;
		case 4:
			detail_name.setText(alarm4Name);
			setTimeString(detail_time, alarm4Time);
			detail_repeat.setText(alarm4RepeatText);
			// repeatSwitch.setValue(alarm4SwitchRepeat == 0?1:0);
			break;
		

		default:
			break;
		}
	}

	/**
	 * 
	 * @param tag
	 *            �ڼ�������
	 * @param flag
	 *            ���ӿ��ر�־
	 */
	private void switchOnOff(int tag, int flag) {
		switch (tag) {
		case 1:
			if (flag == 0) {
				alarm1Switch = 1;
			} else {
				alarm1Switch = 0;
			}
			break;
		case 2:
			if (flag == 0) {
				alarm2Switch = 1;
			} else {
				alarm2Switch = 0;
			}
			break;
		case 3:
			if (flag == 0) {
				alarm3Switch = 1;
			} else {
				alarm3Switch = 0;
			}
			break;
		case 4:
			if (flag == 0) {
				alarm4Switch = 1;
			} else {
				alarm4Switch = 0;
			}
			break;
		case 5:
			
			break;

		default:
			break;
		}
	}

	private void showRepeatDialog() {
		final boolean[] check = new boolean[7];
		switch (currentTag) {
		case 1:
			for (int i = 0; i < check.length; i++) {
				check[i] = alarm1Repeat[i];
			}
			break;
		case 2:
			for (int i = 0; i < check.length; i++) {
				check[i] = alarm2Repeat[i];
			}
			break;
		case 3:
			for (int i = 0; i < check.length; i++) {
				check[i] = alarm3Repeat[i];
			}
			break;
		case 4:
			for (int i = 0; i < check.length; i++) {
				check[i] = alarm4Repeat[i];
			}
			break;
		
		default:
			break;
		}
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this,
				R.style.dialog_style_light);
		builder.setMultiChoiceItems(weeks, check,
				new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						check[which] = isChecked;
					}
				});
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						StringBuffer text = new StringBuffer();
						for (int j = 0; j < check.length; j++) {
							if (check[j]) {
								text.append(weeks[j] + " ");
							}
						}
						switch (currentTag) {
						case 1:
							alarm1Repeat = check;
							alarm1RepeatText = text;
							break;
						case 2:
							alarm2Repeat = check;
							alarm2RepeatText = text;
							break;
						case 3:
							alarm3Repeat = check;
							alarm3RepeatText = text;
							break;
						case 4:
							alarm4Repeat = check;
							alarm4RepeatText = text;
							break;
						
						default:
							break;
						}
						detail_repeat.setText(text);
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog = builder.create();
		dialog.show();
	}

	private void showTimePickDialog() {
		int hour = 0;
		int minute = 0;
		String[] times = new String[] { "00", "00" };
		switch (currentTag) {
		case 1:
			times = alarm1Time.split(":");
			break;
		case 2:
			times = alarm2Time.split(":");
			break;
		case 3:
			times = alarm3Time.split(":");
			break;
		case 4:
			times = alarm4Time.split(":");
			break;
		
		default:
			break;
		}
		hour = Integer.valueOf(times[0]);
		minute = Integer.valueOf(times[1]);
		TimePickerDialog dialog = new TimePickerDialog(this,
				R.style.dialog_style_light, new OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						switch (currentTag) {
						case 1:
							alarm1Time = getTime(hourOfDay, minute);
							setTimeString(detail_time, alarm1Time);
							setTimeString(tvA1Time, alarm1Time);
							break;
						case 2:
							alarm2Time = getTime(hourOfDay, minute);
							setTimeString(detail_time, alarm2Time);
							setTimeString(tvA2Time, alarm2Time);
							break;
						case 3:
							alarm3Time = getTime(hourOfDay, minute);
							setTimeString(detail_time, alarm3Time);
							setTimeString(tvA3Time, alarm3Time);
							break;
						case 4:
							alarm4Time = getTime(hourOfDay, minute);
							setTimeString(detail_time, alarm4Time);
							setTimeString(tvA4Time, alarm4Time);
							break;
						

						default:
							break;
						}
					}
				}, hour, minute, true);
		dialog.show();
	}

	private String getTime(int hour, int minute) {
		String time = null;
		time = String.format("%02d", hour) + ":"
				+ String.format("%02d", minute);
		return time;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.alarm_back:
			finish();
			break;
		case R.id.alarm_e1:
			switchTag(1);
			break;
		case R.id.alarm_e2:
			switchTag(2);
			break;
		case R.id.alarm_e3:
			switchTag(3);
			break;
		case R.id.alarm_e4:
			switchTag(4);
			break;
		case R.id.alarm_e5:
			switchTag(5);
			break;
		case R.id.alarm_time:
			showTimePickDialog();
			break;
		case R.id.alarm_repeat:
			showRepeatDialog();
			break;
		case R.id.alarm_save:
			save();
			break;

		default:
			break;
		}
	}

	private void save() {
		store(); //
		sendBroadcast(new Intent(Global.ACTION_ALARM_CHANGED));
//		if (MyApp.getIntance().mService != null) {
//			if (MyApp.getIntance().mService.mConnectionState == MyApp
//					.getIntance().mService.STATE_CONNECTED) {
//				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMAND_ALARM;
//				MyApp.getIntance().mService.sendAlarmTime();
//			}else{
//				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMAND_ALARM;
//				MyApp.getIntance().mService.connect(entity.getMac());
//			}
//			showDialog(getResources().getString(R.string.please_wait));
//		}
	}

	private void showDialog(String msg) {
		if (mMyDialog == null)
			mMyDialog = new MyDialog(this, msg, false);
		mMyDialog.setMsg(msg);

		if (mMyDialog.isShowing() == false)
			mMyDialog.show();
	}

	/**
	 * ���浽���ݿ�
	 */
	private void store() {
		edit.putString(SystemConfig.KEY_ALARM1_NAME + entity.getMac(),
				alarm1Name).commit();
		edit.putString(SystemConfig.KEY_ALARM2_NAME + entity.getMac(),
				alarm2Name).commit();
		edit.putString(SystemConfig.KEY_ALARM3_NAME + entity.getMac(),
				alarm3Name).commit();
		edit.putString(SystemConfig.KEY_ALARM4_NAME + entity.getMac(),
				alarm4Name).commit();
		
		edit.putString(SystemConfig.KEY_ALARM1_TIME + entity.getMac(),
				alarm1Time).commit();
		edit.putString(SystemConfig.KEY_ALARM2_TIME + entity.getMac(),
				alarm2Time).commit();
		edit.putString(SystemConfig.KEY_ALARM3_TIME + entity.getMac(),
				alarm3Time).commit();
		edit.putString(SystemConfig.KEY_ALARM4_TIME + entity.getMac(),
				alarm4Time).commit();
		

		edit.putString(SystemConfig.KEY_ALARM1_SWITCH + entity.getMac(),
				String.valueOf(alarm1Switch)).commit();
		edit.putString(SystemConfig.KEY_ALARM2_SWITCH + entity.getMac(),
				String.valueOf(alarm2Switch)).commit();
		edit.putString(SystemConfig.KEY_ALARM3_SWITCH + entity.getMac(),
				String.valueOf(alarm3Switch)).commit();
		edit.putString(SystemConfig.KEY_ALARM4_SWITCH + entity.getMac(),
				String.valueOf(alarm4Switch)).commit();
		
		edit.putString(SystemConfig.KEY_ALARM1_REPEATSWITCH + entity.getMac(),
				String.valueOf(alarm1SwitchRepeat)).commit();
		edit.putString(SystemConfig.KEY_ALARM2_REPEATSWITCH + entity.getMac(),
				String.valueOf(alarm2SwitchRepeat)).commit();
		edit.putString(SystemConfig.KEY_ALARM3_REPEATSWITCH + entity.getMac(),
				String.valueOf(alarm3SwitchRepeat)).commit();
		edit.putString(SystemConfig.KEY_ALARM4_REPEATSWITCH + entity.getMac(),
				String.valueOf(alarm4SwitchRepeat)).commit();
		
		byte a1 = (byte) 0x01;
		byte alarm1_repeat = 0;
		for (int i = 0; i < alarm1Repeat.length; i++) {
			if (alarm1Repeat[i]) {
				alarm1_repeat = (byte) (alarm1_repeat | (a1 << i));
			}
		}
		// if (alarm1SwitchRepeat == 1){
		// alarm1_repeat = (byte) (alarm1_repeat | 0x80);
		// }
		edit.putString(SystemConfig.KEY_ALARM1_REPEAT + entity.getMac(),
				String.valueOf(alarm1_repeat)).commit();

		byte a2 = (byte) 0x01;
		byte alarm2_repeat = 0;
		for (int i = 0; i < alarm2Repeat.length; i++) {
			if (alarm2Repeat[i]) {
				alarm2_repeat = (byte) (alarm2_repeat | (a2 << i));
			}
		}
		// if (alarm2SwitchRepeat == 1){
		// alarm2_repeat = (byte) (alarm2_repeat | 0x80);
		// }
		edit.putString(SystemConfig.KEY_ALARM2_REPEAT + entity.getMac(),
				String.valueOf(alarm2_repeat)).commit();

		byte a3 = (byte) 0x01;
		byte alarm3_repeat = 0;
		for (int i = 0; i < alarm3Repeat.length; i++) {
			if (alarm3Repeat[i]) {
				alarm3_repeat = (byte) (alarm3_repeat | (a3 << i));
			}
		}
		// if (alarm3SwitchRepeat == 1){
		// alarm3_repeat = (byte) (alarm3_repeat | 0x80);
		// }
		edit.putString(SystemConfig.KEY_ALARM3_REPEAT + entity.getMac(),
				String.valueOf(alarm3_repeat)).commit();
		byte a4 = (byte) 0x01;
		byte alarm4_repeat = 0;
		for (int i = 0; i < alarm4Repeat.length; i++) {
			if (alarm4Repeat[i]) {
				alarm4_repeat = (byte) (alarm4_repeat | (a4 << i));
			}
		}
		// if (alarm4SwitchRepeat == 1){
		// alarm4_repeat = (byte) (alarm4_repeat | 0x80);
		// }
		edit.putString(SystemConfig.KEY_ALARM4_REPEAT + entity.getMac(),
				String.valueOf(alarm4_repeat)).commit();
		byte a5 = (byte) 0x01;
		
		// if (alarm4SwitchRepeat == 1){
		// alarm4_repeat = (byte) (alarm4_repeat | 0x80);
		// }
		
	}

}
