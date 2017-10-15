package com.cn.zhihengchuang.walkbank.util;

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
