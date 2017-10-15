package com.lingb.ride;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.ble.ParserLoader;
import com.cn.zhihengchuang.walkbank.util.DialogHelper;
import com.isport.trackernew.R;
import com.lingb.global.Global;
import com.lingb.helper.ParserHelper;
import com.lingb.helper.SpHelper;
import com.lingb.helper.TimerHelper;
import com.lingb.helper.ViewHelper;
import com.lingb.ride.bean.Profile;
import com.lingb.ride.bean.Ride;
import com.lingb.ride.database.DatabaseProvider;
import com.lingb.ride.history.RideHistoryActivity;
import com.lingb.ride.service.RideBLEService;
import com.lingb.ride.settings.RideDeviceActivity;
import com.lingb.ride.settings.RideUserActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class RideMainActivity extends Activity {

	private final int USELESS_MIN_VALUE = 100000;

	private final int HANDLER_PLAY = 111;
	private final int HANDLER_STOP = 112;
	private final int HANDLER_SEARCH_FIAL = 113;

	private final int TYPE_CLICK_NULL = 0;
	private final int TYPE_CLICK_SETTING = 1;
	private final int TYPE_CLICK_HISTORY = 2;
	private final int TYPE_CLICK_BACK = 3;
	private int typeClick = TYPE_CLICK_NULL;

	private final int STATE_NULL = -1;
	private final int STATE_PLAYING = 0;
	private final int STATE_PAUSE = 1;
	private int state = STATE_NULL;

	private final String TAG = "RideMainActivity";
	private boolean isBounding = false;
	private AlertDialog dialog_finish;
	private Dialog dialog_search;
	private Dialog dialog_ble_no_open;
	private Dialog dialog_confirm_cancel;
	private Dialog dialog_no_connect;
	private Timer timer_play;
	private Timer timer_stop;
	private Timer timer_hide_search_dialog;
	private int secondTime = 0;
	private int secondTimeSpeed = 0;
	private int secondTimeCadence = 0;

	private boolean isDisconnectByUser = false;

	private long timeLastValue;

	private int unit = Global.TYPE_UNIT_METRIC;

	// private PopupWindow mPopupWindow;

	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_PLAY:
				// long bettwn = Calendar.getInstance().getTimeInMillis() -
				// timeLastValue;
				// if (bettwn > 1000 * 3) {
				// speed = 0;
				// cadence = 0;
				// }
				// double cadence1 = arrayCadence[1];
				// double cadence2 = arrayCadence[2];
				// double cadence3 = arrayCadence[3];
				//
				// arrayCadence[0] = cadence1;
				// arrayCadence[1] = cadence2;
				// arrayCadence[2] = cadence3;
				// arrayCadence[3] = cadence;
				//
				// double resultCadence = 0;
				// if (arrayCadence[3] > 0 && arrayCadence[3] < 300) {
				// resultCadence = arrayCadence[3];
				// } else if (arrayCadence[2] > 0 && arrayCadence[2] < 300) {
				// resultCadence = arrayCadence[2];
				// } else if (arrayCadence[1] > 0 && arrayCadence[1] < 300) {
				// resultCadence = arrayCadence[1];
				// } else if (arrayCadence[0] > 0 && arrayCadence[0] < 300){
				// resultCadence = arrayCadence[0];
				// }

				double resultCadence = ParserHelper.parserCurrentValue(arrayCadence, cadence);
				double resultSpeed = ParserHelper.parserCurrentValue(arraySpeed, speed);

				// 当前速度
				ViewHelper.setText(text_speed, resultSpeed, Global.df_0_0, unit);
				// 当前转速
				text_cadence.setText(Global.df_0.format(resultCadence));

				// 没有暂停才设置
				if (state == STATE_PLAYING && MyApp.getIntance().mRideService.getConnectionState() == RideBLEService.STATE_CONNECTED) {
					// 最大速度
					speedMax = ParserHelper.parserMaxValue(resultSpeed, speedMax);
					ViewHelper.setText(text_speed_max, speedMax, Global.df_0_0, unit);
					// 最小速度
					if (resultSpeed > 0) {
						speedMin = ParserHelper.parserMinValue(resultSpeed, speedMin);
						secondTimeSpeed++;
					}
					if (speedMin == USELESS_MIN_VALUE) {
						ViewHelper.setText(text_speed_min, 0.0, Global.df_0_0, unit);
					} else {
						ViewHelper.setText(text_speed_min, speedMin, Global.df_0_0, unit);
					}
					// 总速度
					speedTatol += resultSpeed;
					if (secondTimeSpeed != 0) {
						// 平均速度
						ViewHelper.setText(text_speed_average, speedTatol / secondTimeSpeed, Global.df_0_0, unit);
						// 距离
						ViewHelper.setText(text_distance, (double) speedTatol / secondTimeSpeed * (double) secondTimeSpeed / 3600, Global.df_0_00, unit);

					}

					// 最大转速
					cadenceMax = ParserHelper.parserMaxValue(resultCadence, cadenceMax);
					text_cadence_max.setText(Global.df_0.format(cadenceMax));
					// 最小转速
					if (resultCadence > 0) {
						cadenceMin = ParserHelper.parserMinValue(resultCadence, cadenceMin);
						secondTimeCadence++;
					}
					if (cadenceMin == USELESS_MIN_VALUE) {
						text_cadence_min.setText(Global.df_0.format(0));
					} else {
						text_cadence_min.setText(Global.df_0.format(cadenceMin));
					}
					// 总转速
					cadenceTatol += resultCadence;
					// 平均转速
					if (secondTimeCadence != 0)
						text_cadence_average.setText(Global.df_0.format(cadenceTatol / secondTimeCadence));

					// 总时间
					secondTime++;
					int minute = secondTime / 60;
					int second = secondTime % 60;
					text_time.setText(Global.df_00.format(minute) + ":" + Global.df_00.format(second));

				}
				break;
			case HANDLER_STOP:
				int visibility = image_play_black.getVisibility();
				if (visibility == View.VISIBLE) {
					image_play_black.setVisibility(View.INVISIBLE);
				} else {
					image_play_black.setVisibility(View.VISIBLE);

				}

				break;
			case HANDLER_SEARCH_FIAL:
				DialogHelper.hideDialog(dialog_search);
				break;

			default:
				break;
			}
		};
	};

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_main);
		initUI();

		initReceiverOther();
		startService(new Intent(RideMainActivity.this, RideBLEService.class));
		stopService(new Intent(RideMainActivity.this, BleService.class));
		// initServiceConnection();
		SpHelper.putBoolean(Global.KEY_IS_NEW_UP_RIDE, false);
		SpHelper.putInt(Global.KEY_DEVICE, Global.TYPE_DEVICE_RIDE);
		timerPlay();

		ArrayList<Byte> by = new ArrayList<Byte>();
		by.add((byte) 0x03);
		by.add((byte) 0x04);
		ParserLoader.byteArrayListToInt(by);
		// Integer.parseInt(null);
		Log.i("test", "");
	};

	@Override
	protected void onResume() {
		initReceiver();
		Profile profile = DatabaseProvider.queryProfile(RideMainActivity.this, RideUserActivity.DEF_NAME);
		if (profile != null) {
			unit = profile.getUnit();
		}

		initLabel();
		initValue();
		if (MyApp.getIntance().mRideService != null) {
			if (!isDisconnectByUser) {
				updateBleScan(true);

			}
		}
		if (MyApp.getIntance().mRideService != null && MyApp.getIntance().mRideService.getConnectionState() == RideBLEService.STATE_CONNECTED) {
			MyApp.getIntance().mRideService.set_notify_true(true);
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(myBroadcastReceiver);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// unbindService(myServiceConnection);
		unregisterReceiver(myBroadcastReceiverOther);
		TimerHelper.cancelTimer(timer_play);
		TimerHelper.cancelTimer(timer_stop);
		TimerHelper.cancelTimer(timer_hide_search_dialog);
		DialogHelper.dismissDialog(dialog_finish);
		DialogHelper.dismissDialog(dialog_search);
		DialogHelper.dismissDialog(dialog_ble_no_open);
		DialogHelper.dismissDialog(dialog_confirm_cancel);
		DialogHelper.dismissDialog(dialog_no_connect);
		stopService(new Intent(RideMainActivity.this, RideBLEService.class));
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && state == STATE_PLAYING) {
			clickFinish(TYPE_CLICK_BACK);
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	private OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.image_setting:
				if (state != STATE_NULL) {
					clickFinish(TYPE_CLICK_SETTING);
				} else {
					startActivity(new Intent(RideMainActivity.this, RideSettingsActivity.class));
				}
				break;
			case R.id.image_history:
				if (state != STATE_NULL) {
					clickFinish(TYPE_CLICK_HISTORY);
				} else {
					startActivity(new Intent(RideMainActivity.this, RideHistoryActivity.class));
				}
				break;
			case R.id.switch_play:
				clickPlay();
				break;
			case R.id.image_finish:
				clickFinish(TYPE_CLICK_NULL);
				break;
			case R.id.text_save:
				clickSave();
				DialogHelper.hideDialog(dialog_finish);
				break;
			case R.id.text_discard:
				clickDiscard();
				break;
			case R.id.text_cancel_:
				if (state == STATE_PAUSE) {
					state = STATE_PLAYING;
				}
				DialogHelper.hideDialog(dialog_finish);
				break;
			case R.id.text_confirm:
				clearAllText();

				if (typeClick == TYPE_CLICK_SETTING) {
					startActivity(new Intent(RideMainActivity.this, RideSettingsActivity.class));
				} else if (typeClick == TYPE_CLICK_HISTORY) {
					startActivity(new Intent(RideMainActivity.this, RideHistoryActivity.class));
				} else if (typeClick == TYPE_CLICK_BACK) {
					RideMainActivity.this.finish();
				} else {

				}
				DialogHelper.hideDialog(dialog_finish);
				DialogHelper.dismissDialog(dialog_confirm_cancel);
				break;
			case R.id.text_cancel:
				if (state == STATE_PAUSE) {
					state = STATE_PLAYING;
				}
				DialogHelper.dismissDialog(dialog_confirm_cancel);
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
	 * myBroadcastReceiver
	 */
	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(RideBLEService.ACTION_GATT_CONNECTED)) {
				stateConnect = -1;
				if (MyApp.getIntance().mRideService != null) {
					MyApp.getIntance().mRideService.scan(false);
				}
				if (!isBounding) {
					isBounding = false;
				}
				TimerHelper.cancelTimer(timer_hide_search_dialog);
				DialogHelper.hideDialog(dialog_search);
				isDisconnectByUser = false;
			} else if (action.equals(RideBLEService.ACTION_GATT_DISCONNECTED)) {
				receiveDisconnect();
				if (!isBounding) {
					isBounding = false;
				}
			} else if (action.equals(RideBLEService.ACTION_GATT_CONNECTED_FAIL)) {
				receiveConnectFail();
			} else if (action.equals(RideBLEService.ACTION_GATT_SERVICES_DISCOVERED)) {
				receiveServiceDiscover();
			} else if (action.equals(RideBLEService.ACTION_WRITE_DESCRIPTOR)) {
				receiveWriteDiscriptor();
			} else if (action.equals(RideBLEService.ACTION_DEVICE_FOUND)) {
				receiveDeviceFound(intent);
			} else if (action.equals(RideBLEService.ACTION_RECEIVE_DATA)) {
				receiveData(intent);
			} else if (action.equals(Global.ACTION_BOUND_MAC_CHANGED)) {
				isBounding = true;
				if (MyApp.getIntance().mRideService != null) {
					if (MyApp.getIntance().mRideService.getConnectionState() == RideBLEService.STATE_CONNECTED) {
						MyApp.getIntance().mRideService.disconnect();
					} else {
						updateBleScan(false);
					}
				}
			} else if (action.equals(Global.ACTION_FINISH_RIDE)) {
				RideMainActivity.this.finish();
			} else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
				if (state == BluetoothAdapter.STATE_ON) {
					updateBleScan(false);
				}
			} else if (action.equals(Global.ACTION_DISCONNECT_BY_USER)) {
				isDisconnectByUser = true;
				// MyApp.getIntance().mRideService.disconnect();
			}
		}
	};
	private BroadcastReceiver myBroadcastReceiverOther = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Global.ACTION_FINISH_RIDE)) {
				RideMainActivity.this.finish();
			} else if (action.equals(Global.ACTION_DISCONNECT_BY_USER)) {
				isDisconnectByUser = true;
				// MyApp.getIntance().mRideService.disconnect();
			}
		}
	};

	private int stateConnect = -1;

	private void receiveDeviceFound(Intent intent) {
		Bundle data = intent.getExtras();
		BluetoothDevice device = data.getParcelable(BluetoothDevice.EXTRA_DEVICE);

		String boundAddress = SpHelper.getString(Global.KEY_BOUNDED_DEVICE, null);
		Log.i(TAG, ">> 已经绑定的手环：" + boundAddress);
		if (device != null) {
			String address = device.getAddress();
			if (address != null && boundAddress != null) {
				if (address.startsWith(boundAddress)) {
					if (MyApp.getIntance().mRideService != null) {
						MyApp.getIntance().mRideService.scan(false);
						if (stateConnect != RideBLEService.STATE_CONNECTING) {
							stateConnect = RideBLEService.STATE_CONNECTING;
							Log.i(TAG, ">> 开始连接新设备：" + address);
							MyApp.getIntance().mRideService.connect(address, false);
						}
					}
				}
			}
		}
	}

	private void receiveConnectFail() {
		if (MyApp.getIntance().mRideService != null) {
			MyApp.getIntance().mRideService.scan(false);
			MyApp.getIntance().mRideService.disconnect();
		}
		stateConnect = -1;
	}

	private void receiveDisconnect() {
		if (MyApp.getIntance().mRideService != null) {
			MyApp.getIntance().mRideService.disconnect();
		}
		String boundedAddress = SpHelper.getString(Global.KEY_BOUNDED_DEVICE, null);
		if (boundedAddress != null) {
			if (isDisconnectByUser == false) {
				updateBleScan(false);
			}

		}
		stateConnect = -1;
		// TimerHelper.cancelTimer(timer_play);
		// isDisconnectByUser = false;

	}

	private void receiveServiceDiscover() {
		// MyApp.getIntance().mRideService.set_notify_true(true);
		stateConnect = -1;
	}

	private void receiveWriteDiscriptor() {
		Log.i(TAG, ">> 连接成功");
		// timerPlay();
		stateConnect = -1;
	}

	/**
	 * 清空数据
	 */
	private void clearAllText() {
		TimerHelper.cancelTimer(timer_stop);
		image_play_black.setVisibility(View.INVISIBLE);
		state = STATE_NULL;
		secondTime = 0;
		secondTimeSpeed = 0;
		secondTimeCadence = 0;
		image_finish.setVisibility(View.GONE);
		switch_play.setBackgroundResource(R.drawable.button_ride_play);

		text_time.setText("00:00");
		text_distance.setText(Global.df_0.format(0));
		// text_speed.setText(Global.df_0_00.format(0));
		text_speed_average.setText(Global.df_0.format(0));
		text_speed_max.setText(Global.df_0.format(0));
		text_speed_min.setText(Global.df_0.format(0));
		// text_cadence.setText(Global.df_0_00.format(0));
		text_cadence_average.setText(Global.df_0.format(0));
		text_cadence_max.setText(Global.df_0.format(0));
		text_cadence_min.setText(Global.df_0.format(0));

		speedTatol = 0;
		speedMax = 0;
		speedMin = USELESS_MIN_VALUE;
		cadenceTatol = 0;
		cadenceMax = 0;
		cadenceMin = USELESS_MIN_VALUE;

	}

	private double[] arrayCadence = new double[4];
	private double[] arraySpeed = new double[4];

	private void receiveData(Intent intent) {
		Log.i("sync", "recevide date");
		timeLastValue = Calendar.getInstance().getTimeInMillis();
		byte[] value = intent.getByteArrayExtra(RideBLEService.KEY_RECEIVE_DATA);
		double[] result = ParserHelper.ParserData(RideMainActivity.this, value);
		if (result[0] > 0 && result[0] <= 60) {
			speed = result[0];
		} else {
			speed = 0;
		}
		if (result[1] >= 0 && result[1] < 300) {
			cadence = result[1];
		}
	}

	private double speed = 0;
	private double speedTatol = 0;
	private double speedMax = 0;
	private double speedMin = USELESS_MIN_VALUE;
	private double cadence = 0;
	private double cadenceTatol = 0;
	private double cadenceMax = 0;
	private double cadenceMin = USELESS_MIN_VALUE;

	// private double parserMaxSpeed(double speed) {
	// if (speed > speedMax) {
	// return speed;
	// } else {
	// return speedMax;
	// }
	// }
	//
	// private double parserMinSpeed(double speed) {
	// if (speed < speedMin) {
	// return speed;
	// } else {
	// return speedMin;
	// }
	// }
	//
	// private double parserMaxCadence(double cadence) {
	// if (cadence > cadenceMax) {
	// return cadence;
	// } else {
	// return cadenceMax;
	// }
	// }
	//
	// private double parserMinCadence(double cadence) {
	// if (cadence < cadenceMin) {
	// return cadence;
	// } else {
	// return cadenceMin;
	// }
	// }

	private void clickPlay() {
		// 从无状态到开始状态
		if (state == STATE_NULL) {
			String boundAddress = SpHelper.getString(Global.KEY_BOUNDED_DEVICE, null);
			if (boundAddress == null) {
				showNoBoundDialog();

			} else {
				if (MyApp.getIntance().mRideService != null && MyApp.getIntance().mRideService.getConnectionState() == RideBLEService.STATE_CONNECTED) {
					state = STATE_PLAYING;
					switch_play.setBackground(getResources().getDrawable(R.drawable.button_ride_stop));
					image_finish.setVisibility(View.VISIBLE);

					initValuePlay();

				} else {

					updateBleScan(false);
				}
			}

		}
		// 从开始状态到暂停状态
		else if (state == STATE_PLAYING) {
			if (MyApp.getIntance().mRideService != null && MyApp.getIntance().mRideService.getConnectionState() == RideBLEService.STATE_CONNECTED) {
				state = STATE_PAUSE;
				switch_play.setBackground(getResources().getDrawable(R.drawable.button_ride_play));
				timerStop();
				image_finish.setVisibility(View.VISIBLE);
			}
		}
		// 从暂停状态到开始状态
		else if (state == STATE_PAUSE) {
			if (MyApp.getIntance().mRideService != null && MyApp.getIntance().mRideService.getConnectionState() == RideBLEService.STATE_CONNECTED) {
				MyApp.getIntance().mRideService.set_notify_true(true);
				state = STATE_PLAYING;
				switch_play.setBackground(getResources().getDrawable(R.drawable.button_ride_stop));
				image_finish.setVisibility(View.VISIBLE);
				TimerHelper.cancelTimer(timer_stop);
				image_play_black.setVisibility(View.INVISIBLE);
			}

		}
	}

	private void clickFinish(int clickType) {
		typeClick = clickType;
		state = STATE_PAUSE;
		if (dialog_finish == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			View view = inflater.inflate(R.layout.view_ride_finish, null);
			TextView text_save = (TextView) view.findViewById(R.id.text_save);
			text_save.setOnClickListener(myOnClickListener);
			TextView text_discard = (TextView) view.findViewById(R.id.text_discard);
			text_discard.setOnClickListener(myOnClickListener);
			TextView text_cancel = (TextView) view.findViewById(R.id.text_cancel_);
			text_cancel.setOnClickListener(myOnClickListener);

			dialog_finish = new AlertDialog.Builder(RideMainActivity.this, R.style.dialog_style_light).setView(view).show();
		
			Window window = dialog_finish.getWindow();
			window.setGravity(Gravity.BOTTOM);
			dialog_finish.setCanceledOnTouchOutside(true);
			dialog_finish.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
						if (state == STATE_PAUSE) {
							state = STATE_PLAYING;
						}
					}
					return false;
				}
			});
		}
		dialog_finish.show();
	}

	// /**
	// * 显示list对话框
	// */
	// private void clickFinish(int clickType) {
	// typeClick = clickType;
	// if (mPopupWindow == null) {
	// View view = getLayoutInflater().inflate(R.layout.view_ride_finish, null);
	//
	// TextView text_save = (TextView) view.findViewById(R.id.text_save);
	// text_save.setOnClickListener(myOnClickListener);
	// TextView text_discard = (TextView) view.findViewById(R.id.text_discard);
	// text_discard.setOnClickListener(myOnClickListener);
	// TextView text_cancel = (TextView) view.findViewById(R.id.text_cancel_);
	// text_cancel.setOnClickListener(myOnClickListener);
	//
	// mPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
	// LayoutParams.WRAP_CONTENT, true);
	// mPopupWindow.setTouchable(true);
	// mPopupWindow.setOutsideTouchable(true);
	// // mPopupWindow.setBackgroundDrawable(new
	// // BitmapDrawable(getResources(), (Bitmap) null));
	// mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
	// mPopupWindow.setAnimationStyle(R.style.AnimTools);
	// mPopupWindow.showAtLocation(findViewById(R.id.layout_root),
	// Gravity.BOTTOM, 0, 0);
	//
	// mPopupWindow.setOnDismissListener(new poponDismissListener());
	// } else {
	// mPopupWindow.showAtLocation(findViewById(R.id.layout_root),
	// Gravity.BOTTOM, 0, 0);
	// }
	//
	// backgroundAlpha(0.5f);
	//
	// }

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		getWindow().setAttributes(lp);
	}

	/**
	 * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
	 * 
	 * @author cg
	 * 
	 */
	class poponDismissListener implements PopupWindow.OnDismissListener {

		@Override
		public void onDismiss() {
			// TODO Auto-generated method stub
			// Log.v("List_noteTypeActivity:", "我是关闭事件");
			backgroundAlpha(1f);
		}

	}

	/**
	 * 保存数据
	 */
	private void clickSave() {
		Ride ride = new Ride();
		Calendar dateTime = Calendar.getInstance();

		double totalDistance = 0;
		double speed = 0;
		if (secondTimeSpeed != 0) {
			totalDistance = (double) speedTatol / secondTimeSpeed * (double) secondTimeSpeed / 3600;
			speed = speedTatol / secondTimeSpeed;

		}
		double speedMax = this.speedMax;
		double speedMin = this.speedMin;
		if (speedMin >= USELESS_MIN_VALUE) {
			speedMin = 0;
		}
		double cadence = Double.parseDouble(text_cadence_average.getText().toString());
		double cadenceMax = Double.parseDouble(text_cadence_max.getText().toString());
		double cadenceMin = Double.parseDouble(text_cadence_min.getText().toString());
		ride.setDateTime(dateTime);
		ride.setTotalTime(secondTime);
		ride.setTotalDistance(totalDistance);
		ride.setSpeed(speed);
		ride.setSpeedMax(speedMax);
		ride.setSpeedMin(speedMin);
		ride.setCadence(cadence);
		ride.setCadenceMax(cadenceMax);
		ride.setCadenceMin(cadenceMin);
		DatabaseProvider.insertHistoryHour(RideMainActivity.this, ride);
		clearAllText();
		if (typeClick == TYPE_CLICK_SETTING) {
			startActivity(new Intent(RideMainActivity.this, RideSettingsActivity.class));
		} else if (typeClick == TYPE_CLICK_HISTORY) {
			startActivity(new Intent(RideMainActivity.this, RideHistoryActivity.class));
		} else if (typeClick == TYPE_CLICK_BACK) {
			RideMainActivity.this.finish();
		} else {

		}
	}

	/**
	 * 忽略数据
	 */
	private void clickDiscard() {
		
		DialogHelper.dismissDialog(dialog_finish);

		if (dialog_confirm_cancel == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.dialog_ride_confirm_cancel, null);
			TextView text_msg = (TextView) view.findViewById(R.id.text_msg);
			text_msg.setText(getString(R.string.ride_Confirm_to_discard));
			TextView text_confirm = (TextView) view.findViewById(R.id.text_confirm);
			TextView text_cancel = (TextView) view.findViewById(R.id.text_cancel);
			text_confirm.setOnClickListener(myOnClickListener);
			text_cancel.setOnClickListener(myOnClickListener);
			dialog_confirm_cancel = new Dialog(RideMainActivity.this, R.style.dialog_transparent);
			dialog_confirm_cancel.setContentView(view);
			dialog_confirm_cancel.show();

			dialog_confirm_cancel.setCanceledOnTouchOutside(true);
			dialog_confirm_cancel.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
						if (state == STATE_PAUSE) {
							state = STATE_PLAYING;
						}
					}
					return false;
				}
			});
		} else {
			dialog_confirm_cancel.show();
		}

		// new AlertDialog.Builder(RideMainActivity.this,
		// R.style.dialog_style_light).setMessage(getString(R.string.ride_Confirm_to_discard)).setPositiveButton(getString(R.string.ride_Confirm),
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// clearAllText();
		//
		// if (typeClick == TYPE_CLICK_SETTING) {
		// startActivity(new Intent(RideMainActivity.this,
		// RideSettingsActivity.class));
		// } else if (typeClick == TYPE_CLICK_HISTORY) {
		// startActivity(new Intent(RideMainActivity.this,
		// RideHistoryActivity.class));
		// } else if (typeClick == TYPE_CLICK_BACK) {
		// RideMainActivity.this.finish();
		// } else {
		//
		// }
		//
		// }
		// }).setNegativeButton(getString(R.string.ride_Cancel), new
		// DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		//
		// }
		// }).show();
	}

	/**
	 * 显示蓝牙没有打开对话框
	 */
	private void showBleNoOpenDialog() {
		if (dialog_ble_no_open == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.dialog_ride_ok, null);
			TextView text_msg = (TextView) view.findViewById(R.id.text_msg);
			text_msg.setText(getString(R.string.ride_turn_on_ble));
			TextView text_ok = (TextView) view.findViewById(R.id.text_ok);
			text_ok.setOnClickListener(myOnClickListener);
			dialog_ble_no_open = new Dialog(RideMainActivity.this, R.style.dialog_transparent);
			dialog_ble_no_open.setContentView(view);
			dialog_ble_no_open.show();

			// dialog_ble_no_open = new AlertDialog.Builder(new
			// ContextThemeWrapper(this,
			// R.style.dialog_style_light_2)).setMessage(getString(R.string.ride_turn_on_ble)).setPositiveButton("OK",
			// null).show();

		} else {
			dialog_ble_no_open.show();

		}
	}

	/**
	 * 显示还没有绑定对话框
	 */
	private void showNoBoundDialog() {

		if (dialog_no_connect == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.dialog_ride_ok, null);
			TextView text_msg = (TextView) view.findViewById(R.id.text_msg);
			text_msg.setText(getString(R.string.ride_no_device_connect));
			TextView text_ok = (TextView) view.findViewById(R.id.text_ok);
			text_ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogHelper.dismissDialog(dialog_no_connect);
					RideMainActivity.this.startActivity(new Intent(RideMainActivity.this, RideDeviceActivity.class));
				}
			});
			dialog_no_connect = new Dialog(RideMainActivity.this, R.style.dialog_transparent);
			dialog_no_connect.setContentView(view);
			dialog_no_connect.show();

			// dialog_ble_no_open = new AlertDialog.Builder(new
			// ContextThemeWrapper(this,
			// R.style.dialog_style_light_2)).setMessage(getString(R.string.ride_turn_on_ble)).setPositiveButton("OK",
			// null).show();

		} else {
			dialog_no_connect.show();

		}

		// new AlertDialog.Builder(RideMainActivity.this,
		// style.dialog_style_light).setMessage(getString(R.string.ride_no_device_connect)).setPositiveButton(getString(R.string.ride_Confirm),
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// RideMainActivity.this.startActivity(new Intent(RideMainActivity.this,
		// RideDeviceActivity.class));
		// }
		// }).show();
	}

	/**
	 * 显示正在同步对话框
	 */
	private void showSearchDialog() {
		if (dialog_search == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			View view_progress = inflater.inflate(R.layout.view_progress, null);
			dialog_search = new Dialog(RideMainActivity.this, R.style.dialog_transparent);
			dialog_search.setContentView(view_progress);
			dialog_search.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					com.lingb.helper.TimerHelper.cancelTimer(timer_hide_search_dialog);
					return false;
				}
			});
			dialog_search.setCanceledOnTouchOutside(true);
			dialog_search.show();
		} else {
			dialog_search.show();
		}
		timerSearch();
	}

	/**
	 * 
	 */
	private void timerPlay() {
		TimerHelper.cancelTimer(timer_play);
		timer_play = new Timer();
		timer_play.schedule(new TimerTask() {

			@Override
			public void run() {
				myHandler.sendEmptyMessage(HANDLER_PLAY);

			}
		}, 0, 1000);
	}

	private void timerStop() {
		TimerHelper.cancelTimer(timer_stop);
		timer_stop = new Timer();
		timer_stop.schedule(new TimerTask() {

			@Override
			public void run() {
				myHandler.sendEmptyMessage(HANDLER_STOP);

			}
		}, 0, 500);
	}

	private void timerSearch() {
		TimerHelper.cancelTimer(timer_hide_search_dialog);
		timer_hide_search_dialog = new Timer();
		timer_hide_search_dialog.schedule(new TimerTask() {

			@Override
			public void run() {
				myHandler.sendEmptyMessage(HANDLER_SEARCH_FIAL);

			}
		}, 15 * 1000);
	}

	/**
	 * 开始扫描设备
	 */
	private void beginScanDevice(boolean isBound) {

		if (MyApp.getIntance().mRideService != null) {
			MyApp.getIntance().mRideService.scan(false);
			int connectState = MyApp.getIntance().mRideService.getConnectionState();
			// 蓝牙没有连接设备
			if (connectState != RideBLEService.STATE_CONNECTED) {
				Log.i(TAG, "no connected device, begin to scan");
				// 清空map
				// 开始搜索
				Thread t = new Thread() {
					public void run() {
						MyApp.getIntance().mRideService.scan(true);
					};
				};
				t.start();

			}
		}
	}

	private void updateBleScan(boolean isResume) {
		stateConnect = -1;
		if (MyApp.getIntance().mRideService != null && MyApp.getIntance().mRideService.getConnectionState() != RideBLEService.STATE_CONNECTED) {
			// 获取绑定的地址
			String address = SpHelper.getString(Global.KEY_BOUNDED_DEVICE, null);
			if (address != null) {
				BluetoothAdapter mBtAdapter = MyApp.getIntance().mRideService.initBluetooth_manual(this, isResume);
				if (mBtAdapter.isEnabled()) {
					beginScanDevice(true);
					showSearchDialog();
				} else {
					showBleNoOpenDialog();
				}
			}
		}
	}

	// /**
	//
	// * 开启View闪烁效果
	//
	// *
	//
	// * */
	//
	// private void startFlick( View view ){
	//
	// if( null == view ){
	//
	// return;
	//
	// }
	//
	// Animation alphaAnimation = new AlphaAnimation( 1, 0 );
	//
	// alphaAnimation.setDuration( 500 );
	//
	// alphaAnimation.setInterpolator( new LinearInterpolator( ) );
	//
	// alphaAnimation.setRepeatCount( 50 );
	//
	// alphaAnimation.setRepeatMode( Animation.REVERSE );
	//
	// view.startAnimation( alphaAnimation );
	//
	// }
	//
	// /**
	//
	// * 取消View闪烁效果
	//
	// *
	//
	// * */
	//
	// private void stopFlick( View view ){
	//
	// if( null == view ){
	//
	// return;
	//
	// }
	//
	// view.clearAnimation( );
	//
	// }

	/**
	 * 初始化标签和单位
	 */
	private void initLabel() {
		if (unit == Global.TYPE_UNIT_IMPERIAL) {
			label_S.setText(getString(R.string.ride_Speed) + "(" + getString(R.string.ride_mph) + ")");
			label_MAX_S.setText(getString(R.string.ride_Max) + " " + getString(R.string.ride_Speed) + "(" + getString(R.string.ride_mph) + ")");
			label_MIN_S.setText(getString(R.string.ride_Min) + " " + getString(R.string.ride_Speed) + "(" + getString(R.string.ride_mph) + ")");
			label_A_S.setText(getString(R.string.ride_average) + " " + getString(R.string.ride_Speed) + "(" + getString(R.string.ride_mph) + ")");
			label_D.setText(getString(R.string.ride_Distance) + "(" + getString(R.string.ride_mi) + ")");
		} else {
			label_S.setText(getString(R.string.ride_Speed) + "(" + getString(R.string.ride_kmh) + ")");
			label_MAX_S.setText(getString(R.string.ride_Max) + " " + getString(R.string.ride_Speed) + "(" + getString(R.string.ride_kmh) + ")");
			label_MIN_S.setText(getString(R.string.ride_Min) + " " + getString(R.string.ride_Speed) + "(" + getString(R.string.ride_kmh) + ")");
			label_A_S.setText(getString(R.string.ride_average) + " " + getString(R.string.ride_Speed) + "(" + getString(R.string.ride_kmh) + ")");
			label_D.setText(getString(R.string.ride_Distance) + "(" + getString(R.string.ride_km) + ")");

		}
	}

	/**
	 * 初始化Value
	 */
	private void initValue() {
		text_time.setText("00:00");
		text_distance.setText(Global.df_0.format(0));
		text_speed_average.setText(Global.df_0.format(0));
		text_speed_max.setText(Global.df_0.format(0));
		text_speed_min.setText(Global.df_0.format(0));
		text_cadence_average.setText(Global.df_0.format(0));
		text_cadence_max.setText(Global.df_0.format(0));
		text_cadence_min.setText(Global.df_0.format(0));

	}

	/**
	 * 初始化Value
	 */
	private void initValuePlay() {
		text_time.setText("00:00");
		text_distance.setText(Global.df_0_00.format(0).replace(",", "."));
		text_speed_average.setText(Global.df_0_0.format(0).replace(",", "."));
		text_speed_max.setText(Global.df_0_0.format(0).replace(",", "."));
		text_speed_min.setText(Global.df_0_0.format(0).replace(",", "."));
		text_cadence_average.setText(Global.df_0.format(0));
		text_cadence_max.setText(Global.df_0.format(0));
		text_cadence_min.setText(Global.df_0.format(0));

	}

	// /**
	// * my ServiceConnection
	// */
	// private ServiceConnection myServiceConnection = new ServiceConnection() {
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	// Log.i(TAG, "onServiceDisconnected");
	// MyApp.getIntance().mRideService = null;
	// }
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// Log.i(TAG, "onServiceConnected");
	// MyApp.getIntance().mRideService = ((RideBLEService.LocalBinder)
	// service).getService();
	// if (MyApp.getIntance().mRideService.getConnectionState() ==
	// RideBLEService.STATE_CONNECTED) {
	// } else {
	// updateBleScan(false);
	// }
	// }
	// };
	//
	// /**
	// * 初始化 serviceConnection
	// */
	// private void initServiceConnection() {
	// Log.i(TAG, "初始化 serviceConnection");
	// Intent bindIntent = new Intent(RideMainActivity.this,
	// RideBLEService.class);
	// RideMainActivity.this.bindService(bindIntent, myServiceConnection,
	// Context.BIND_AUTO_CREATE);
	//
	// }

	/**
	 * 初始化广播接收器
	 */
	private void initReceiver() {
		IntentFilter f = new IntentFilter();
		f.addAction(RideBLEService.ACTION_GATT_CONNECTED);
		f.addAction(RideBLEService.ACTION_GATT_CONNECTED_FAIL);
		f.addAction(RideBLEService.ACTION_GATT_DISCONNECTED);
		f.addAction(RideBLEService.ACTION_DEVICE_FOUND);
		f.addAction(RideBLEService.ACTION_GATT_SERVICES_DISCOVERED);
		f.addAction(RideBLEService.ACTION_WRITE_DESCRIPTOR);
		f.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		f.addAction(RideBLEService.ACTION_RECEIVE_DATA);
		f.addAction(Global.ACTION_BOUND_MAC_CHANGED);
		f.addAction(Global.ACTION_FINISH_RIDE);
		f.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		f.addAction(Global.ACTION_DISCONNECT_BY_USER);
		registerReceiver(myBroadcastReceiver, f);
	}

	private void initReceiverOther() {
		IntentFilter f = new IntentFilter();
		f.addAction(Global.ACTION_FINISH_RIDE);
		f.addAction(Global.ACTION_DISCONNECT_BY_USER);
		registerReceiver(myBroadcastReceiverOther, f);
	}

	private void initUI() {
		ImageView image_setting = (ImageView) findViewById(R.id.image_setting);
		image_setting.setOnClickListener(myOnClickListener);
		ImageView image_history = (ImageView) findViewById(R.id.image_history);
		image_history.setOnClickListener(myOnClickListener);

		text_speed = (TextView) findViewById(R.id.text_speed);
		text_speed_average = (TextView) findViewById(R.id.text_speed_average);
		text_speed_max = (TextView) findViewById(R.id.text_speed_max);
		text_speed_min = (TextView) findViewById(R.id.text_speed_min);
		text_cadence = (TextView) findViewById(R.id.text_cadence);
		text_cadence_average = (TextView) findViewById(R.id.text_cadence_average);
		text_cadence_max = (TextView) findViewById(R.id.text_cadence_max);
		text_cadence_min = (TextView) findViewById(R.id.text_cadence_min);
		text_time = (TextView) findViewById(R.id.text_time);
		text_distance = (TextView) findViewById(R.id.text_distance);

		label_S = (TextView) findViewById(R.id.text_label_S);
		label_MAX_S = (TextView) findViewById(R.id.text_label_MAX_S);
		label_MIN_S = (TextView) findViewById(R.id.text_label_MIN_S);
		label_A_S = (TextView) findViewById(R.id.text_label_A_S);
		label_D = (TextView) findViewById(R.id.text_label_d);

		switch_play = (ImageView) findViewById(R.id.switch_play);
		switch_play.setOnClickListener(myOnClickListener);
		image_finish = (ImageView) findViewById(R.id.image_finish);
		image_finish.setOnClickListener(myOnClickListener);
		image_play_black = (ImageView) findViewById(R.id.image_play_black);
		image_play_black.getBackground().setAlpha(70);

	}

	private TextView text_speed, text_speed_average, text_speed_max, text_speed_min;
	private TextView text_cadence, text_cadence_average, text_cadence_max, text_cadence_min;
	private TextView text_time, text_distance;
	private TextView label_S, label_A_S, label_MAX_S, label_MIN_S, label_D;
	private ImageView switch_play, image_play_black;
	private ImageView image_finish;

}
