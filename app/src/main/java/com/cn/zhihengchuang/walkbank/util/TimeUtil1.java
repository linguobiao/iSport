package com.cn.zhihengchuang.walkbank.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import com.isport.trackernew.R;

public class TimeUtil1 {
    public static final Calendar FIRST_DAY_OF_TIME;
    public static final Calendar LAST_DAY_OF_TIME;
    public static final int DAYS_OF_TIME;

    static {
        FIRST_DAY_OF_TIME = Calendar.getInstance();
        FIRST_DAY_OF_TIME.set(1970, Calendar.JANUARY, 1);
        LAST_DAY_OF_TIME = Calendar.getInstance();
        //LAST_DAY_OF_TIME.set(2100, Calendar.DECEMBER, 31);
        DAYS_OF_TIME =(int) ((LAST_DAY_OF_TIME.getTimeInMillis() - FIRST_DAY_OF_TIME.getTimeInMillis()) / (24 * 60 * 60 * 1000))+1;
    }

    /**
     * Get the position in the ViewPager for a given day
     *
     * @param day
     * @return the position or 0 if day is null
     */
    public static int getPositionForDay(Calendar startDay,Calendar currentDay) {
        if (startDay != null) {
        	int day=(int) (startDay.getTimeInMillis() - currentDay.getTimeInMillis())
            / 86400000;
        	if(day%8==0){
        	  return day/8;	
        	}else{
        		return day/8+1;	
        	}
           
        }
        return 1;
    }
    /** 
    *字符串的日期格式的计算 
    */  
        public static int daysBetween(String smdate,String bdate) {  
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
            Calendar cal = Calendar.getInstance();    
            try {
				cal.setTime(sdf.parse(smdate));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
            long time1 = cal.getTimeInMillis();                 
            try {
				cal.setTime(sdf.parse(bdate));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
            long time2 = cal.getTimeInMillis();         
            long between_days=(time2-time1)/(1000*3600*24);  
            int day=Integer.parseInt(String.valueOf(between_days));
            if(day%8==0){
          	  return day/8+1;	
          	}else{
          		return day/8+1;	
          	}    
        }  

    /**
     * Get the day for a given position in the ViewPager
     *
     * @param position
     * @return the day
     * @throws IllegalArgumentException if position is negative
     */
    public static Calendar getDayForPosition(int position) throws IllegalArgumentException {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be negative");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(FIRST_DAY_OF_TIME.getTimeInMillis());
        cal.add(Calendar.DAY_OF_YEAR, position);
        return cal;
    }


    public static String getFormattedDate(Context context, long date) {
        final String defaultPattern = "yyyy-MM-dd";

        String pattern = null;
        if (context != null) {
            pattern = context.getString(R.string.date_format);
        }
        if (pattern == null) {
            pattern = defaultPattern;
        }
        SimpleDateFormat simpleDateFormat = null;
        try {
            simpleDateFormat = new SimpleDateFormat(pattern);
        } catch (IllegalArgumentException e) {
            simpleDateFormat = new SimpleDateFormat(defaultPattern);
        }

        return simpleDateFormat.format(new Date(date));
    }


}
