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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.zhihengchuang.walkbank.ble.BleController;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.util.UiTools;
import com.cn.zhihengchuang.walkbank.view.MyDialog;
import com.isport.trackernew.R;
import com.lingb.global.Global;
import com.lingb.helper.SpHelper;

public class SleepActivity extends Activity {

//	private CharSequence[] TIMES_REMINDER = new CharSequence[] { "5", "10", "15", "20", "30", "60" };
	private CharSequence[] TIMES_REMINDER = new CharSequence[60];
	public static final CharSequence[] TIMES_TARGET = new CharSequence[] { "5", "6", "7", "8", "9", "10", "11", "12"};

	public final static String KEY_TIME_BEGIN_HOUR = "sleep_time_begin_hour";
	public final static String KEY_TIME_END_HOUR = "sleep_time_end_hour";
	public final static String KEY_TIME_BEGIN_MINUTE = "sleep_time_begin_minute";
	public final static String KEY_TIME_END_MINUTE = "sleep_time_end_minute";
	public final static String KEY_REMINDER = "sleep_reminder";
	public final static String KEY_LUNCH_BEGIN_HOUR = "sleep_lunch_begin_hour";
	public final static String KEY_LUNCH_END_HOUR = "sleep_lunch_end_hour";
	public final static String KEY_LUNCH_BEGIN_MINUTE = "sleep_lunch_begin_minute";
	public final static String KEY_LUNCH_END_MINUTE = "sleep_lunch_end_minute";
	public final static String KEY_TARGET_HOUR = "sleep_target_hour";
	public final static String KEY_TARGET_MINUTE = "sleep_target_minute";

	public final static String KEY_IS_SLEEP_AUTO = "is_sleep_auto_open";
	public final static String KEY_IS_SLEEP_TIME = "is_sleep_time_open";
	public final static String KEY_IS_SLEEP_REMIND = "is_sleep_reminder_open";
	public final static String KEY_IS_SLEEP_LUNCH = "is_sleep_lunch_open";

	private final int TYPE_SLEEP_BEIGN = 0;
	private final int TYPE_SLEEP_END = 1;
	private final int TYPE_LUNCH_BEGIN = 2;
	private final int TYPE_LUNCH_END = 3;

	private final int TYPE_REMINDER = 4;
	private final int TYPE_TARGET = 5;

	private int unit = 0;
	/** 睡眠提醒当前条目 */
	private int indexReminder = 15;
	/** 睡眠目标当前条目 */
	private int indexTarget = 3;
	private MyDialog mMyDialog;
	private MyBroadCastReceiver mReceiver;
	
	private int targetHour = 8;
	private int targetMinute = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_sleep);
		initUI();
		initValue();
		registerReceiver(true);
	}

	@Override
	protected void onDestroy() {
		registerReceiver(false);
		super.onDestroy();
	}

	private class MyBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleController.ACTION_AUTO_SLEEP_OK.equals(action)) {
				UiTools.showAlert(getApplicationContext(), getResources().getString(R.string.setting_seccess));
			} else if (BleController.ACTION_FAIL.equals(action)) {

			}
			mMyDialog.dismiss();
		}
	}

	private void registerReceiver(boolean flag) {
		if (flag) {
			mReceiver = new MyBroadCastReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(BleController.ACTION_AUTO_SLEEP_OK);
			filter.addAction(BleController.ACTION_FAIL);
			registerReceiver(mReceiver, filter);
		} else {
			if (mReceiver != null) {
				unregisterReceiver(mReceiver);
			}
		}
	}

	private OnClickListener myOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.return_back:
				SleepActivity.this.finish();
				break;
			case R.id.text_save:
				clickSave();
				break;
			case R.id.text_sleep_time_begin:
				String[] timeBegin = text_time_begin.getText().toString().split(":");
				showTimePickDialog(Integer.parseInt(timeBegin[0]), Integer.parseInt(timeBegin[1]), TYPE_SLEEP_BEIGN);
				break;
			case R.id.text_sleep_time_end:
				String[] timeEnd = text_time_end.getText().toString().split(":");
				showTimePickDialog(Integer.parseInt(timeEnd[0]), Integer.parseInt(timeEnd[1]), TYPE_SLEEP_END);
				break;
			case R.id.text_lunch_begin:
				String[] lunchBegin = text_lunch_begin.getText().toString().split(":");
				showTimePickDialog(Integer.parseInt(lunchBegin[0]), Integer.parseInt(lunchBegin[1]), TYPE_LUNCH_BEGIN);
				break;
			case R.id.text_lunch_end:
				String[] lunchEnd = text_lunch_end.getText().toString().split(":");
				showTimePickDialog(Integer.parseInt(lunchEnd[0]), Integer.parseInt(lunchEnd[1]), TYPE_LUNCH_END);
				break;
			case R.id.text_reminder:
				showItemCheckDialog(TIMES_REMINDER, indexReminder, TYPE_REMINDER);
				break;
			case R.id.text_target:
				showTimePickDialog(targetHour, targetMinute, TYPE_TARGET);
