package com.cn.zhihengchuang.walkbank.activity.call;

import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.SystemConfig;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
	private final static String TAG = BleService.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
//		Log.i("sms", "CallReceiver Start...");
//		/*
//		 * //如果是去电 if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){ String phoneNumber = intent .getStringExtra(Intent.EXTRA_PHONE_NUMBER); Log.d(TAG, "call OUT:" + phoneNumber); }else{
//		 */
//		String action = intent.getAction();
//		if (action != null && "com.android.phone.NotificationMgr.MissedCall_intent".equals(action)) {
//			int mMissCallCount = intent.getExtras().getInt("MissedCallNumber");
//			Log.i(TAG, "mMissCallCount>>>>" + mMissCallCount);
//		}
//		TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//		CallListener customPhoneListener = new CallListener(context);
//		telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
//		Bundle bundle = intent.getExtras();
//		String phoneNr = bundle.getString("incoming_number");
//
//		String outphoneNr = bundle.getString("outcoming_number");
//		SharedPreferences share = context.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
//
//		if (phoneNr == null) {
//			/*
//			 * boolean notHand = bundle.getBoolean("not_hand"); if(notHand){
//			 */
//			// readMissCall(context);
//			// }
//
//		} else {
//			share.edit().putString(SystemConfig.KEY_COMMING_PHONE, phoneNr).commit();
//			share.edit().putString(SystemConfig.KEY_COMMING_PHONE_NAME, getContactNameByPhoneNumber(context, phoneNr)).commit();
//			if (MyApp.getIntance().mService.mConnectionState == MyApp.getIntance().mService.STATE_CONNECTED) {
//				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMING_PHONE_NUBER;
//				MyApp.getIntance().mService.sendCommingPhoneNumber();
//
//			} else {
//				// MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMING_PHONE_NUBER;
//				// MyApp.getIntance().mService.connect(MyApp.getIntance()
//				// .getAppSettings().LAST_CONNECT_MAC.getValue()
//				// .toString());
//			}
//			Log.i("sms", "CallReceiver Phone Number : " + getContactNameByPhoneNumber(context, phoneNr));
//		}
//		// 设置一个监听器
//		// }

	}

	/*
	 * 2 * 根据电话号码取得联系人姓名 3
	 */
	public static String getContactNameByPhoneNumber(Context context, String address) {
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };

		// 将自己添加到 msPeers 中
		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, // Which columns to return.
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + address + "'", // WHERE clause.
				null, // WHERE clause value substitution
				null); // Sort order.

		if (cursor == null) {
			Log.d(TAG, "getPeople null");
			return null;
		}
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);

			// 取得联系人名字
			int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
			String name = cursor.getString(nameFieldColumnIndex);
			return name;
		}
		return null;
	}

	private int readMissCall(Context context) {
		int result = 0;
		Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] { Calls.TYPE }, " type=? and new=?",
				new String[] { Calls.MISSED_TYPE + "", "1" }, "date desc");

		if (cursor != null) {
			result = cursor.getCount();
			SharedPreferences share = context.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
			share.edit().putInt(SystemConfig.KEY_UNREADPHONE, result).commit();
			if (MyApp.getIntance().mService.mConnectionState == MyApp.getIntance().mService.STATE_CONNECTED) {
				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.UNREADPHONE_NUM;
				MyApp.getIntance().mService.sendUnReadPhoneCount();

			} else {
				// MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.UNREADPHONE_NUM;
				// MyApp.getIntance().mService.connect(MyApp.getIntance()
				// .getAppSettings().LAST_CONNECT_MAC.getValue()
				// .toString());
			}
			;
			Log.i(TAG, "mMissCallCount>>>>" + result);
			cursor.close();
		}
		return result;
	}

}
