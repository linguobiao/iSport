package com.cn.zhihengchuang.walkbank.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
	private Context context;
	private Toast toast;

	public ToastUtil(Context context) {
		if (context != null)
			this.context = context;
	}
	
	public void getToast(String str){
		toast = Toast.makeText(this.context, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2,toast.getYOffset() / 2);
		toast.show();
	}

}
