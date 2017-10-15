package com.cn.zhihengchuang.walkbank.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
/**
 * 
 * @author longke
 * 时间转化工具类
 *
 */

public class Tools {
	/**
	 * 判断是否存在SDCard
	 * 
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * �������벢ȡ��
	 * */
	public static int roundHalfUp(double value) {
		return new BigDecimal(value).setScale(0, BigDecimal.ROUND_HALF_UP)
				.intValue();
	}

	public static String longToDateStr(String lstr) {
		if (Tools.isEmpty(lstr))
			return "";

		long ld = Tools.parseLong(lstr);
		if (ld <= 0)
			return "";

		try {
			Date date = new Date(ld);
			return Tools.defaultLongDateFormat(date);
		} catch (Exception e) {
			Log.e(Tools.class, e);
		}

		return "";
	}

	// ���Ƕ�ת��Ϊ����
	public static double deg2rad(double degree) {
		return degree / 180 * Math.PI;
	}

	// ������ת��Ϊ�Ƕ�
	public static double rad2deg(double radian) {
		return radian * 180 / Math.PI;
	}

	public static double getDistance2(double paramDouble1, double paramDouble2,
			double paramDouble3, double paramDouble4) {
		double d1 = 3.141592653589793D * (paramDouble2 - paramDouble1) / 180.0D;
		double d2 = 3.141592653589793D * (paramDouble4 - paramDouble3) / 180.0D;
		double d3 = Math.sin(d1 / 2.0D) * Math.sin(d1 / 2.0D)
				+ Math.cos(3.141592653589793D * paramDouble1 / 180.0D)
				* Math.cos(3.141592653589793D * paramDouble2 / 180.0D)
				* Math.sin(d2 / 2.0D) * Math.sin(d2 / 2.0D);
		return 6371.0D * (2.0D * Math
				.atan2(Math.sqrt(d3), Math.sqrt(1.0D - d3))) * 1.609344D;
	}

	public static double getDistance(double lat1, double lon1, double lat2,
			double lon2) {
		float[] results = new float[1];
		Location.distanceBetween(lat1, lon1, lat2, lon2, results);
		return results[0];
	}

	public static String dateFormatForStartDate(Date date) {
		return formatDate(date, "yyyy-MM-dd 00:00:00");
	}

	// public static String dateFormatForStartDate(Date date) {
	// return formatDate(date, "yyyy-MM-dd 00:00");
	// }

	public static String dateFormatForEndDate(Date date) {
		return formatDate(date, "yyyy-MM-dd 23:59:59");
	}

	// public static String dateFormatForEndDate(Date date) {
	// return formatDate(date, "yyyy-MM-dd 23:59");
	// }

	public static String defaultLongDateFormat(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	// public static String defaultLongDateFormat(Date date) {
	// return formatDate(date, "yyyy-MM-dd HH:mm");
	// }

	public static String defaultDateFormat(Date date) {
		return formatDate(date, "yyyy-MM-dd");
	}

	public static String defaultShortDateFormat(Date date) {
		return formatDate(date, "yyyy-MM");
	}
	
	public static String time24Totime12(String time){
		String[] times = time.split(":");
		int hour = Integer.valueOf(times[0]);
		if (hour >= 13){
			hour = hour - 12;
			time = String.format("%02d", hour) + ":" + times[1] + "PM";
		}else if (hour == 0){
			time = 12 + ":" + times[1] + "AM";
		}else if (hour == 12){
			time = 12 + ":" + times[1] + "PM";
		} else {
			time = String.format("%02d", hour) + ":" + times[1] + "AM";
		}
		return time;
	}

	public static GregorianCalendar StringToCalender(String date) {
		GregorianCalendar c = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sdf.parse(date);
			c.setTime(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c;
	}

	// ����ת����XСʱX����
	public static String minuteToHoutMinute(int minute) {
		String time = "0h 0min";
		if (minute != 0) {
			int shour = minute / 60;
			int sminute = minute % 60;
			time = shour + "h " + sminute + "min";
		}
		return time;
	}

	//
	public static String minuteToSleepTime(int minute, int unit) {
		String time = "N/A";
		int shour = minute / 60 + 12;
		int sminute = minute % 60;
		if (shour > 23) {
			shour -= 24;
		}
		if (unit == 1) {
//			if (shour < 12) {
//				time = String.format("%02d", shour) + ":"
//						+ String.format("%02d", sminute) + "AM";
//			} else {
//				time = String.format("%02d", (shour - 12)) + ":"
//						+ String.format("%02d", sminute) + "PM";
//			}
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, shour);
			c.set(Calendar.MINUTE, sminute);
			time = timeTo12HourFormar(c);
			
		} else {
			time = String.format("%02d", shour) + ":"
					+ String.format("%02d", sminute);
		}
		return time;
	}

	public static String formatDate(Date date, String format) {
		String result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			result = sdf.format(date);
		} catch (Exception e) {

		}

		return result;
	}
	
