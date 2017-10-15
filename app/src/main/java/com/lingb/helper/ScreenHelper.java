package com.lingb.helper;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenHelper {

	
	 /** 
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp==dip
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    
    public static int getDensityDpi(Activity context) {
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
	    int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）	
	    
		return densityDpi;
	}
    
    /**
	 * 获取屏幕宽度
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Activity context) {
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
	    int width = metric.widthPixels;	
	    
	    return width;
	}
}
