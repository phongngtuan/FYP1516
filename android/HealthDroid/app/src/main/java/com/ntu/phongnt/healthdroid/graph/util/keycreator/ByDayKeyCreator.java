package com.ntu.phongnt.healthdroid.graph.util.keycreator;

import com.ntu.phongnt.healthdroid.graph.util.DateHelper;
import com.ntu.phongnt.healthdroid.graph.util.DateRange;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ByDayKeyCreator implements KeyCreator {
    @Override
    public String createKey(String date) {
        int dayOfMonth = DateHelper.getProperty(date, Calendar.DAY_OF_MONTH);
        int month = DateHelper.getProperty(date, Calendar.MONTH);
        int year = DateHelper.getProperty(date, Calendar.YEAR);
        return String.format("%02d", dayOfMonth) + "/"
                + String.format("%02d", month + 1) + "/"
                + String.format("%04d", year);
    }

    @Override
    public DateRange getDateRange(String firstDate, String lastDate) {
        return new DateRangeByDay(firstDate, lastDate);
    }

    @Override
    public DateFormat getDateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Override
    public int getTimeUnit() {
        return Calendar.DAY_OF_YEAR;
    }

    public static class DateRangeByDay extends DateRange {
        public DateRangeByDay(String firstDate, String lastDate) {
            String[] firstKey = firstDate.split("/");
            String[] lastKey = lastDate.split("/");
            int firstDayOfMonth = Integer.parseInt(firstKey[0]);
            int firstMonth = Integer.parseInt(firstKey[1]);
            int firstYear = Integer.parseInt(firstKey[2]);
            int lastDayOfMonth = Integer.parseInt(lastKey[0]);
            int lastMonth = Integer.parseInt(lastKey[1]);
            int lastYear = Integer.parseInt(lastKey[2]);

            GregorianCalendar first = new GregorianCalendar();
            first.set(firstYear, firstMonth - 1, firstDayOfMonth);
            GregorianCalendar last = new GregorianCalendar();
            last.set(lastYear, lastMonth - 1, lastDayOfMonth);

            setFirst(first);
            setLast(last);

            normalize();
        }

        @Override
        public int getTimeUnit() {
            return Calendar.DAY_OF_YEAR;
        }
    }
}
