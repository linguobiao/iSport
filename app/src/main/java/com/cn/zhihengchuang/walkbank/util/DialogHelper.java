package com.cn.zhihengchuang.walkbank.util;


import java.lang.reflect.Field;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogHelper {
	
	
	public static ProgressDialog showProgressDialog(Context context, String message) {
		
		ProgressDialog mpDialog = new ProgressDialog(context);  
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
        mpDialog.setMessage(message); 
        
        return mpDialog;
	}
	
	public static ProgressDialog showSelectDialog(Context context, String message) {
		ProgressDialog mpDialog = new ProgressDialog(context);  
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
        mpDialog.setMessage(message);  
        
        return mpDialog;
	}
	
	
	
	
	public static void closeDialogByClickButton(DialogInterface dialog, boolean isClose) {
		try { 
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
			field.setAccessible(true); 
			field.set(dialog, isClose);
			} catch (Exception e) { 
			e.printStackTrace(); 
			}
	}
	public static void dismissDialog(Dialog dialog) {
		if (dialog != null) {
			dialog.dismiss();
		}
	}
	
	
	public static void hideDialog(Dialog dialog) {
		if (dialog != null) {
			dialog.hide();
		}
	}
	
	public static void cancelDialog(Dialog dialog) {
		if (dialog != null) {
			dialog.cancel();
		}
	}
	
	public static ProgressDialog showProgressDialogRect(Context context, String message) {
		ProgressDialog pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage(message);   
        return pd;
		
	}
}

