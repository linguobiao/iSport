package com.cn.zhihengchuang.walkbank.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class UiTools {

	
	

	/**
	 * ������ͨ�绰
	 * */
	public static void makeCallByLocal(Activity act, String phoneNumber) {
		try {
			String tmpPN = null;
			int index = phoneNumber.indexOf('(');
			if (index != -1)
				tmpPN = phoneNumber.substring(0, index).trim();
			else
				tmpPN = phoneNumber;

			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ tmpPN));
			act.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���������
	 * 
	 * @param act
	 */
	public static void hideKeyboard(Activity act) {
		try {
			InputMethodManager imm = (InputMethodManager) act
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			View view = act.getCurrentFocus();
			if (view != null) {
				imm.hideSoftInputFromWindow(view.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ת��Ŀ�����
	 * 
	 * @param curr
	 *            ��ǰ����
	 * @param targetActivity
	 *            Ŀ�����
	 * @param data
	 *            ��������
	 * @param finish
	 *            ���Ϊtrue���رյ�ǰ���档���򲻹ر�
	 */
	public static void forwardTargetActivity(Context context,
			Class targetActivity, Bundle data, boolean finish) {
		try {
			Intent intent = new Intent();
			intent.setClass(context, targetActivity);
			if (data != null)
				intent.putExtras(data);

			context.startActivity(intent);
			if (finish && context instanceof Activity)
				((Activity) context).finish();

		} catch (Exception e) {
			Log.e(context.getClass(), e);
		}
	}

	/**
	 * ��ת��Ŀ�����
	 * 
	 * @param curr
	 *            ��ǰ����
	 * @param targetActivity
	 *            Ŀ�����
	 * @param data
	 *            ��������
	 * @param finish
	 *            ���Ϊtrue���رյ�ǰ���档���򲻹ر�
	 */
	@SuppressWarnings("unchecked")
	public static void forwardTargetActivityForResult(Context curr,
			Class targetActivity, Bundle data, boolean finish, int requestCode) {
		try {
			Intent intent = new Intent();
			intent.setClass(curr, targetActivity);
			if (data != null)
				intent.putExtras(data);

			((Activity) curr).startActivityForResult(intent, requestCode);
			if (finish)
				((Activity) curr).finish();

		} catch (Exception e) {
			Log.e(curr.getClass(), e);
			// Toast.makeText(curr, "�޷���ת", Toast.LENGTH_LONG);
		}
	}

	public static void showAlert(Context context, String message) {
		try {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e(UiTools.class, e);
		}
	}
	
	public static void showAlert(Context context, int message) {
		try {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e(UiTools.class, e);
		}
	}
}
