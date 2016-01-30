package com.ntu.phongnt.healthdroid.graph.util;

import android.util.Log;

import com.ntu.phongnt.healthdroid.db.data.DataHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    private static SimpleDateFormat formatRfc3339 = new SimpleDateFormat(DataHelper.RFC3339_TEMPLATE, Locale.getDefault());

    public static Date getDate(String date) {
        try {
            return formatRfc3339.parse(date);
        } catch (ParseException e) {
            Log.d("DataHelper", "Cannot parse string: " + date);
        }
        return null;
    }

    public static String formatAsRfc3992(Date date) {
        return formatRfc3339.format(date);
    }

    public static int getProperty(String date, int property) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatRfc3339.parse(date));
            return calendar.get(property);
        } catch (ParseException e) {
            Log.d("DataHelper", "Cannot parse string: " + date);
        }
        return -1;
    }

    public static String ZERO_DATE = "2000-01-01T00:00:00.000+07:00";

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
