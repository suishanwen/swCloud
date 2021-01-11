package com.sw.note.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
    public static String getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat localDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
        localDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return localDate.format(cal.getTime());
    }
}
