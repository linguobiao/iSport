package com.cn.zhihengchuang.walkbank.activity.call;

import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.SystemConfig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallListener extends PhoneStateListener {
	private boolean isHandup = false;
	private boolean isCalling = false;
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // 空闲
				Log.i(TAG, "phone 空闲");
				if (isHandup && !isCalling) {
					Log.i(TAG, "来电终止");
					isHandup = false;

				}
				isCalling = false;
				break;
			case TelephonyManager.CALL_STATE_RINGING: // 来电
				Log.i(TAG, "phone 来电 " + incomingNumber);
				isHandup = true;
				
				if (incomingNumber != null && !incomingNumber.equals("")) {
					SharedPreferences share = context.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
					share.edit().putString(SystemConfig.KEY_COMMING_PHONE, incomingNumber).commit();
					share.edit().putString(SystemConfig.KEY_COMMING_PHONE_NAME, getContactNameByPhoneNumber(context, incomingNumber)).commit();
					if (MyApp.getIntance().mService.mConnectionState == MyApp.getIntance().mService.STATE_CONNECTED) {
						MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMING_PHONE_NUBER;
						MyApp.getIntance().mService.sendCommingPhoneNumber();

					} else {
						
					}
					Log.i("sms", "CallReceiver Phone Number : " + getContactNameByPhoneNumber(context, incomingNumber));
				}
/////////////////////////////////////////////////////////////////

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机（正在通话中）
				Log.i(TAG, "phone 通话中");
				if (isHandup) {
					Log.i(TAG, "phone 接通来电");
					isCalling = true;
				}
				break;
			}
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

		try {
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
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		
		return null;
	}
	
	private static final String TAG = "sms";
//	private static int lastetState = TelephonyManager.CALL_STATE_OFFHOOK; // 最后的状态
	private Context context;
//
	public CallListener(Context context) {
		super();
		this.context = context;
	}

//	public void onCallStateChanged(int state, String incomingNumber) {
//		Log.v(TAG, "CallListener call state changed : " + incomingNumber);
//		String m = null;
//		// 如果当前状态为空闲,上次状态为响铃中的话,则认为是未接来电
//		if (lastetState == TelephonyManager.CALL_STATE_RINGING
//				&& state == TelephonyManager.CALL_STATE_IDLE) {
//			sendSmgWhenMissedCall(incomingNumber);
//		}
//		// 最后改变当前值
//		lastetState = state;
//	}
//
//	private void sendSmgWhenMissedCall(String incomingNumber) {
//		// 未接来电处理(发短信,发email等)
//		/*Intent intent=new Intent("com.intent.longke.nothand");
//		intent.putExtra("not_hand", true);
//		context.sendBroadcast(intent);*/
//	}
}
