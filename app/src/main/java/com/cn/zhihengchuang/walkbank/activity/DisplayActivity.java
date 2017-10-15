package com.cn.zhihengchuang.walkbank.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.TextView;

import com.cn.zhihengchuang.walkbank.ble.BleController;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.util.UiTools;
import com.cn.zhihengchuang.walkbank.view.MyDialog;
import com.isport.trackernew.R;
import com.lingb.helper.SpHelper;

public class DisplayActivity extends Activity {

	public final static String KEY_SHOW_CALORIES = "KEY_SHOW_CALORIES";
	public final static String KEY_SHOW_DISTANCE = "KEY_SHOW_DISTANCE";
	public final static String KEY_SHOW_TIME = "KEY_SHOW_TIME";
	public final static String KEY_SHOW_PERCENT = "KEY_SHOW_PERCENT";
	public final static String KEY_SHOW_FACE = "KEY_SHOW_FACE";
	public final static String KEY_SHOW_ALARM = "KEY_SHOW_ALARM";
	public final static String KEY_SHOW_SMS = "KEY_SHOW_SMS";
	public final static String KEY_SHOW_CALL = "KEY_SHOW_CALL";

	private MyDialog mMyDialog;
	private MyBroadCastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_display);

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
			if (BleController.ACTION_DISPLAY_OK.equals(action)) {
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
			filter.addAction(BleController.ACTION_DISPLAY_OK);
			filter.addAction(BleController.ACTION_FAIL);
			registerReceiver(mReceiver, filter);
		} else {
			if (mReceiver != null) {
				unregisterReceiver(mReceiver);
			}
		}
	}

	private class OnClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.return_back:
				DisplayActivity.this.finish();
				break;
			case R.id.text_save:
				clickSave();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 保存数据
	 */
	private void clickSave() {
		SpHelper.putBoolean(KEY_SHOW_CALORIES, button_calories.isChecked());
		SpHelper.putBoolean(KEY_SHOW_DISTANCE, button_distance.isChecked());
		SpHelper.putBoolean(KEY_SHOW_TIME, button_time.isChecked());
		SpHelper.putBoolean(KEY_SHOW_PERCENT, button_percent.isChecked());
		SpHelper.putBoolean(KEY_SHOW_FACE, button_face.isChecked());
		SpHelper.putBoolean(KEY_SHOW_ALARM, button_alarm.isChecked());
		SpHelper.putBoolean(KEY_SHOW_SMS, button_sms.isChecked());
		SpHelper.putBoolean(KEY_SHOW_CALL, button_call.isChecked());
		// 向手环发送数据
		if (MyApp.getIntance().mService != null && MyApp.getIntance().mService.mConnectionState == BleService.STATE_CONNECTED) {
			MyApp.getIntance().mService.mCommand = BleService.COMMAND_DISPLAY;
			boolean isSended = MyApp.getIntance().mService.sendDeviceDisplay();
			if (isSended) {
				showDialog(getResources().getString(R.string.please_wait));
			} else {
				Toast.makeText(DisplayActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(DisplayActivity.this, getString(R.string.please_connect), Toast.LENGTH_SHORT).show();
		}
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
	 * 初始化数据
	 */
	private void initValue() {
		button_calories.setChecked(SpHelper.getBoolean(KEY_SHOW_CALORIES, true));
		button_distance.setChecked(SpHelper.getBoolean(KEY_SHOW_DISTANCE, true));
		button_time.setChecked(SpHelper.getBoolean(KEY_SHOW_TIME, true));
		button_percent.setChecked(SpHelper.getBoolean(KEY_SHOW_PERCENT, true));
		button_face.setChecked(SpHelper.getBoolean(KEY_SHOW_FACE, true));
		button_alarm.setChecked(SpHelper.getBoolean(KEY_SHOW_ALARM, true));
		button_sms.setChecked(SpHelper.getBoolean(KEY_SHOW_SMS, true));
		button_call.setChecked(SpHelper.getBoolean(KEY_SHOW_CALL, true));
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		TextView re_back = (TextView) findViewById(R.id.return_back);
		re_back.setOnClickListener(new OnClickListenerImpl());
		TextView text_save = (TextView) findViewById(R.id.text_save);
		text_save.setOnClickListener(new OnClickListenerImpl());

		button_calories = (ToggleButton) findViewById(R.id.button_calories);
		button_distance = (ToggleButton) findViewById(R.id.button_distance);
		button_time = (ToggleButton) findViewById(R.id.button_time);
		button_percent = (ToggleButton) findViewById(R.id.button_percent);
		button_face = (ToggleButton) findViewById(R.id.button_face);
		button_alarm = (ToggleButton) findViewById(R.id.button_alarm);
		button_sms = (ToggleButton) findViewById(R.id.button_sms);
		button_call = (ToggleButton) findViewById(R.id.button_call);

	}

	private ToggleButton button_calories, button_distance, button_time, button_percent, button_face, button_alarm, button_sms, button_call;

}