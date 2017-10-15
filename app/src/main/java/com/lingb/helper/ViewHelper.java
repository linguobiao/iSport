package com.lingb.helper;

import java.text.DecimalFormat;

import com.lingb.global.Global;

import android.widget.TextView;

public class ViewHelper {
	
	public static void setText(TextView textView, Double text, DecimalFormat df, int unit) {
		
		if (unit == Global.TYPE_UNIT_IMPERIAL) {
			text = CalculateHelper.kmToMile(text);
		} 
		if (df != null) {
			textView.setText(df.format(text).replace(",", "."));
		} else {
			textView.setText(String.valueOf(text).replace(",", "."));
		}
		
	}

	public static void setText(TextView textView, Double text, DecimalFormat df, int unit, String unitM, String unitI) {
		String unitStr = unitM;
		if (unit == Global.TYPE_UNIT_IMPERIAL) {
			text = CalculateHelper.kmToMile(text);
			unitStr = unitI;
		} else {
			
		}
		if (df != null) {
			textView.setText(df.format(text).replace(",", ".") + "  " + unitStr);
		} else {
			textView.setText(String.valueOf(text).replace(",", ".") + "  " + unitStr);
		}
		
	}
}