//				showItemCheckDialog(TIMES_TARGET, indexTarget, TYPE_TARGET);
				break;

			default:
				break;
			}
		}
	};

	private OnCheckedChangeListener myOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

			switch (buttonView.getId()) {
			case R.id.switch_sleep:
				showSleepSettings(isChecked);
				break;
			case R.id.switch_sleep_time:
				showSleepTimeSettings(isChecked);
				break;
			case R.id.switch_lunch:
				showLunchSettings(isChecked);
				break;
			case R.id.switch_reminder:
				showRemindSettings(isChecked);
				break;
			default:
				break;
			}

		}
	};

	/**
	 * 保存数据
	 */
	private void clickSave() {
		// 自动睡眠开关
		SpHelper.putBoolean(KEY_IS_SLEEP_AUTO, switch_sleep.isChecked());
		// 睡眠时间
		SpHelper.putBoolean(KEY_IS_SLEEP_TIME, switch_sleep_time.isChecked());
		String[] timeBegin = text_time_begin.getText().toString().split(":");
		SpHelper.putInt(KEY_TIME_BEGIN_HOUR, Integer.parseInt(timeBegin[0]));
		SpHelper.putInt(KEY_TIME_BEGIN_MINUTE, Integer.parseInt(timeBegin[1]));
		String[] timeEnd = text_time_end.getText().toString().split(":");
		SpHelper.putInt(KEY_TIME_END_HOUR, Integer.parseInt(timeEnd[0]));
		SpHelper.putInt(KEY_TIME_END_MINUTE, Integer.parseInt(timeEnd[1]));
		// 睡眠提醒
		SpHelper.putBoolean(KEY_IS_SLEEP_REMIND, switch_reminder.isChecked());
		SpHelper.putInt(KEY_REMINDER, indexReminder);
		// 午休时间
		SpHelper.putBoolean(KEY_IS_SLEEP_LUNCH, switch_lunch.isChecked());
		String[] luchBegin = text_lunch_begin.getText().toString().split(":");
		SpHelper.putInt(KEY_LUNCH_BEGIN_HOUR, Integer.parseInt(luchBegin[0]));
		SpHelper.putInt(KEY_LUNCH_BEGIN_MINUTE, Integer.parseInt(luchBegin[1]));
		String[] lunchEnd = text_lunch_end.getText().toString().split(":");
		SpHelper.putInt(KEY_LUNCH_END_HOUR, Integer.parseInt(lunchEnd[0]));
		SpHelper.putInt(KEY_LUNCH_END_MINUTE, Integer.parseInt(lunchEnd[1]));
		// 睡眠目标
		SpHelper.putInt(KEY_TARGET_HOUR, targetHour);
		SpHelper.putInt(KEY_TARGET_MINUTE, targetMinute);
		// 向手环发送数据
		if (MyApp.getIntance().mService != null && MyApp.getIntance().mService.mConnectionState == BleService.STATE_CONNECTED) {
			MyApp.getIntance().mService.mCommand = BleService.COMMAND_SLEEP;
			
			MyApp.getIntance().mService.mCommand = BleService.STEP_LENGTH;
			MyApp.getIntance().mService.sendStepLength();
			showDialog(getResources().getString(R.string.please_wait));
//			boolean isSended = MyApp.getIntance().mService.sendAutoSleep();
//			if (isSended) {
//				showDialog(getResources().getString(R.string.please_wait));
//			} else {
//				Toast.makeText(SleepActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
//			}
		} else {
			Toast.makeText(SleepActivity.this, getString(R.string.please_connect), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setTarget() {
		int target = 8;
		String[] timeBeign = text_time_begin.getText().toString().split(":");
		int begin = Integer.parseInt(timeBeign[0]) * 60 + Integer.parseInt(timeBeign[1]);
		
		String[] timeEnd = text_time_end.getText().toString().split(":");
		int end = Integer.parseInt(timeEnd[0]) * 60 + Integer.parseInt(timeEnd[1]);
		Log.i("sleep", "target begin = " + begin + ",   end = " + end);
		
		target = end - begin;
		if (target < 0) {
			target = target + 24 * 60;
		}
		if (target > 24 * 60) {
			target =target - 24 * 60;
		}
		int hour = target / 60;
		int minute = target % 60;
		
		Log.i("sleep", "target hour = " + hour + ",   minute = " + minute);
		String time = "";
		if (hour != 0) {
			time = time + hour + getString(R.string.hours);
		}
		if (minute != 0) {
			time = time + minute + getString(R.string.minutes);
		}
		targetHour = hour;
		targetMinute = minute;
		text_target.setText(time);
	}

	/**
	 * 显示等待对话框
	 * 
	 * @param msg
	 */
	private void showDialog(String msg) {
		if (mMyDialog == null)
			mMyDialog = new MyDialog(this, msg, false);
		mMyDialog.setMsg(msg);

		if (mMyDialog.isShowing() == false)
			mMyDialog.show();
	}

	/**
	 * 显示时间选择对话框
	 * 
	 * @param time
	 * @param minute
	 * @param timeType
	 */
	private void showTimePickDialog(int time, int minute, final int timeType) {
		TimePickerDialog dialog = new TimePickerDialog(this, R.style.dialog_style_light, new OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				String time = Global.df_00.format(hourOfDay) + ":" + Global.df_00.format(minute);
				if (timeType == TYPE_SLEEP_BEIGN) {
					text_time_begin.setText(time);
					setTarget();
				} else if (timeType == TYPE_SLEEP_END) {
					text_time_end.setText(time);
					setTarget();
				} else if (timeType == TYPE_LUNCH_BEGIN) {
					text_lunch_begin.setText(time);
				} else if (timeType == TYPE_LUNCH_END) {
					text_lunch_end.setText(time);
				} else if (timeType == TYPE_TARGET) {
					 time = "";
					if (hourOfDay != 0) {
						time = time + hourOfDay + getString(R.string.hours);
					}
					if (minute != 0) {
						time = time + minute + getString(R.string.minutes);
					}
					targetHour = hourOfDay;
					targetMinute = minute;
					text_target.setText(time);				}
			}
		}, time, minute, unit == 0 ? true : false);

		dialog.show();
	}

	/**
	 * 显示条目选择对话框
	 * 
	 * @param items
	 * @param checkItem
	 * @param type
	 */
	private void showItemCheckDialog(CharSequence[] items, int checkItem, final int type) {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style_light);
		builder.setSingleChoiceItems(items, checkItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (type == TYPE_REMINDER) {
					indexReminder = which;
					text_reminder.setText(TIMES_REMINDER[which] + getString(R.string.minutes));
				} else if (type == TYPE_TARGET) {
					indexTarget = which;
					text_target.setText(TIMES_TARGET[which] + getString(R.string.hours));
				}

				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
	}

	/**
	 * 是否显示睡眠设置
	 * 
	 * @param isShow
	 */
	private void showSleepSettings(boolean isShow) {
		if (isShow) {
			layout_sleep_settings.setVisibility(View.VISIBLE);
		} else {
			layout_sleep_settings.setVisibility(View.GONE);
		}
	}
	
	private void showSleepTimeSettings(boolean isShow) {
		if (isShow) {
			text_time_begin.setTextColor(getResources().getColor(R.color.black));
			text_time_end.setTextColor(getResources().getColor(R.color.black));
			text_target.setTextColor(getResources().getColor(R.color.text_sleep_disable));
			setTarget();
		} else {
			text_time_begin.setTextColor(getResources().getColor(R.color.text_sleep_disable));
			text_time_end.setTextColor(getResources().getColor(R.color.text_sleep_disable));
			text_target.setTextColor(getResources().getColor(R.color.black));
			switch_reminder.setChecked(false);
		}
		text_time_begin.setEnabled(isShow);
		text_time_end.setEnabled(isShow);
		switch_reminder.setEnabled(isShow);
		text_target.setEnabled(!isShow);
	}

	private void showLunchSettings(boolean isShow) {
		if (isShow) {
			text_lunch_begin.setTextColor(getResources().getColor(R.color.black));
			text_lunch_end.setTextColor(getResources().getColor(R.color.black));
		} else {
			text_lunch_begin.setTextColor(getResources().getColor(R.color.text_sleep_disable));
			text_lunch_end.setTextColor(getResources().getColor(R.color.text_sleep_disable));
		}
		text_lunch_begin.setEnabled(isShow);
		text_lunch_end.setEnabled(isShow);
	}
	
	private void showRemindSettings(boolean isShow) {
		if (isShow) {
			text_reminder.setTextColor(getResources().getColor(R.color.black));
		} else {
			text_reminder.setTextColor(getResources().getColor(R.color.text_sleep_disable));
		}
		text_reminder.setEnabled(isShow);
	}
	/**
	 * 初始化数据
	 */
	private void initValue() {
		// 自动睡眠开关
		boolean isAutoSleep = SpHelper.getBoolean(KEY_IS_SLEEP_AUTO, false);
		showSleepSettings(isAutoSleep);
		switch_sleep.setChecked(isAutoSleep);
		// 睡眠时间
		boolean isSleepTime = SpHelper.getBoolean(KEY_IS_SLEEP_TIME, false);
		showSleepTimeSettings(isSleepTime);
		switch_sleep_time.setChecked(isSleepTime);
		text_time_begin.setText(Global.df_00.format(SpHelper.getInt(KEY_TIME_BEGIN_HOUR, 22)) + ":" + Global.df_00.format(SpHelper.getInt(KEY_TIME_BEGIN_MINUTE, 0)));
		text_time_end.setText(Global.df_00.format(SpHelper.getInt(KEY_TIME_END_HOUR, 6)) + ":" + Global.df_00.format(SpHelper.getInt(KEY_TIME_END_MINUTE, 0)));
		// 睡眠提醒
		for (int i = 0; i < 60; i ++) {
			TIMES_REMINDER[i] = String.valueOf(i);
		}
		boolean isRemind = SpHelper.getBoolean(KEY_IS_SLEEP_REMIND, false);
		showRemindSettings(isRemind);
		switch_reminder.setChecked(isRemind);
		indexReminder = SpHelper.getInt(KEY_REMINDER, 15);
		text_reminder.setText(TIMES_REMINDER[indexReminder] + getString(R.string.minutes));
		// 午休时间
		boolean isLunch = SpHelper.getBoolean(KEY_IS_SLEEP_LUNCH, false);
		showLunchSettings(isLunch);
		switch_lunch.setChecked(isLunch);
		text_lunch_begin.setText(Global.df_00.format(SpHelper.getInt(KEY_LUNCH_BEGIN_HOUR, 13)) + ":" + Global.df_00.format(SpHelper.getInt(KEY_LUNCH_BEGIN_MINUTE, 0)));
		text_lunch_end.setText(Global.df_00.format(SpHelper.getInt(KEY_LUNCH_END_HOUR, 14)) + ":" + Global.df_00.format(SpHelper.getInt(KEY_LUNCH_END_MINUTE, 0)));
		// 睡眠目标
		setTarget();
//		text_target.setText(TIMES_TARGET[SpHelper.getInt(KEY_TARGET, 3)] + getString(R.string.hours));

		// 判断手机制式
		ContentResolver cv = this.getContentResolver(); // 获取当前系统设置
		String strTimeFormat = android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24);
		if ("24".equals(strTimeFormat))
			unit = 0;
		else
			unit = 1;
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		TextView re_back = (TextView) findViewById(R.id.return_back);
		re_back.setOnClickListener(myOnClickListener);
		TextView text_save = (TextView) findViewById(R.id.text_save);
		text_save.setOnClickListener(myOnClickListener);

		text_time_begin = (TextView) findViewById(R.id.text_sleep_time_begin);
		text_time_begin.setOnClickListener(myOnClickListener);
		text_time_end = (TextView) findViewById(R.id.text_sleep_time_end);
		text_time_end.setOnClickListener(myOnClickListener);
		text_reminder = (TextView) findViewById(R.id.text_reminder);
		text_reminder.setOnClickListener(myOnClickListener);
		text_lunch_begin = (TextView) findViewById(R.id.text_lunch_begin);
		text_lunch_begin.setOnClickListener(myOnClickListener);
		text_lunch_end = (TextView) findViewById(R.id.text_lunch_end);
		text_lunch_end.setOnClickListener(myOnClickListener);
		text_target = (TextView) findViewById(R.id.text_target);
		text_target.setOnClickListener(myOnClickListener);

		switch_sleep = (CheckBox) findViewById(R.id.switch_sleep);
		switch_sleep.setOnCheckedChangeListener(myOnCheckedChangeListener);
		switch_sleep_time = (CheckBox) findViewById(R.id.switch_sleep_time);
		switch_sleep_time.setOnCheckedChangeListener(myOnCheckedChangeListener);
		switch_reminder = (CheckBox) findViewById(R.id.switch_reminder);
		switch_reminder.setOnCheckedChangeListener(myOnCheckedChangeListener);
		switch_lunch = (CheckBox) findViewById(R.id.switch_lunch);
		switch_lunch.setOnCheckedChangeListener(myOnCheckedChangeListener);

		layout_sleep_settings = (LinearLayout) findViewById(R.id.layout_sleep_settings);

	}

	private TextView text_time_begin, text_time_end, text_reminder, text_lunch_begin, text_lunch_end, text_target;
	private CheckBox switch_sleep, switch_sleep_time, switch_reminder, switch_lunch;
	private LinearLayout layout_sleep_settings;
}