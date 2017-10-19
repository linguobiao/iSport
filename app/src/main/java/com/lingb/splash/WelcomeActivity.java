package com.lingb.splash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.cn.zhihengchuang.walkbank.activity.MainActivityGroup;
import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.isport.trackernew.R;
import com.lingb.global.BaseActivity;
import com.lingb.global.Global;
import com.lingb.helper.SpHelper;
import com.lingb.ride.RideMainActivity;
import com.lingb.ride.service.RideBLEService;

public class WelcomeActivity extends BaseActivity {
	private Animation myAnimation_Alpha;
	private View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = getLayoutInflater().inflate(R.layout.activity_welcome, null);
		setContentView(view);
		initUI();
		setAnimation();
		
		if (Global.PACKAGE_NAME.startsWith(Global.PACKAGE_NAME_ISPORT)) {
			
			int type = SpHelper.getInt(Global.KEY_DEVICE, Global.TYPE_DEVICE_NULL);
			if (type == Global.TYPE_DEVICE_BAND) {
				startService(new Intent(this, BleService.class));
				Log.i("welcome", "start bleservice");
			} else if (type == Global.TYPE_DEVICE_RIDE){
				startService(new Intent(this, RideBLEService.class));
			} else {
				startService(new Intent(this, RideBLEService.class));
				startService(new Intent(this, BleService.class));
			}
		} else {
			startService(new Intent(this, BleService.class));
			
		}
	}

	private void initUI() {
		ImageView bg_welcome = (ImageView) findViewById(R.id.bg_welcome);
		ImageView image_welcome = (ImageView) findViewById(R.id.image_welcome);
		if (Global.PACKAGE_NAME.startsWith(Global.PACKAGE_NAME_ISPORT)) {
			bg_welcome.setImageResource(R.drawable.bg_welcome_isport);
			image_welcome.setVisibility(View.INVISIBLE);
		} else if (Global.PACKAGE_NAME.startsWith(Global.PACKAGE_NAME_CRAIG)) {
			bg_welcome.setImageResource(R.drawable.bg_welcome_craig);
			image_welcome.setVisibility(View.INVISIBLE);
		} else if (Global.PACKAGE_NAME.startsWith(Global.PACKAGE_NAME_GO)) {
			bg_welcome.setImageResource(R.drawable.bg_welcome_go_activity);
			image_welcome.setVisibility(View.INVISIBLE);
		} else if (Global.PACKAGE_NAME.startsWith(Global.PACKAGE_NAME_READY)) {
			bg_welcome.setImageResource(R.drawable.bg_welcome_ready);
			image_welcome.setVisibility(View.INVISIBLE);
		} else {
			bg_welcome.setVisibility(View.INVISIBLE);
			image_welcome.setVisibility(View.INVISIBLE);
		}

	}

	private void setAnimation() {
		myAnimation_Alpha = new AlphaAnimation(0.1f, 1.0f);
		myAnimation_Alpha.setDuration(1500);
		myAnimation_Alpha.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				int type = SpHelper.getInt(Global.KEY_DEVICE, Global.TYPE_DEVICE_NULL);
//				type = Global.TYPE_DEVICE_NULL;
				if (type == Global.TYPE_DEVICE_BAND) {
					Intent intent = null;
					intent = new Intent(WelcomeActivity.this, MainActivityGroup.class);
					startActivity(intent);
				} else if (type == Global.TYPE_DEVICE_RIDE) {
//					boolean isNewUp = SpHelper.getBoolean(Global.KEY_IS_NEW_UP_RIDE, true);
//					if (isNewUp) {
//						Intent intent = null;
//						intent = new Intent(WelcomeActivity.this, RideDeviceActivity.class);
//						startActivity(intent);
//					} else {
						Intent intent = null;
						intent = new Intent(WelcomeActivity.this, RideMainActivity.class);
						startActivity(intent);
//					}
				} else {
					SpHelper.putString(Global.KEY_BOUNDED_DEVICE, null);
					SpHelper.putString(Global.KEY_BOUNDED_NAME, null);
					MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue(null);
					Intent intent = null;
					intent = new Intent(WelcomeActivity.this, FirstDeviceActivity.class);
					startActivity(intent);
				}
				WelcomeActivity.this.finish();
			}
		});
		view.startAnimation(myAnimation_Alpha);
	}
}