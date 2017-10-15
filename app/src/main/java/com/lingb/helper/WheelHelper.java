package com.lingb.helper;

import java.util.Arrays;
import java.util.List;

import android.view.View;

import kankan.wheel.widget.WheelView;

public class WheelHelper {

	/**
	 * 设置wheel的currentItem
	 * @param value
	 * @param Array
	 * @param wheel
	 */
	public static void setWheelCurrentItem(String value, String[] Array, WheelView wheel) {
		if (value != null && Array != null && wheel != null) {
			List<String> list = Arrays.asList(Array);
			int index = list.indexOf(value);
			System.out.println("value:" + value + ", index:" + index);
			if (index != -1) {
				wheel.setCurrentItem(index);
			} else {
				wheel.setCurrentItem(0);
			}
			
		} else {
			
		}
	}
	
	/**
	 * 显示
	 * @param view_wheel
	 * @param wheel
	 */
	public static void showWheel(View view_wheel) {
		if (view_wheel != null && !view_wheel.isShown()) {
			view_wheel.setVisibility(View.VISIBLE);

		}
	}
	
	
	/**
	 * 隐藏
	 * @param view_wheel
	 */
	public static void hideWheel(View view_wheel) {
		if (view_wheel != null && view_wheel.isShown()) {
			view_wheel.setVisibility(View.GONE);
			
		}
	}
}
