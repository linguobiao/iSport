package com.cn.zhihengchuang.walkbank.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.isport.trackernew.R;
import com.lingb.global.Global;
import com.lingb.helper.SpHelper;
import com.lingb.helper.StringHelper;
import com.lingb.ride.RideMainActivity;
import com.lingb.ride.service.RideBLEService;
import com.lingb.ride.settings.RideUserActivity;
import com.lingb.splash.FirstDeviceActivity;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DialogHelper;
import com.cn.zhihengchuang.walkbank.util.TimerHelper;
import com.cn.zhihengchuang.walkbank.util.ToastUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class BindDeviceActivity extends Activity {
	private RelativeLayout re_back;
	private TextView title;
	private Button bind_device, delete_device;
	private ToastUtil toast;
	private int position;
	private ProgressDialog dialog_bounding;
	private String mac;
	private String name;
	private Timer timer_bound_timeout;
	private String mac_bounded;
	private SharedPreferences share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_device_bind);
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
		init();
		initBroadcastReceiver();
		setDefaultData();
		initService();
		mac_bounded = MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue();
	}

	@Override
	protected void onResume() {
		sendBroadcast(new Intent(Global.ACTION_BOUND_STOP));
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(myBLEBroadcastReceiver);
		TimerHelper.cancelTimer(timer_bound_timeout);
		com.lingb.helper.DialogHelper.dismissDialog(dialog_bounding);
		super.onDestroy();
	}

	private void setDefaultData() {
		position = getIntent().getIntExtra("position", 0);
		name = getIntent().getStringExtra("title");
		title.setText(StringHelper.replaceDeviceNameToCC431(name));
		mac = getIntent().getStringExtra("mac");
	}
	
	private void initService() {
				startService(new Intent(BindDeviceActivity.this, RideBLEService.class));
	}

	private void init() {
		toast = new ToastUtil(this);
		bind_device = (Button) findViewById(R.id.bind_device_bind);
		delete_device = (Button) findViewById(R.id.bind_device_delete);
		re_back = (RelativeLayout) findViewById(R.id.return_back);
		title = (TextView) findViewById(R.id.title_name);
		re_back.setOnClickListener(new OnClickListenerImpl());
		bind_device.setOnClickListener(new OnClickListenerImpl());
		delete_device.setOnClickListener(new OnClickListenerImpl());
	}

	private class OnClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.return_back:
				if (mac_bounded != null) {
					MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue(mac_bounded);
				}
				BindDeviceActivity.this.finish();
				break;
			case R.id.bind_device_bind:
//				BindDeviceActivity.this.setResult(100);
//				toast.getToast(getResources().getString(R.string.binding_is_successful));
//				BindDeviceActivity.this.finish();
				boundDevice();
				break;
			case R.id.bind_device_delete:
				if (mac_bounded != null) {
					MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue(mac_bounded);
				}
				setResult(101);
				toast.getToast(getResources().getString(R.string.Successfully_deleted));
				BindDeviceActivity.this.finish();
				break;
			}
		}
	}
	
	private void boundDevice() {
		
		if (mac != null) {
			share.edit().putBoolean(Global.KEY_IS_NEW_BOUND, true).commit();
			MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue(mac);
			sendBroadcast(new Intent(Global.ACTION_BOUND_MAC_CHANGED));
			showBoundingDialog();
			TimerBound();
			name = StringHelper.formatDeviceName(name);
			if (MyApp.getIntance().list_device_ride.contains(name)) {
				SpHelper.putString(Global.KEY_BOUNDED_DEVICE, mac);
				SpHelper.putString(Global.KEY_BOUNDED_NAME, name);
				MyApp.getIntance().mService.disconnect();
				MyApp.getIntance().mRideService.connect(mac, false);
				
			} else {
				MyApp.getIntance().mService.connect(mac);
				
			}
		}
	}
	
	private void TimerBound() {
		TimerHelper.cancelTimer(timer_bound_timeout);
		timer_bound_timeout = new Timer();
		timer_bound_timeout.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				if (myHandler != null) {
					myHandler.sendEmptyMessage(HANDLER_BOUND_TIMEOUT);
				}
				
			}
		}, 3 * 3000);
	}
	
	/**
	 * 显示正在同步对话框
	 */
	private void showBoundingDialog() {
		if (dialog_bounding == null) {
			dialog_bounding = DialogHelper.showProgressDialog(BindDeviceActivity.this, getString(R.string.light_screen));
			dialog_bounding.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
						TimerHelper.cancelTimer(timer_bound_timeout);
						sendBroadcast(new Intent(Global.ACTION_BOUND_STOP));
						
					}
					return false;
				}
			});
			dialog_bounding.setCanceledOnTouchOutside(false);
			dialog_bounding.show();
		} else {
			dialog_bounding.show();
		}
	}
	
	private final int HANDLER_BOUND_TIMEOUT = 111;
	
	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_BOUND_TIMEOUT:
//				share.edit().putBoolean(Global.KEY_IS_NEW_BOUND, false).commit();
				Toast.makeText(BindDeviceActivity.this, getString(R.string.binding_is_fail), Toast.LENGTH_SHORT).show();
				DialogHelper.hideDialog(dialog_bounding);
				sendBroadcast(new Intent(Global.ACTION_BOUND_STOP));
				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * my BLE BroadcastReceiver
	 */
	private BroadcastReceiver myBLEBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BleService.ACTION_GATT_CONNECTED)  || action.equals(RideBLEService.ACTION_GATT_CONNECTED)) {
				
				String nameRide = MyApp.getIntance().mRideService.getDeviceName();
				String nameBand = MyApp.getIntance().mService.getDeviceName();
				if (nameRide != null) {
					nameRide = StringHelper.formatDeviceName(nameRide);
				}
				if (nameBand != null) {
					if (nameBand.contains("_")) {
						nameBand = nameBand.split("_")[0];
					}

				}
				
				if (nameRide != null && MyApp.getIntance().list_device_ride.contains(nameRide)) {
					
					BindDeviceActivity.this.startActivity(new Intent(BindDeviceActivity.this, RideMainActivity.class));
					sendBroadcast(new Intent(Global.ACTION_FINISH_BAND));
					MyApp.getIntance().mService.scan(false, null);
					BindDeviceActivity.this.stopService(new Intent(BindDeviceActivity.this, BleService.class));
				} else if (nameBand != null && MyApp.getIntance().list_device_band.contains(nameBand)){
					
					BindDeviceActivity.this.stopService(new Intent(BindDeviceActivity.this, RideBLEService.class));
					BindDeviceActivity.this.setResult(100);
					
				}
				
				toast.getToast(getResources().getString(R.string.binding_is_successful));
				
				TimerHelper.cancelTimer(timer_bound_timeout);
				DialogHelper.dismissDialog(dialog_bounding);
				BindDeviceActivity.this.finish();
			}
		}
	};

	/**
	 * 初始化 BroadcastReceiver
	 */
	private void initBroadcastReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(RideBLEService.ACTION_GATT_CONNECTED);
		BindDeviceActivity.this.registerReceiver(myBLEBroadcastReceiver, intentFilter);

	}
}