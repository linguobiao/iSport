/**
 * 
 */
package com.cn.zhihengchuang.walkbank.util;

/**
 * Log
 * <p>
 * </p>
 * 
 * @author Doni Chen
 * @createDate 2011-8-21
 */
public class Log {
	public final static String LOGTAG = "Pedometer";

	public static final boolean DEBUG = true;

	public static final boolean PRINT_STACK_TRACE = true;

	public static void v(String msg) {
		if (DEBUG&&msg != null)
			android.util.Log.v(LOGTAG, msg);
	}

	public static void i(String msg) {
		if (DEBUG&&msg != null)
			android.util.Log.i(LOGTAG, msg);
	}

	public static void e(String msg) {
		if (DEBUG && msg != null)
			android.util.Log.e(LOGTAG, msg);
	}

	public static void e(Class c, Exception e) {
		if (c == null && e == null)
			return;

		if (e == null) {
			android.util.Log.e(LOGTAG + c.getName(),
					"Exception Object is null!");
		} else {
			String msg = e.getMessage();
			if (msg == null)
				android.util.Log.e(LOGTAG + c.getName(),
						"Exception Object is null!");
			else {
				android.util.Log.e(LOGTAG + c.getName(), msg);
				if (PRINT_STACK_TRACE)
					e.printStackTrace();
			}

			msg = null;
		}
	}

	public static void e(Class c, String msg) {
		if (DEBUG&& c == null && msg == null)
			return;

		if (msg == null)
			android.util.Log.e(LOGTAG + c.getName(), "msg Object is null!");
		else
			android.util.Log.e(LOGTAG + c.getName(), msg);
	}

	public static void d(String msg) {
		if (DEBUG && msg != null)
			android.util.Log.d(LOGTAG, msg);
	}
}
