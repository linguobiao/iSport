package com.cn.zhihengchuang.walkbank.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isport.trackernew.R;
import com.lingb.global.Global;
import com.lingb.helper.SpHelper;
import com.lingb.ride.RideMainActivity;
import com.lingb.ride.RideSettingsActivity;
import com.lingb.ride.service.RideBLEService;
import com.lingb.ride.settings.RideDeviceActivity;

public class SelectActivity extends Activity {
	private TextView re_back, text_version;

	@TargetApi(19)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		initUI();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			int defDevice = SpHelper.getInt(Global.KEY_DEVICE, Global.TYPE_DEVICE_NULL);
			if (defDevice == Global.TYPE_DEVICE_BAND) {
				startActivity(new Intent(SelectActivity.this, MainActivityGroup.class));
			} else if (defDevice == Global.TYPE_DEVICE_RIDE){
				startActivity(new Intent(SelectActivity.this, RideMainActivity.class));
			} else {
				stopService(new Intent(SelectActivity.this, RideBLEService.class));
//				finish();
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	
private OnClickListener myOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.text_bind:
				startActivity(new Intent(SelectActivity.this, MainActivityGroup.class));
				SpHelper.putInt(Global.KEY_DEVICE, Global.TYPE_DEVICE_BAND);
				finish();
				break;
			case R.id.text_ride:
				boolean isNewUp = SpHelper.getBoolean(Global.KEY_IS_NEW_UP_RIDE, true);
				if (isNewUp) {
					Intent intent = null;
					intent = new Intent(SelectActivity.this, RideDeviceActivity.class);
					startActivity(intent);
				} else {
					SpHelper.putInt(Global.KEY_DEVICE, Global.TYPE_DEVICE_RIDE);
					Intent intent = null;
					intent = new Intent(SelectActivity.this, RideMainActivity.class);
					startActivity(intent);
				}
				finish();
				break;

			default:
				break;
			}
			
		}
	};
	
	private void initUI() {
		TextView text_bind = (TextView) findViewById(R.id.text_bind);
		text_bind.setOnClickListener(myOnClickListener);
		TextView text_ride = (TextView) findViewById(R.id.text_ride);
		text_ride.setOnClickListener(myOnClickListener);
		
	}

}