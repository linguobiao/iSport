package com.cn.zhihengchuang.walkbank.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.adapter.DeviceAdapter;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.entity.BltModel;
import com.cn.zhihengchuang.walkbank.entity.DeviceEntity;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.cn.zhihengchuang.walkbank.util.TimerHelper;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lingb.global.Global;
import com.lingb.helper.StringHelper;

/**
 * 
 * @author longke 扫描设备
 * 
 */

public class ManageDeviceActivity extends Activity {
	private TextView text_no_device;
	private ListView list;
	private TextView fresh;
	private ProgressBar fresh_bar;
	private RelativeLayout re_back;
	private Handler mHandler;
	private ArrayList<DeviceEntity> lists;
	private boolean is_start;
	private DeviceAdapter adapter;
	private SharedPreferences share = null;
	private Editor edit;
	private int position;
	private IntentFilter myIntentFilter;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_device_scan);

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					setLinearLayout();
					break;
				}
			}
		};
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE);
		edit = share.edit();
		init();
		 scanLeDevice();
		registerBoradcastReceiver();
	}


	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(mReceiver, myIntentFilter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		TimerHelper.cancelTimer(timer_scan_timeout);
		unregisterReceiver(mReceiver);
		if (MyApp.getIntance().mService != null) {
			MyApp.getIntance().mService.scan(false, null);
		}
		// unbindService(mServiceConnection);
		lists.clear();
	};

	private void init() {
		text_no_device = (TextView) findViewById(R.id.text_no_device);
		lists = new ArrayList<DeviceEntity>();
		initBoundDevice();
		adapter = new DeviceAdapter(lists, this);
		list = (ListView) findViewById(R.id.manage_device_list);
		fresh = (TextView) findViewById(R.id.manage_device_fresh);
		fresh_bar = (ProgressBar) findViewById(R.id.manage_device_fresh_bar);
		re_back = (RelativeLayout) findViewById(R.id.return_back);
		re_back.setOnClickListener(new OnClickListenerImpl());
		fresh.setOnClickListener(new OnClickListenerImpl());
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ManageDeviceActivity.this.position = position;
				Intent intent = null;
				if (position == lists.size() - 1 && lists.get(position).getMac() == null) {
					intent = new Intent(ManageDeviceActivity.this, OtherDeviceActivity.class);
					intent.putExtra("title", lists.get(position).getName());
				} else if (lists.get(position).isIs_device()) {
					if (lists.get(position).isIs_bind()) {
						intent = new Intent(ManageDeviceActivity.this, UnbindDeviceActivity.class);
						intent.putExtra("title", lists.get(position).getName());
						intent.putExtra("mac", lists.get(position).getMac());
						intent.putExtra("position", position);
					} else {
						intent = new Intent(ManageDeviceActivity.this, BindDeviceActivity.class);
						// mBleService.connect(lists.get(position).getMac());
						intent.putExtra("title", lists.get(position).getName());
						intent.putExtra("position", position);
						intent.putExtra("mac", lists.get(position).getMac());
					}
				}
				if (intent != null) {
					startActivityForResult(intent, 10);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Global.REQUEST_ENABLE_BLUETOOTH) {
			if (resultCode == Activity.RESULT_OK) {
//				updateBluetoothScan();
			} else  {
				is_start = false;
				fresh_bar.setVisibility(View.GONE);
				list.setVisibility(View.GONE);
				text_no_device.setVisibility(View.VISIBLE);
				if (MyApp.getIntance().mService != null) {
					MyApp.getIntance().mService.scan(false, null);

				}
				TimerHelper.cancelTimer(timer_scan_timeout);
			}
		}
		if (resultCode == 101) {
			lists.remove(position);
			setLinearLayout();
		} else if (resultCode == 100) {

			for (int i = 0; i < lists.size(); i++) {
				DeviceEntity entity = lists.get(i);

				if (i == position) {
					entity.setIs_bind(true);
				} 
				else {
					entity.setIs_bind(false);
				}
			}

			DeviceEntity entity = lists.get(position);
			entity.setIs_bind(true);
			setLinearLayout();

			MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue(entity.getMac());
			// sendBroadcast(new Intent(Global.ACTION_BOUND_MAC_CHANGED));
			finish();
		} else if (resultCode == 102) {
			DeviceEntity entity = lists.get(position);
			entity.setIs_bind(false);
			if (entity.getMac().equals(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString())) {
				MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue("");
			}
			setLinearLayout();
			edit.remove("device_mac").commit();
			DbUtils db = DbUtils.create(getApplicationContext());
			WhereBuilder builder = WhereBuilder.b("bltid", "==", entity.getMac());
			try {
				db.delete(BltModel.class, builder);

			} catch (DbException e) {
				e.printStackTrace();
			} finally {
				MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue("");
				sendBroadcast(new Intent(Constants.CONNECTING_DEVICE));
				MyApp.getIntance().mService.disconnect();
			}

		} else if (resultCode == 103) {
			finish();
		}
	}

	protected void setLinearLayout() {
		list.setVisibility(View.VISIBLE);
		fresh_bar.setVisibility(View.GONE);
		if (adapter == null) {
			adapter = new DeviceAdapter(lists, this);
			list.setAdapter(adapter);
		} else {
			adapter.notifymDataSetChanged(lists);
		}
	}

	private class OnClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.return_back:
				sendBroadcast(new Intent(Constants.CONNECTED_SETS));// 通知设置时区等
				ManageDeviceActivity.this.finish();
				break;
			case R.id.manage_device_fresh:

				if (!is_start) {
					scanLeDevice();
				}

				break;
			}
		}
	}

	private void scanLeDevice() {
		is_start = true;
		fresh_bar.setVisibility(View.VISIBLE);
		list.setVisibility(View.GONE);
		text_no_device.setVisibility(View.GONE);
		lists.clear();
		

		BluetoothAdapter mBtAdapter = initBluetooth_manual(ManageDeviceActivity.this);
		if (mBtAdapter != null) {
			if (mBtAdapter.isEnabled()) {
				if (MyApp.getIntance().mService != null) {
					MyApp.getIntance().mService.scan(true, null);
					timerScanDevice();
					initBoundDevice();
				}
			}
		}
		
	}

	/**
	 * 初始化蓝牙
	 * 
	 * @param context
	 */
	public static BluetoothAdapter initBluetooth_manual(Activity context) {
		BluetoothManager mBluetoothManager = null;

		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

			// 没有蓝牙模块
			if (mBluetoothManager == null) {
				return null;
			}
		}
		BluetoothAdapter mBtAdapter = mBluetoothManager.getAdapter();

		// 判断机器是否有蓝牙
		if (!mBtAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			context.startActivityForResult(enableIntent, Global.REQUEST_ENABLE_BLUETOOTH);
		}
		return mBtAdapter;
	}

	private Timer timer_scan_timeout;
	private final int HANDLER_SCAN_TIMEOUT = 111;

	private void timerScanDevice() {
		TimerHelper.cancelTimer(timer_scan_timeout);
		timer_scan_timeout = new Timer();
		timer_scan_timeout.schedule(new TimerTask() {

			@Override
			public void run() {
				if (myHandler != null) {
					myHandler.sendEmptyMessage(HANDLER_SCAN_TIMEOUT);
				}

			}
		}, 6 * 1000);
	}

	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_SCAN_TIMEOUT:
				is_start = false;
				fresh_bar.setVisibility(View.GONE);
				list.setVisibility(View.GONE);
				text_no_device.setVisibility(View.VISIBLE);
				if (MyApp.getIntance().mService != null) {
					MyApp.getIntance().mService.scan(false, null);

				}
				break;

			default:
				break;
			}
		};
	};

	private void registerBoradcastReceiver() {
		myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(BleService.ACTION_DEVICE_FOUND);
		myIntentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
		myIntentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
		myIntentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
		myIntentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
		myIntentFilter.addAction(BleService.ACTION_STATUS_WRONG);
		myIntentFilter.addAction(BleService.ACTION_GATT_NOTIFICATION_OPEN);
		myIntentFilter.addAction(BleService.ACTION_GATT_NOTIFICATION_INEXISTENCE);

		myIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		myIntentFilter.addAction(Global.ACTION_FINISH_BAND);
		
		// 注册广播
		registerReceiver(mReceiver, myIntentFilter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleService.ACTION_DEVICE_FOUND.equals(action)) {

				Bundle data = intent.getExtras();
				final BluetoothDevice device = data.getParcelable(BluetoothDevice.EXTRA_DEVICE);
				final int rssi = data.getInt("rssi");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DbUtils db = DbUtils.create(ManageDeviceActivity.this);
						try {
							WhereBuilder builder = WhereBuilder.b("bltid", "==", device.getAddress());
							BltModel pedometer = db.findFirst(Selector.from(BltModel.class).where(builder));
							if (pedometer != null) {
								addDevice(device, rssi, true);
							} else {
								addDevice(device, rssi, false);
							}

						} catch (DbException e) {
							e.printStackTrace();
						}

					}
				});
			} else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
				if (state == BluetoothAdapter.STATE_ON) {
					if (MyApp.getIntance().mService != null) {
						MyApp.getIntance().mService.scan(true, null);
						timerScanDevice();
					}
				}
			} else if (action.equals(Global.ACTION_FINISH_BAND)) {
				ManageDeviceActivity.this.finish();
			}
		}
	};

	// 添加设备
	private void addDevice(BluetoothDevice device, int rssi, boolean isBind) {
		boolean isHave = false;
		for (int i = 0; i < lists.size(); i++) {
			if (device.getAddress().equals(lists.get(i).getMac())) {
				isHave = true;
			}
		}
		if (!isHave) {
			String deviceName = device.getName();
			Log.i("tests", "device name = " + deviceName);
			if (deviceName != null) {
				if (deviceName.contains("_")) {
					deviceName = deviceName.split("_")[0];
				}
			}
//			deviceName = StringHelper.formatDeviceName(deviceName);
			Log.i("tests", "device name = " + deviceName);
			DeviceEntity entity = new DeviceEntity();
			entity.setMac(device.getAddress());
			entity.setDevice(device);
			entity.setName(deviceName);
			entity.setRssi(rssi);
			entity.setIs_device(true);
			entity.setIs_bind(isBind);
			
			if (deviceName != null) {
				deviceName = StringHelper.formatDeviceName(deviceName);
				if (MyApp.getIntance().list_device_ride.contains(deviceName) || MyApp.getIntance().list_device_band.contains(deviceName)) {
					lists.add(entity);
					TimerHelper.cancelTimer(timer_scan_timeout);
					is_start = false;
					myHandler.sendEmptyMessage(0);
				}
			}

		} else {

			mHandler.sendEmptyMessage(0);
		}
	}

	private void initBoundDevice() {
		if (MyApp.getIntance().mService != null && MyApp.getIntance().mService.mConnectionState == BleService.STATE_CONNECTED) {
			DbUtils db = DbUtils.create(ManageDeviceActivity.this);
			try {
				WhereBuilder builder = WhereBuilder.b("bltid", "==", MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue());
				BltModel pedometer = db.findFirst(Selector.from(BltModel.class).where(builder));
				if (pedometer != null) {
					DeviceEntity entity = new DeviceEntity();
					entity.setMac(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue());
					String deviceName = pedometer.getBltname();
					if (deviceName != null) {
						if (deviceName.contains("_")) {
							deviceName = deviceName.split("_")[0];
						}
						deviceName = StringHelper.replaceDeviceNameToCC431(deviceName);
					}
					entity.setName(deviceName);
					entity.setRssi(0);
					entity.setIs_device(true);
					entity.setIs_bind(true);
					lists.add(entity);
					TimerHelper.cancelTimer(timer_scan_timeout);
					is_start = false;
					mHandler.sendEmptyMessage(0);
				} else {
					
				}

			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}
}