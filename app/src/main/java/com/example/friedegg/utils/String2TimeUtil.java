package com.example.friedegg.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by CRZ on 2016/5/25 18:06.
 * 用于将String转化为时间
 */
public class String2TimeUtil {
    /**
     * 转换日期格式到用户体验好的时间格式
     * @param time 2015-04-11 12:45:06
     * @return string
     */
    public static String dateString2GoodExperienceFormat(String time) {
        if (isNullString(time)) {
            return "";
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
            try {
                String timeString;
                Date parse = simpleDateFormat.parse(time);
                long distanceTime = new Date().getTime() - parse.getTime();
                if (distanceTime < 0L) {
                    timeString = "0 mins ago";
                } else {
                    long n2 = distanceTime / 60000L;
                    new SimpleDateFormat("HH:mm",Locale.CHINA);
                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM-dd",Locale.CHINA);
                    if (n2 < 60L) {
                        timeString = String.valueOf(n2) + " mins ago";
                    } else if (n2 < 720L) {
                        timeString = String.valueOf(n2 / 60L) + " hours ago";
                    } else {
                        timeString = simpleDateFormat2.format(parse);
                    }
                }
                return timeString;
            } catch (Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
    }

    public static boolean isNullString(String s) {
        return s == null || s.equals("") || s.equals("null");
    }
}
