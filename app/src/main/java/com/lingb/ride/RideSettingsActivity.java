package com.lingb.ride;

import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.isport.trackernew.R;
import com.lingb.global.Global;
import com.lingb.helper.SpHelper;
import com.lingb.helper.StringHelper;
import com.lingb.ride.service.RideBLEService;
import com.lingb.ride.settings.RideDeviceActivity;
import com.lingb.ride.settings.RideUserActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RideSettingsActivity extends Activity {

	public static final int REQUEST_CODE_DEVICE = 111;
	private final String KEY_BATTERY_RIDE = "KEY_BATTERY_RIDE";

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_setting);
		initUI();
		initReceiver();
		initVersion();
	}

	@Override
	protected void onResume() {
		String boundAddress = SpHelper.getString(Global.KEY_BOUNDED_DEVICE, null);
		if (boundAddress != null) {
			String boundName = SpHelper.getString(Global.KEY_BOUNDED_NAME, null);
			if (boundName != null) {
				boundName = StringHelper.formatDeviceName(boundName);
			}
			text_name.setText(StringHelper.getRideDeviceName(boundName));
			text_connect.setVisibility(View.VISIBLE);
			text_battery.setVisibility(View.VISIBLE);
			image_battery.setVisibility(View.VISIBLE);
			layout_battery.setVisibility(View.VISIBLE);
		} else {
			text_name.setText(getString(R.string.ride_Add_a_new_device));
			text_connect.setVisibility(View.GONE);
			text_battery.setVisibility(View.GONE);
			image_battery.setVisibility(View.GONE);
			layout_battery.setVisibility(View.GONE);
		}
		if (MyApp.getIntance().mRideService != null && MyApp.getIntance().mRideService.getConnectionState() == RideBLEService.STATE_CONNECTED) {
			text_connect.setText(getString(R.string.ride_Connected));
			text_connect.setTextColor(getResources().getColor(R.color.ride_main_green));
			MyApp.getIntance().mRideService.get_battery();
			int battery = SpHelper.getInt(KEY_BATTERY_RIDE, 0);
			text_battery.setText(battery + "%");
			int widthTemp = width * battery / 100;
			layout_battery.setVisibility(View.VISIBLE);
			image_battery.setVisibility(View.VISIBLE);
			LayoutParams params = image_battery.getLayoutParams();
			params.width = widthTemp;
			image_battery.setLayoutParams(params);
		} else {
			text_connect.setText(getString(R.string.ride_Not_connected));
			text_connect.setTextColor(getResources().getColor(R.color.ride_black));
			text_battery.setText("0%");
			image_battery.setVisibility(View.GONE);
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(myBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_DEVICE) {
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.text_back:
				finish();
				break;
			case R.id.layout_user:
				startActivity(new Intent(RideSettingsActivity.this, RideUserActivity.class));
				break;
			case R.id.layout_device:
				startActivityForResult(new Intent(RideSettingsActivity.this, RideDeviceActivity.class), REQUEST_CODE_DEVICE);
				break;

			default:
				break;
			}

		}
	};

	/**
	 * myBroadcastReceiver
	 */
	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(RideBLEService.ACTION_RECEIVE_BATTERY)) {
				byte[] value = intent.getByteArrayExtra(RideBLEService.KEY_RECEIVE_DATA);
				if (value != null) {
					text_battery.setText(value[0] + "%");
					int widthTemp = width * value[0] / 100;
					layout_battery.setVisibility(View.VISIBLE);
					image_battery.setVisibility(View.VISIBLE);
					LayoutParams params = image_battery.getLayoutParams();
					params.width = widthTemp;
					image_battery.setLayoutParams(params);
					SpHelper.putInt(KEY_BATTERY_RIDE, value[0]);
				}
				
				
				
			} else if (action.equals(RideBLEService.ACTION_GATT_CONNECTED)) {
				text_connect.setText(getString(R.string.ride_Connected));
				text_connect.setTextColor(getResources().getColor(R.color.ride_main_green));
				MyApp.getIntance().mRideService.get_battery();

			} else if (action.equals(RideBLEService.ACTION_GATT_DISCONNECTED)) {
				text_connect.setText(getString(R.string.ride_Not_connected));
				text_connect.setTextColor(getResources().getColor(R.color.ride_black));
				text_battery.setText("");
				image_battery.setVisibility(View.GONE);
			} else if (action.equals(Global.ACTION_FINISH_RIDE)) {
				RideSettingsActivity.this.finish();
			}

		}
	};

	/**
	 * 初始化广播接收器
	 */
	private void initReceiver() {
		IntentFilter f = new IntentFilter();
		f.addAction(RideBLEService.ACTION_RECEIVE_BATTERY);
		f.addAction(RideBLEService.ACTION_GATT_CONNECTED);
		f.addAction(RideBLEService.ACTION_GATT_DISCONNECTED);
		f.addAction(Global.ACTION_FINISH_RIDE);
		registerReceiver(myBroadcastReceiver, f);
	}
	
	private void initVersion() {
		PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			text_version.setText(pi.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void initUI() {
		TextView text_back = (TextView) findViewById(R.id.text_back);
		text_back.setOnClickListener(myOnClickListener);
		text_name = (TextView) findViewById(R.id.text_name);
		text_connect = (TextView) findViewById(R.id.text_connect);
		text_battery = (TextView) findViewById(R.id.text_battery);
		text_version = (TextView) findViewById(R.id.text_version);

		RelativeLayout layout_user = (RelativeLayout) findViewById(R.id.layout_user);
		layout_user.setOnClickListener(myOnClickListener);
		RelativeLayout layout_device = (RelativeLayout) findViewById(R.id.layout_device);
		layout_device.setOnClickListener(myOnClickListener);
		
		image_battery = (ImageView) findViewById(R.id.image_battery);
		width = image_battery.getLayoutParams().width;
		height  = image_battery.getLayoutParams().height;
		layout_battery = (RelativeLayout) findViewById(R.id.layout_battery);
		}
	
	private int width, height;

	private TextView text_connect, text_battery, text_name;
	private TextView text_version;
	private ImageView image_battery;
	private RelativeLayout layout_battery;
	

}
