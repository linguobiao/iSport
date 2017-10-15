package com.lingb.helper;

import android.content.Context;
import android.util.Log;

import com.cn.zhihengchuang.walkbank.util.FormatTransfer;
import com.lingb.global.Global;
import com.lingb.ride.bean.Profile;
import com.lingb.ride.database.DatabaseProvider;
import com.lingb.ride.settings.RideUserActivity;

public class ParserHelper {
	
	public final static String KEY_WHEEL = "KEY_WHEEL";
	public final static String KEY_WHEEL_TIME = "KEY_WHEEL_TIME";
	public final static String KEY_PEDAL = "KEY_PEDAL";
	public final static String KEY_PEDAL_TIME = "KEY_PEDAL_TIME";

	public static double[] ParserData(Context context, byte[] datas) {
		int size = Global.TIRE_L[RideUserActivity.DEF_SIZE];
		Profile profile = DatabaseProvider.queryProfile(context, RideUserActivity.DEF_NAME);
		if (profile != null) {
			int item = profile.getSize();
			if (item == Global.TIRE_L.length) {
				size = ProfileHelper.getTireSize();
			} else {
				size = Global.TIRE_L[profile.getSize()];
				
			}
		}
		double[] result = new double[2];
		long wheel = (formatToInt(datas[4]) << 24) | (formatToInt(datas[3]) << 16) | (formatToInt(datas[2]) << 8) | (formatToInt(datas[1]));
		int wheelTime = (formatToInt(datas[6]) << 8) | (formatToInt(datas[5]));
		int pedal = (formatToInt(datas[8]) << 8) | (formatToInt(datas[7]));
		int pedalTime = (formatToInt(datas[10]) << 8) | (formatToInt(datas[9]));
		
		long wheelLast = SpHelper.getLong(KEY_WHEEL, -1);
		int wheelTimeLast = SpHelper.getInt(KEY_WHEEL_TIME, -1);
		int pedalLast = SpHelper.getInt(KEY_PEDAL, -1);
		int pedalTimeLast = SpHelper.getInt(KEY_PEDAL_TIME, -1);
		
//		Log.i("sync", "**** wheel:" + wheel + ",  " + wheelTime + ",  pedal = " + pedal + ",  " + pedalTime);
//		Log.i("sync", "#### wheel:" + wheelLast + ",  " + wheelTimeLast + ",  pedal = " + pedalLast + ",  " + pedalTimeLast);
//		Log.i("sync", "#### wheel:" + wheel + ",  time:" +wheelTime);
//		Log.i("sync", "#### :" + Arrays.toString(datas));
//		Log.i("sync", "#### :" + datas[5] + ",  " + datas[6]);

		double speed = 0;
		double cadence = 0;
		
		if (wheelLast != -1 && wheelTimeLast != -1 && pedalLast != -1 && pedalTimeLast != -1) {
			if ((wheelTime - wheelTimeLast) > 0) {
			Log.i("sync", "#### wheel:" + wheel + ",  time:" +wheelTime + ",  " + (wheel - wheelLast) + ",  " + (wheelTime - wheelTimeLast));
				speed = (double)(wheel - wheelLast) * ((double)size / 1000 / 1000) / ((double)(wheelTime - wheelTimeLast) / 1024 / 3600);
				if (speed < 0) {
					speed = 0;
				}
			} 
			
			if ((pedalTime - pedalTimeLast) > 0) {
//				Log.i("sync", "#### wheel:" + pedal + ",  time:" +pedalTime + ",  " + (pedal - pedalLast) + ",  " + (pedalTime - pedalTimeLast));
				cadence = (double)(pedal - pedalLast) / ((double)(pedalTime - pedalTimeLast) / 1024 / 60);
				if (cadence < 0) {
					cadence = 0;
				}
			}
		}
		
		
		result[0] = speed;
		result[1] = cadence;
		
		SpHelper.putlong(KEY_WHEEL, wheel);
		SpHelper.putInt(KEY_WHEEL_TIME, wheelTime);
		SpHelper.putInt(KEY_PEDAL, pedal);
		SpHelper.putInt(KEY_PEDAL_TIME, pedalTime);
		
		return result;
	}
	
	/**
	 * 计算最大值
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static double parserMaxValue(double value, double valueMax) {
		if (value > valueMax) {
			return value;
		} else {
			return valueMax;
		}
	}
	
	/**
	 * 计算最小值
	 * @param value
	 * @param valueMin
	 * @return
	 */
	public static double parserMinValue(double value, double valueMin) {
		if (value < valueMin) {
			return value;
		} else {
			return valueMin;
		}
	}


	
	/**
	 * 计算出当前值，若有连续的3个0值。才取0。否则维持上一个非0值
	 * @param array
	 * @param value
	 * @return
	 */
	public static double parserCurrentValue(double[] array, double value) {
		double value1 = array[1];
		double value2 = array[2];
		double value3 = array[3];
		
		array[0] = value1;
		array[1] = value2;
		array[2] = value3;
		array[3] = value;
		
		double result = 0;
		if (array[3] > 0 && array[3] < 300) { 
			result = array[3];
		} else if (array[2] > 0 && array[2] < 300) {
			result = array[2];
		} else if (array[1] > 0 && array[1] < 300) {
			result = array[1];
		} else if (array[0] > 0 && array[0] < 300){
			result = array[0];
		}
		return result;
	}

	private static int formatToInt(Byte b) {
		String sHH = FormatTransfer.toHexString(b);
		int iHH = Integer.parseInt(sHH, 16);
		return iHH;
	}

}