	public static Date formatDate(String date,String format){
		Date d = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			d = sdf.parse(date);
		} catch (Exception e) {

		}		
		return d;
	}

	public static String formatDateUS(Date date, String format) {
		String result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format,
					Locale.getDefault());
			result = sdf.format(date);
		} catch (Exception e) {

		}

		return result;
	}

	public static String defaultShortDateUSFormat(Date date) {
		return formatDateUS(date, "dd MMM.,yyyy");
	}
	
	public static String defaultBirthDayFormat(Date date) {
		return formatDateUS(date, "dd MMM yyyy");
	}

	public static String defaultMonthDateUSFormat(Date date) {
		return formatDateUS(date, "MMM.,yyyy");
	}

	public static String defaultYearDateUSFormat(Date date) {
		return formatDateUS(date, "yyyy");
	}
	
	public static String defaultDayMonthUsFormat(Calendar c){
		String startDate = formatDateUS(c.getTime(), "dd MMM.");
		Calendar cc = Calendar.getInstance();
		cc.setTime(c.getTime());
		cc.add(Calendar.DAY_OF_MONTH, +1);
		String endDate = formatDateUS(cc.getTime(), "dd MMM.");
		return startDate+"-"+endDate;
	}
	
	public static String timeTo12HourFormar(Calendar c){
		return formatDateUS(c.getTime(), "hh:mmaa");
		
	}

	public static String defaultWeekDateUSFormat(Date date) {
		GregorianCalendar g = new GregorianCalendar();
		g.setTime(date);
		int week = g.get(Calendar.WEEK_OF_YEAR);// �������
		int year = g.get(Calendar.YEAR);

		StringBuffer text = new StringBuffer();
		String country = Locale.getDefault().getLanguage();
		if (country.equals("zh")) {
			text.append(year).append("��").append("��").append(week).append("��");
			return text.toString();
		} else if (country.equals("es")) {
			StringBuffer index = new StringBuffer();
			if (week == 1 || week == 3)
				index.append("ra ");
			else if (week == 2) {
				index.append("da ");
			} else {
				index.append("a ");
			}
			text.append(week).append(index).append("semana,").append(year);
			return text.toString();
		} else if (country.equals("fr")) {
			StringBuffer index = new StringBuffer();
			if (week == 1)
				index.append("��re ");
			else if (week == 2) {
				index.append("nde ");
			} else {
				index.append("��me ");
			}
			text.append(week).append(index).append("semaine,").append(year);
			return text.toString();
		}
		if (week>10 && week < 20){
			return formatDateUS(date, "w'th Week',yyyy");
		}
		if (week % 10 == 1) {
			return formatDateUS(date, "w'st Week',yyyy");
		} else if (week % 10 == 2) {
			return formatDateUS(date, "w'nd Week',yyyy");
		} else if (week % 10 == 3) {
			return formatDateUS(date, "w'rd Week',yyyy");
		} else {
			return formatDateUS(date, "w'th Week',yyyy");
		}
	}

	// public static String defaultWeekDateUSFormat2(Date date) {
	// String dateString = formatDateUS(date, "w'th Week',yyyy");
	// if (dateString != null){
	//
	// }
	//
	// }

	public static String defaultWeekDateUSFormat(Date date, int week) {
		return formatDateUS(date, week + "'th Week',yyyy");
	}

	/***
	 * ��ȡԲ��ͼƬ
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,
			final float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		// Log.d(bitmap.getWidth() + "-----------------" + bitmap.getHeight());

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

		return baos.toByteArray();
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, final float roundPx,
			final int w, final int h) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();

		float scaleWidht = 0.0f;
		float scaleHeight = 0.0f;

		Bitmap newbmp = null;
		if (width > w && height > h) {
			scaleWidht = ((float) w / width);
			scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, (int) scaleWidht,
					(int) scaleHeight, matrix, true);
		} else {
			scaleWidht = width;
			scaleHeight = height;

			newbmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
					Config.ARGB_8888);
		}

		Log.d(scaleWidht + "--------------" + scaleHeight);

		Canvas canvas = new Canvas(newbmp);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return newbmp;
	}

	public static boolean isChinese(String str) {
		if (str == null || str.trim().equals(""))
			return false;

		int ch = str.charAt(0);

		return (ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122) || ch >= 128;
	}

	public static char getFirstLetters(String str) {

		char word = str.charAt(0);

		// String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
		// if (pinyinArray != null) {
		// word = pinyinArray[0].charAt(0);
		// }

		return Character.toUpperCase(word);
	}

	public static String getdays(long callTime) {
		long DAY = 1440;
		String value = "";

		long nowTime = System.currentTimeMillis();// new Date().getTime();
		long st = 0L;
		if (callTime > nowTime)
			st = nowTime;
		else
			st = nowTime - callTime;

		long duration = st / (1000 * 60);
		if (duration == 0) {
			st = st / 1000;
			value = st + "��ǰ";
		} else if (duration < 60) {
			value = duration + "����ǰ";
		} else if (duration >= 60 && duration < 1440) {

			String t1 = Tools.defaultDateFormat(new Date(callTime));
			String t2 = Tools.defaultDateFormat(new Date(nowTime));

			if (t1 != null && t2 != null && t1.equals(t2)) {
				value = Tools.formatDate(new Date(callTime), "HH:mm");
			} else {
				value = Tools.formatDate(new Date(callTime), "���� HH:mm");
			}

		} else if (duration >= DAY && duration < DAY * 2) {
			value = Tools.formatDate(new Date(callTime), "���� HH:mm");
		} else {
			value = Tools.formatDate(new Date(callTime), "M��dd�� HH:mm");
		}

		return value;
	}

	// ����ʱ��
	public static String formatDuring(long ms) {
		// ����ms��ת�����������Ȼ����תΪ΢��
		long tmp = ms;// * 1000L;

		final int ss = 1000;
		final int mi = ss * 60;
		final int hh = mi * 60;
		final int dd = hh * 24;

		long day = tmp / dd;
		long hour = (tmp - day * dd) / hh;
		long minute = (tmp - day * dd - hour * hh) / mi;
		long second = (tmp - day * dd - hour * hh - minute * mi) / ss;

		if (hour == 0 && minute == 0)
			return second + " ��";
		else if (hour == 0 && minute != 0)
			return minute + " �� " + second + " ��";
		else
			return hour + " ʱ  " + minute + " ��  " + second + " ��";
	}

	public static String toMd5(byte[] bytes) {

		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(bytes);

			return toHexString(algorithm.digest(), "");
		} catch (Exception e) {
			Log.e(Tools.class, e);
		}

		return null;
	}

	public static String toHexString(byte[] bytes, String separator) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			hexString.append(Integer.toHexString(0xFF & b)).append(separator);
		}
		return hexString.toString();
	}

	public static String encodeUnicode(final String gbString) {
		char[] utfBytes = gbString.toCharArray();
		String unicodeBytes = "";
		for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
			String hexB = Integer.toHexString(utfBytes[byteIndex]);
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexB;
		}
		return unicodeBytes;
	}

	/**
	 * ��unicode�ַ���ת��������
	 * 
	 * @param str
	 *            unicode�ַ���
	 * @return ����
	 */
	public static String unicodeToString(String str) {
		if (str == null)
			return "";

		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}
		return str;
	}

	/**
	 * �Ѳ���STRת����INT
	 * 
	 * @param str
	 * @return ת�����ɹ����أ�1
	 */
	public static int parseInt(String str) {
		return parseInt(str, -1);
	}

	public static int parseInt(String str, int defaultStr) {
		try {
			if (str == null)
				return defaultStr;
			return Integer.parseInt(str);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return defaultStr;
	}

	public static double parseDouble(String str) {
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return 0.0d;
	}

	public static long parseLong(String str) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * �жϲ���str�Ƿ�Ϊ�ջ�null
	 * 
	 * @return ���Ϊ�ջ�null������true�����򷵻�false
	 * */
	public static boolean isEmpty(String str) {
		return str == null ? true : str.trim().equals("");
	}

	/**
	 * ���ֽ�����(���ݳ���Ϊ2)ת��Ϊint
	 * 
	 * @param res
	 *            �ֽ�����(���ݳ���Ϊ2)
	 * @return int����
	 */
	public static int byteToInt(byte[] res) {
		if (res == null && res.length < 2)
			return 0;

		return ((res[0] & 0xff) << 8) | (res[1] & 0xff);
	}

	/**
	 * ��INT���͵������������ֽڱ�ʾ
	 * 
	 * @param res
	 *            INT���͵�����
	 * @return �ֽ�����
	 */
	public static byte[] intToByte(int res) {
		byte[] b = new byte[2];
		b[0] = (byte) (0xff & (res >> 8));
		b[1] = (byte) (0xff & res);
		return b;
	}

	/**
	 * ��ȡ��ǰSIM�����ֻ�����
	 * 
	 * @param service
	 *            TelephonyManager����
	 * @return SIM�����ֻ�����
	 */
	public static int getSIMStatus(Object service) {
		try {
			if (service instanceof TelephonyManager) {
				TelephonyManager telMgr = (TelephonyManager) service;
				// SIM_STATE_READY ����
				// SIM_STATE_ABSENT ��SIM��
				// SIM����������δ֪��״̬
				int sims = telMgr.getSimState();
				Log.d("getSIMStatus----------------->" + sims);

				return sims;
			}
		} catch (Exception e) {
			Log.e(Tools.class.getClass(), e);
		}
		return -1;
	}

	/**
	 * ��ȡ��ǰSIM�����ֻ�����
	 * 
	 * @param service
	 *            TelephonyManager����
	 * @return SIM�����ֻ�����
	 */
	public static String getSIMPhoneNumber(Object service) {
		try {
			if (service instanceof TelephonyManager) {
				TelephonyManager telMgr = (TelephonyManager) service;
				int state = telMgr.getSimState();
				if (state == TelephonyManager.SIM_STATE_READY) {
					return telMgr.getLine1Number();
				}
			}
		} catch (Exception e) {
			Log.e(Tools.class.getClass(), e);
		}
		return null;
	}

	/**
	 * ��ȡ��ǰSIM����IMIE����
	 * 
	 * @param service
	 *            TelephonyManager����
	 * @return SIM����IMIE����
	 */
	public static String getSIMIMIE(Object service) {
		try {
			if (service instanceof TelephonyManager) {
				TelephonyManager telMgr = (TelephonyManager) service;
				// int state = telMgr.getSimState();
				// if (state == TelephonyManager.SIM_STATE_READY) {
				return telMgr.getDeviceId();
				// }
			}
		} catch (Exception e) {
			Log.e(Tools.class.getClass(), e);
		}
		return null;
	}

	/**
	 * ��ȡ��ǰSIM����IMSI����
	 * 
	 * @param service
	 *            TelephonyManager����
	 * @return SIM����IMSI����
	 */
	public static String getSIMIMSI(Object service) {
		try {
			if (service instanceof TelephonyManager) {
				TelephonyManager telMgr = (TelephonyManager) service;
				// int state = telMgr.getSimState();
				// Log.d("getSIMIMSI----------------->" + state);
				// if (state == TelephonyManager.SIM_STATE_READY) {
				return telMgr.getSubscriberId();
				// }
			}
		} catch (Exception e) {
			Log.e(Tools.class.getClass(), e);
		}
		return null;
	}

	/**
	 * ��鵱ǰ�����Ƿ�����
	 * 
	 * @param service
	 * @return �����������������true�������෴��
	 */
	public static boolean isNetworkConnected(Context context) {
		boolean result = false;

		try {
			ConnectivityManager mConnectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			TelephonyManager mTelephony = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			// ����������ӣ������������ã��Ͳ���Ҫ��������������
			NetworkInfo info = mConnectivity.getActiveNetworkInfo();
			if (info == null || !mConnectivity.getBackgroundDataSetting()) {
				return result;
			}

			// �ж������������ͣ�ֻ����3G��wifi�����һЩ���ݸ��¡�
			int netType = info.getType();
			int netSubtype = info.getSubtype();
			if (netType == ConnectivityManager.TYPE_WIFI) {
				return info.isConnected();
			} else if (netType == ConnectivityManager.TYPE_MOBILE
					&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
					&& !mTelephony.isNetworkRoaming()) {
				return info.isConnected();
			}

		} catch (Exception e) {
			Log.e(context.getClass(), e);
		}

		return result;
	}

	/***
	 * ���Ͷ���
	 * 
	 * @param to
	 *            �������ֻ�����
	 * @param content
	 *            ��������
	 * @param context
	 *            �����Ķ���
	 */
	public static void sendSMS(String to, String content, Context context) {
		SmsManager sms = SmsManager.getDefault();
		// PendingIntent pt = PendingIntent.getBroadcast(context, 0, new
		// Intent(),0);
		sms.sendTextMessage(to, null, "content", null, null);
	}

	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.hearbook", 0).versionCode;
			Log.d("verCode-------------->" + verCode);
		} catch (NameNotFoundException e) {
			Log.e(context.getClass(), e);
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.hearbook", 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(context.getClass(), e);
		}
		return verName;
	}

	public static Object readAiFromFile(String filename) {
		Object ais = null;
		try {
			FileInputStream fin = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fin);
			ais = ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ais;

	}

	public static void writeAiToFile(String filename, Object ais) {
		try {
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(ais);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String toHexString(byte[] b) { // String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String EncoderByMd5(String str) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(str.getBytes());
			byte messageDigest[] = digest.digest();

			return toHexString(messageDigest);
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";

	}


}