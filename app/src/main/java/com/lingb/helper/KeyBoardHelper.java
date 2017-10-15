package com.lingb.helper;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyBoardHelper {
	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);  
		Log.i("key", "imm = " + imm);
		if (imm != null) {
			View focus = activity.getCurrentFocus();
			Log.i("key", "focus = " + focus);
			if (focus != null) {
				IBinder ib = focus.getWindowToken();
				Log.i("key", "ib = " + ib);
				if (ib != null) {
					Log.i("key", "hide");
					imm.hideSoftInputFromWindow(ib, 0); 
				}
			}
		}	
	}
	
	public static void hideKeyboard3(Activity activity, View view) {
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);  
		if (imm != null) {
			if (view != null) {
				IBinder ib = view.getWindowToken();
				if (ib != null) {
					imm.hideSoftInputFromWindow(ib, 0); 
				}
			}
		}	
	}
	
	public static void hideKeyboard2(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		Log.i("key", "isOpen" + isOpen);
	    if (isOpen) {

	        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
	    	imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  

	    }
	}
	
}
