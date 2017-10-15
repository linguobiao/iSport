package com.cn.zhihengchuang.walkbank.activity;

import im.fir.sdk.FIR;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Looper;
import android.widget.Toast;

import com.androidex.appformwork.preference.MicroRecruitSettings;
import com.cn.zhihengchuang.walkbank.ble.BleController;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.Log;
import com.lingb.global.Global;
import com.lingb.ride.service.RideBLEService;

public class MyApp extends Application implements UncaughtExceptionHandler {

	public static MyApp getMyApp() {
		return myApp;
	}

	public static void setMyApp(MyApp myApp) {
		MyApp.myApp = myApp;
	}
	private Thread.UncaughtExceptionHandler mDefaultHandler;
 
	private static MyApp myApp;
	private static int mPosition;
	private static boolean isToday=true;
	public ArrayList<String> list_device_new;
	public ArrayList<String> list_device_band;
	public ArrayList<String> list_device_ride;
	private SharedPreferences share;
	private Editor editor;

	public static boolean isToday() {
		return isToday;
	}

	public static void setToday(boolean isToday) {
		MyApp.isToday = isToday;
	}

	public static int getmPosition() {
		return mPosition;
	}

	public static void setmPosition(int mPosition) {
		MyApp.mPosition = mPosition;
	}

	public MicroRecruitSettings getAppSettings() {
		return appSettings;
	}
	private BleController mBleController;
	private MicroRecruitSettings  appSettings;
	
	public BleService mService;
	public RideBLEService mRideService;
	

	public void setBleController(BleController bleController) {
		this.mBleController = bleController;
	}

	public static MyApp getIntance() {
		return myApp;
	}
	
	public SharedPreferences getSharedPreferences() {
		return share;
	}
	
	public Editor getEditor() {
		return editor;
	}

	public void onCreate() {
		super.onCreate();
		FIR.init(this);
		
		myApp = this;
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
		editor = share.edit();
		appSettings=new MicroRecruitSettings(this);
		// CrashHandler ch = CrashHandler.getInstance();
		// ch.init(this);
		this.init();
	}
    
	public void init() {
		if (list_device_new == null) {
			list_device_new = new ArrayList<String>();
			for (int i = 0; i < Global.DEVICE_NAME_NEW.length; i ++) {
				list_device_new.add(Global.DEVICE_NAME_NEW[i]);
			}
		}
		if (list_device_band == null) {
			list_device_band = new ArrayList<String>();
			for (int i = 0; i < Global.DEVICE_NAME.length; i ++) {
				list_device_band.add(Global.DEVICE_NAME[i]);
			}
		}
		if (list_device_ride == null) {
			list_device_ride = new ArrayList<String>();
			for (int i = 0; i < Global.DEVICE_NAME_RIDE.length; i ++) {
				list_device_ride.add(Global.DEVICE_NAME_RIDE[i]);
			}
		}
//		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
//		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public void uncaughtException(Thread thread, Throwable ex) {
//		if (!handleException(ex) && mDefaultHandler != null) {
//			// ����û�û�д�������ϵͳĬ�ϵ��쳣������������
//			Log.d("+++++++++++++++uncaughtException");
//			mDefaultHandler.uncaughtException(thread, ex);
//		} else {
//			// Sleepһ����������
//			try {
//				if (this.mBleController != null)
//					this.mBleController.close();
//
//				Log.d("+++++++++++++++sleep");
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//
//			}
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(10);
//		}

	}

	/**
	 * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����. �����߿��Ը����Լ���������Զ����쳣�����߼�
	 * 
	 * @param ex
	 * @return true:��������˸��쳣��Ϣ;���򷵻�false
	 */
	private boolean handleException(Throwable ex) {
		if(ex!=null)
			ex.printStackTrace();
		
		Log.d("+++++++++++++++handleException");
		// ʹ��Toast����ʾ�쳣��Ϣ
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(MyApp.this, "Sorry, this application got an error for runtime. please to restart the APP.", Toast.LENGTH_LONG)
						.show();
				Looper.loop();
			}

		}.start();

		return true;
	}

}
