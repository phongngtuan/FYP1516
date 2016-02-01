package com.ntu.phongnt.healthdroid.graph.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    public static final String RFC3339_TEMPLATE = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
    private static SimpleDateFormat formatRfc3339 = new SimpleDateFormat(RFC3339_TEMPLATE, Locale.getDefault());

    public static Date getDate(String date) {
        try {
            return formatRfc3339.parse(date);
        } catch (ParseException e) {
            Log.d("HealthDroidDatabaseHelper", "Cannot parse string: " + date);
        }
        return null;
    }

    public static String formatAsRfc3992(Date date) {
        return formatRfc3339.format(date);
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                RFC3339_TEMPLATE, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static int getProperty(String date, int property) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatRfc3339.parse(date));
            return calendar.get(property);
        } catch (ParseException e) {
            Log.d("HealthDroidDatabaseHelper", "Cannot parse string: " + date);
        }
        return -1;
    }

    public static int getWeek(String date) {
        return getProperty(date, Calendar.WEEK_OF_YEAR);
    }

    public static int getMonth(String date) {
        return getProperty(date, Calendar.MONTH);
    }

    public static int getYear(String date) {
        return getProperty(date, Calendar.YEAR);
    }

}
