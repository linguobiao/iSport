package com.lingb.helper;

import java.util.Calendar;

public class CalendarHelper {

	/**
	 * 格式 yyyy.mm.dd hh:mm:ss
	 * @param cal
	 * @return
	 */
	public static Calendar setSecondFormat(Calendar cal) {
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal;
	}
	
}
