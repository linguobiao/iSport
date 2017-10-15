package com.lingb.helper;

import java.util.Timer;

public class TimerHelper {

	public static void cancelTimer(Timer timer) {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}
}
