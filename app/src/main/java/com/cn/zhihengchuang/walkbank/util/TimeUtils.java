package com.cn.zhihengchuang.walkbank.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.content.Context;
import com.isport.trackernew.R;
import com.lingb.global.Global;


public class TimeUtils {
    public static final Calendar FIRST_DAY_OF_TIME;
    public static  Calendar LAST_DAY_OF_TIME;
    public static  int DAYS_OF_TIME;

    static {
        FIRST_DAY_OF_TIME = Calendar.getInstance();
        FIRST_DAY_OF_TIME.set(1970, Calendar.JANUARY, 1);
        LAST_DAY_OF_TIME = Calendar.getInstance();
        //LAST_DAY_OF_TIME.set(2100, Calendar.DECEMBER, 31);
        DAYS_OF_TIME =(int) ((LAST_DAY_OF_TIME.getTimeInMillis() - FIRST_DAY_OF_TIME.getTimeInMillis()+ 1000000) / (24 * 60 * 60 * 1000))+1;
    }

    /**
     * Get the position in the ViewPager for a given day
     *
     * @param day
     * @return the position or 0 if day is null
     */
    public static int getPositionForDay(Calendar day) {
        if (day != null) {
        	day.set(Calendar.HOUR_OF_DAY, 0);
        	day.set(Calendar.MINUTE, 0);
        	day.set(Calendar.SECOND, 0);
        	day.set(Calendar.MILLISECOND, 0);
        	FIRST_DAY_OF_TIME.set(Calendar.HOUR_OF_DAY, 0);
        	FIRST_DAY_OF_TIME.set(Calendar.MINUTE, 0);
        	FIRST_DAY_OF_TIME.set(Calendar.SECOND, 0);
        	FIRST_DAY_OF_TIME.set(Calendar.MILLISECOND, 0);
//        	android.util.Log.i("date", "day = " + ((double)day.getTimeInMillis() - (double)FIRST_DAY_OF_TIME.getTimeInMillis())/ 86400000);
        	int result = Integer.parseInt(Global.df_0.format(((double)(day.getTimeInMillis() - (double)FIRST_DAY_OF_TIME.getTimeInMillis())
                    / 86400000 ))
                    );
            return result;
        }
        return 0;
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
