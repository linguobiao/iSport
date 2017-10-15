package com.lingb.helper;

import java.math.BigDecimal;

import android.util.Log;

public class CalculateHelper {
	
	public static double cmToInch(double cm) {
		return cm * 0.39370078740157;
	}
	
	
	public static double inchToCm(double inch) {
		return 2.54 * inch;
	}
	
	
	/**
	 * kg 转换成 lbs
	 * @param kg
	 * @return
	 */
	public static double kgToLbs(double kg) {
		return kg / 0.4536;
	}
	
	/**
	 * lbs 转换成 kg
	 * @param lbs
	 * @return
	 */
	public static double LbsToKg(double lbs) {
		return lbs * 0.4536;
	}
	
	
	public static double kmToMile(double km) {
		return km / 1.609;
	}
	
	
	public static double mileToKm(double mile) {
		return mile * 1.609;
	}
	
	/**
	 * 将byte数值转化为int
	 * @param title
	 * @return
	 */
	public static int getByteValue(byte title) {
		int value = title;
		if (title < 0) {
			value += 256;
		}
		
		return value;
	}
	
	public static double parserDouble(double value, int score, int type) {
		Log.i("tests", "value = " + value);
		BigDecimal b = new BigDecimal(value);  
		Log.i("tests", "value**** = " + b.setScale(score,   type).doubleValue());
		return  b.setScale(score,   type).doubleValue();  
	}
	

	
}
