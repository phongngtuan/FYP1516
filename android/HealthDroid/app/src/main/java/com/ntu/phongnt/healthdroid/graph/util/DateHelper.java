package com.ntu.phongnt.healthdroid.graph.util;

import android.util.Log;

import com.ntu.phongnt.healthdroid.db.data.DataHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(DataHelper.RFC3339_TEMPLATE, Locale.getDefault());

    public static Date getDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            Log.d("DataHelper", "Cannot parse string: " + date);
        }
        return null;
    }

    public static String toString(Date date) {
        return dateFormat.format(date);
    }

    public static int getProperty(String date, int property) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(date));
            return calendar.get(property);
        } catch (ParseException e) {
            Log.d("DataHelper", "Cannot parse string: " + date);
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
