package com.lingb.splash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.cn.zhihengchuang.walkbank.activity.MainActivityGroup;
import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.entity.DeviceEntity;
import com.cn.zhihengchuang.walkbank.util.DialogHelper;
import com.cn.zhihengchuang.walkbank.util.TimerHelper;
import com.isport.trackernew.R;
import com.isport.trackernew.R.style;
import com.lingb.global.BaseActivity;
import com.lingb.global.Global;
import com.lingb.helper.ParserHelper;
import com.lingb.helper.SpHelper;
import com.lingb.helper.StringHelper;
import com.lingb.ride.adapter.MyListView;
import com.lingb.ride.adapter.MyListView.OnRefreshListener;
import com.lingb.ride.service.RideBLEService;
import com.lingb.ride.settings.RideUserActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FirstDeviceActivity extends BaseActivity {

	private final int HANDLER_SCAN_TIMEOUT = 111;
	private final int HANDLER_FOUND_DEVICE = 112;

	private ArrayList<DeviceEntity> lists;
	private boolean is_start;
	private FirstDeviceAdapter adapter;
	private IntentFilter myIntentFilter;

	// private Dialog dialog_select;
	private Dialog dialog_search;
	private Dialog dialog_device_is_connected;
	private Dialog dialog_ble_no_open;
	private Timer timer_scan_timeout;
	private boolean isBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_setting_device);

		lists = new ArrayList<DeviceEntity>();
		initUI();
		startService(new Intent(FirstDeviceActivity.this, BleService.class));
		initBoundDevice();
		initListView();
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
		DialogHelper.dismissDialog(dialog_search);
		DialogHelper.dismissDialog(dialog_device_is_connected);
		DialogHelper.dismissDialog(dialog_ble_no_open);

		unregisterReceiver(mReceiver);
		if (MyApp.getIntance().mRideService != null) {
			MyApp.getIntance().mRideService.scan(false);
		}
		lists.clear();
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED && requestCode == Global.REQUEST_ENABLE_BLUETOOTH) {
			is_start = false;
			if (MyApp.getIntance().mRideService != null) {
				MyApp.getIntance().mRideService.scan(false);

			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 显示正在扫描对话框
	 */
	private void showSearchDialog() {
		if (dialog_search == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			View view_progress = inflater.inflate(R.layout.view_progress, null);
			dialog_search = new Dialog(FirstDeviceActivity.this, R.style.dialog_transparent);
			dialog_search.setContentView(view_progress);
			dialog_search.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
						is_start = false;
					}
					return false;
				}
			});
			dialog_search.setCanceledOnTouchOutside(false);
			dialog_search.show();
		} else {
			dialog_search.show();
		}
		timerScanDevice();
	}

	/**
	 * 显示正在扫描对话框
	 */
	private void showDeviceIsConnectedDialog() {
		if (dialog_device_is_connected == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			View view_progress = inflater.inflate(R.layout.view_progress, null);
			TextView text_dialog = (TextView) view_progress.findViewById(R.id.text_dialog);
			text_dialog.setText(getString(R.string.ride_device_is_connected));
			dialog_device_is_connected = new Dialog(FirstDeviceActivity.this, R.style.dialog_transparent);
			dialog_device_is_connected.setContentView(view_progress);
			dialog_device_is_connected.setCanceledOnTouchOutside(false);
			dialog_device_is_connected.show();
		} else {
			dialog_device_is_connected.show();
		}
	}

	/**
	 * 显示没有打开蓝牙对话框
	 */
	private void showBleNoOpenDialog() {
		if (dialog_ble_no_open == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.dialog_ride_ok, null);
			TextView text_msg = (TextView) view.findViewById(R.id.text_msg);
			text_msg.setText(getString(R.string.ride_turn_on_ble));
			TextView text_ok = (TextView) view.findViewById(R.id.text_ok);
			text_ok.setOnClickListener(myOnClickListener);
			dialog_ble_no_open = new Dialog(FirstDeviceActivity.this, R.style.dialog_transparent);
			dialog_ble_no_open.setContentView(view);
			dialog_ble_no_open.show();
			
//			dialog_ble_no_open = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialog_style_light_2)).setMessage(getString(R.string.ride_turn_on_ble)).setPositiveButton("OK", null).show();

		} else {
			dialog_ble_no_open.show();

		}
	}

	/**
	 * 显示退出提示框
	 */
	private void showExitDialog(final Context context) {
		String connect = getString(R.string.ride_Connect);
		if (MyApp.getIntance().mRideService != null && MyApp.getIntance().mRideService.getConnectionState() == RideBLEService.STATE_CONNECTED) {
			connect = getString(R.string.ride_Disconnect);
		} else {
			connect = getString(R.string.ride_Connect);
		}
		final String[] item_exit = { connect, getString(R.string.ride_Delete), getString(R.string.ride_Cancel) };
		AlertDialog alertDialon = new AlertDialog.Builder(context).setItems(item_exit, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					if (item_exit[0].equalsIgnoreCase(getString(R.string.ride_Connect))) {
						String addressBound = SpHelper.getString(Global.KEY_BOUNDED_DEVICE, null);
						clickConnect(addressBound);
						isBound = true;
					} else {
						MyApp.getIntance().mRideService.disconnect();
						sendBroadcast(new Intent(Global.ACTION_DISCONNECT_BY_USER));
					}
					break;
				case 1:
					showUnBoundDialog();
					break;
				case 2:
					break;
				default:
					break;
				}
			}
		}).show();
		Window window = alertDialon.getWindow();
		window.setGravity(Gravity.BOTTOM);
		alertDialon.setCanceledOnTouchOutside(true);
	}

	/**
	 * 显示解除绑定对话框
	 */
	private void showUnBoundDialog() {

		new AlertDialog.Builder(FirstDeviceActivity.this, style.dialog_style_light).setMessage("         " + getString(R.string.ride_Delete_device) + "         ").setPositiveButton(getString(R.string.ride_Confirm), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SpHelper.putString(Global.KEY_BOUNDED_DEVICE, null);
				SpHelper.putString(Global.KEY_BOUNDED_NAME, null);
				if (MyApp.getIntance().mRideService != null) {
					MyApp.getIntance().mRideService.scan(false);
					MyApp.getIntance().mRideService.disconnect();
					
				}
				com.lingb.helper.TimerHelper.cancelTimer(timer_scan_timeout);
				lists.clear();
				adapter.notifyDataSetChanged();
				SpHelper.putlong(ParserHelper.KEY_WHEEL, -1);
				SpHelper.putInt(ParserHelper.KEY_WHEEL_TIME, -1);
				SpHelper.putInt(ParserHelper.KEY_PEDAL, -1);
				SpHelper.putInt(ParserHelper.KEY_PEDAL_TIME, -1);
				isBound = false;
			}
		}).setNegativeButton(getString(R.string.ride_Cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).show();
	}

	/**
	 * 绑定设备
	 * 
	 * @param mac
	 */
	private void boundDevice(DeviceEntity entity) {
		String name = entity.getName();
		String mac = entity.getMac();
		if (mac != null) {
			SpHelper.putString(Global.KEY_BOUNDED_DEVICE, mac);
			SpHelper.putString(Global.KEY_BOUNDED_NAME, name);
			MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue(mac);
			Log.i("bound", "***** bound mac = " + SpHelper.getString(Global.KEY_BOUNDED_DEVICE, null));
			scanDeviceToConnect();
			// if (name.contains(Global.DEVICE_NAME_RIDE)) {
			// MyApp.getIntance().mRideService.connect(mac, false);
			//
			// } else {
			// MyApp.getIntance().mService.connect(mac);
			//
			// }
		}
	}

	/**
	 * 连接设备
	 * 
	 * @param address
	 */
	private void clickConnect(String address) {
		BluetoothAdapter mBtAdapter = MyApp.getIntance().mRideService.initBluetooth_manual(FirstDeviceActivity.this, false);
		if (mBtAdapter != null) {
			if (!mBtAdapter.isEnabled()) {
				showBleNoOpenDialog();
				return;
			}
		}
		scanDeviceToConnect();
		// showSearchDialog();
		// MyApp.getIntance().mRideService.scan(false);
		// MyApp.getIntance().mRideService.connect(address, false);
	}

	/**
	 * 扫描超时机制
	 */
	private void timerScanDevice() {
		Log.i("bound", "***** timerScanDevic");
		TimerHelper.cancelTimer(timer_scan_timeout);
		timer_scan_timeout = new Timer();
		timer_scan_timeout.schedule(new TimerTask() {

			@Override
			public void run() {
				Log.i("bound", "***** sendEmptyMessage(HANDLER_SCAN_TIMEOUT)");
				if (myHandler != null) {
					myHandler.sendEmptyMessage(HANDLER_SCAN_TIMEOUT);
				}

			}
		}, 15 * 1000);
	}

	/**
	 * 
	 */
	protected void setLinearLayout() {
		list_device.setVisibility(View.VISIBLE);
		if (adapter == null) {
			adapter = new FirstDeviceAdapter(lists, this);
			list_device.setAdapter(adapter);
		} else {
			adapter.notifymDataSetChanged(lists);
		}
	}

	/**
	 * 开始扫描设备
	 */
	private void scanLeDevice() {
		String boundAddress = SpHelper.getString(Global.KEY_BOUNDED_DEVICE, null);
		if (boundAddress == null) {
			lists.clear();

			BluetoothAdapter mBtAdapter = MyApp.getIntance().mRideService.initBluetooth_manual(FirstDeviceActivity.this, false);
			if (mBtAdapter != null) {
				if (mBtAdapter.isEnabled()) {
					if (MyApp.getIntance().mRideService != null) {
						is_start = true;
						MyApp.getIntance().mRideService.scan(true);
						// timerScanDevice();
						initBoundDevice();
//						showSearchDialog();
						timerScanDevice();
					}
				} else {
					list_device.onRefreshComplete();
					showBleNoOpenDialog();
				}
			}
		} else {
			list_device.onRefreshComplete();
		}
	}

	private void scanDeviceToConnect() {
		stateConnect = -1 ;
		BluetoothAdapter mBtAdapter = MyApp.getIntance().mRideService.initBluetooth_manual(FirstDeviceActivity.this, false);
		if (mBtAdapter != null) {
			if (mBtAdapter.isEnabled()) {
				if (MyApp.getIntance().mRideService != null) {
					MyApp.getIntance().mRideService.scan(true);
//					showSearchDialog();
					timerScanDevice();
				}
			} else {
				showBleNoOpenDialog();
			}
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_SCAN_TIMEOUT:
				is_start = false;
				DialogHelper.hideDialog(dialog_search);
				list_device.onRefreshComplete();
				MyApp.getIntance().mRideService.scan(false);
				
//				SpHelper.putString(Global.KEY_BOUNDED_DEVICE, null);
//				MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue(null);

				break;
			case HANDLER_FOUND_DEVICE:
				DialogHelper.hideDialog(dialog_search);
				list_device.onRefreshComplete();
				setLinearLayout();
				break;

			default:
				break;
			}
		};
	};

	private int stateConnect = -1;

	private void receiveDeviceFound(Intent intent) {
		Bundle data = intent.getExtras();
		BluetoothDevice device = data.getParcelable(BluetoothDevice.EXTRA_DEVICE);

		String boundAddress = SpHelper.getString(Global.KEY_BOUNDED_DEVICE, null);
		if (device != null) {
			String address = device.getAddress();
			if (address != null && boundAddress != null) {
				if (address.startsWith(boundAddress)) {
					String name = device.getName();
					if (name != null) {
						name = StringHelper.formatDeviceName(name);
						if (MyApp.getIntance().list_device_ride.contains(name)) {
							if (MyApp.getIntance().mRideService != null) {
								MyApp.getIntance().mRideService.scan(false);
								if (MyApp.getIntance().mService != null)
								MyApp.getIntance().mService.scan(false, null);
								if (stateConnect != RideBLEService.STATE_CONNECTING) {
									stateConnect = RideBLEService.STATE_CONNECTING;
									Log.i("", ">> 开始连接新设备：" + address);
									MyApp.getIntance().mRideService.connect(address, false);
								}
							}
						} else {

							if (MyApp.getIntance().mService != null) {
								if (MyApp.getIntance().mRideService != null)
								MyApp.getIntance().mRideService.scan(false);
								MyApp.getIntance().mService.scan(false, null);
								if (stateConnect != RideBLEService.STATE_CONNECTING) {
									stateConnect = RideBLEService.STATE_CONNECTING;
									Log.i("", ">> 开始连接新设备：" + address);
									MyApp.getIntance().mService.connect(address);
								}
							}

						}
					}

				}
			}
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (RideBLEService.ACTION_DEVICE_FOUND.equals(action)) {

				String boundAddress = SpHelper.getString(Global.KEY_BOUNDED_DEVICE, null);
				if (boundAddress != null) {
					receiveDeviceFound(intent);
				} else {
					Bundle data = intent.getExtras();
					final BluetoothDevice device = data.getParcelable(BluetoothDevice.EXTRA_DEVICE);
					final int rssi = data.getInt("rssi");
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							addDevice(device, rssi, false);
						}
					});

				}
			} else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
				if (state == BluetoothAdapter.STATE_ON) {
					if (MyApp.getIntance().mRideService != null) {
						MyApp.getIntance().mRideService.scan(true);
						scanLeDevice();
					}
				}

			} else if (action.equals(RideBLEService.ACTION_GATT_CONNECTED) || action.equals(BleService.ACTION_GATT_CONNECTED)) {

				TimerHelper.cancelTimer(timer_scan_timeout);
				DialogHelper.dismissDialog(dialog_search);
				list_device.onRefreshComplete();

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
					boolean isNewUp = SpHelper.getBoolean(Global.KEY_IS_NEW_UP_RIDE, true);
					if (isNewUp) {
						FirstDeviceActivity.this.startActivity(new Intent(FirstDeviceActivity.this, RideUserActivity.class));
					}
					FirstDeviceActivity.this.setResult(RESULT_OK);
					FirstDeviceActivity.this.finish();
					
				} else if (nameBand != null && MyApp.getIntance().list_device_band.contains(nameBand)){
					FirstDeviceActivity.this.startActivity(new Intent(FirstDeviceActivity.this, MainActivityGroup.class));
					FirstDeviceActivity.this.finish();
				}
			}
		}
	};

	/**
	 * 添加设备
	 * 
	 * @param device
	 * @param rssi
	 * @param isBind
	 */
	private void addDevice(BluetoothDevice device, int rssi, boolean isBind) {
		if (device == null) {
			return;
		}
		boolean isHave = false;
		for (int i = 0; i < lists.size(); i++) {
			if (device.getAddress().equals(lists.get(i).getMac())) {
				isHave = true;
			}
		}
		if (!isHave) {
			String deviceName = device.getName();
			if (deviceName != null) {
				
				Log.i("tests", "device name = " + deviceName);
				if (deviceName != null) {
					if (deviceName.contains("_")) {
						deviceName = deviceName.split("_")[0];
					}
				}
				deviceName = StringHelper.formatDeviceName(deviceName);
				Log.i("tests", "device name = " + deviceName);
				DeviceEntity entity = new DeviceEntity();
				entity.setMac(device.getAddress());
				entity.setDevice(device);
				entity.setName(deviceName);
				entity.setRssi(rssi);
				entity.setIs_device(true);
				entity.setIs_bind(isBind);
				// if (deviceName != null) {
				// if (deviceName.contains(Global.DEVICE_NAME_RIDE)) {
				// lists.add(entity);
				// TimerHelper.cancelTimer(timer_scan_timeout);
				// is_start = false;
				// myHandler.sendEmptyMessage(HANDLER_FOUND_DEVICE);
				// }
				// }
				
				if (deviceName != null) {
					if (MyApp.getIntance().list_device_ride.contains(deviceName) || MyApp.getIntance().list_device_band.contains(deviceName)) {
						lists.add(entity);
						// TimerHelper.cancelTimer(timer_scan_timeout);
						is_start = false;
						myHandler.sendEmptyMessage(HANDLER_FOUND_DEVICE);
					}
				}
			}

		} else {

			myHandler.sendEmptyMessage(HANDLER_FOUND_DEVICE);
		}
	}

	private OnRefreshListener myOnRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			if (!is_start) {
				scanLeDevice();
			}

		}
	};

	private OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.text_back:
				finish();
				break;
			case R.id.text_fresh:

				if (!is_start) {
					scanLeDevice();
				}
				break;
			case R.id.text_connect:

				// clickConnect();
				break;
			case R.id.text_delete:
				showUnBoundDialog();
				break;
			case R.id.text_ok:
				DialogHelper.dismissDialog(dialog_ble_no_open);
				break;

			default:
				break;
			}

		}
	};

	/**
	 * 初始化已经绑定的设备
	 */
	private void initBoundDevice() {
		String boundAddress = SpHelper.getString(Global.KEY_BOUNDED_DEVICE, null);
		Log.i("bound", "boundAddress = " + boundAddress);
		if (boundAddress != null) {
			DeviceEntity entity = new DeviceEntity();
			entity.setMac(boundAddress);
			String deviceName = Global.DEVICE_NAME_RIDE[0];
			if (deviceName != null) {
				if (deviceName.contains("_")) {
					deviceName = deviceName.split("_")[0];
				}
				// deviceName =
				// StringHelper.replaceDeviceNameToCC431(deviceName);
			}
			entity.setName(deviceName);
			entity.setRssi(0);
			entity.setIs_device(true);
			entity.setIs_bind(true);
			lists.add(entity);
			TimerHelper.cancelTimer(timer_scan_timeout);
			is_start = false;
			myHandler.sendEmptyMessage(HANDLER_FOUND_DEVICE);
		} else {

		}

	}

	/**
	 * 初始化ListView
	 */
	private void initListView() {
		adapter = new FirstDeviceAdapter(lists, this);
		list_device.setAdapter(adapter);
		list_device.setonRefreshListener(myOnRefreshListener);
		list_device.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == lists.size() - 1 && lists.get(position).getMac() == null) {
				} else if (lists.get(position - 1).isIs_device()) {
					if (lists.get(position - 1).isIs_bind()) {
						showExitDialog(FirstDeviceActivity.this);
					} else {
						boundDevice(lists.get(position - 1));
					}
				}
			}
		});
	}

	private void registerBoradcastReceiver() {
		myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(RideBLEService.ACTION_DEVICE_FOUND);
		myIntentFilter.addAction(RideBLEService.ACTION_GATT_CONNECTED);
		myIntentFilter.addAction(BleService.ACTION_GATT_CONNECTED);

		myIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		// 注册广播
		registerReceiver(mReceiver, myIntentFilter);
	}

	private void initUI() {
		TextView text_back = (TextView) findViewById(R.id.text_back);
		text_back.setVisibility(View.GONE);
		list_device = (MyListView) findViewById(R.id.manage_device_list);

	}

	private MyListView list_device;

}
