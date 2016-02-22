package com.ntu.phongnt.healthdroid.graph.util.keycreator;

import com.ntu.phongnt.healthdroid.graph.util.DateHelper;
import com.ntu.phongnt.healthdroid.graph.util.DateRange;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ByMonthKeyCreator implements KeyCreator {
    @Override
    public String createKey(String date) {
        int month = DateHelper.getMonth(date);
        int year = DateHelper.getYear(date);
        return String.format("%02d", month + 1) + "/" + String.format("%04d", year);
    }

    @Override
    public DateRange getDateRange(String firstDate, String lastDate) {
        return new DateRangeByMonth(firstDate, lastDate);
    }

    @Override
    public DateFormat getDateFormat() {
        return new SimpleDateFormat("MM/yyyy");
    }

    @Override
    public int getTimeUnit() {
        return Calendar.MONTH;
    }

    public static class DateRangeByMonth extends DateRange {
        public DateRangeByMonth(String firstDate, String lastDate) {
            String[] firstKey = firstDate.split("/");
            String[] lastKey = lastDate.split("/");
            int firstMonth = Integer.parseInt(firstKey[0]);
            int firstYear = Integer.parseInt(firstKey[1]);
            int lastMonth = Integer.parseInt(lastKey[0]);
            int lastYear = Integer.parseInt(lastKey[1]);

            GregorianCalendar first = new GregorianCalendar();
            first.set(Calendar.MONTH, firstMonth - 1);
            first.set(Calendar.YEAR, firstYear);

            GregorianCalendar last = new GregorianCalendar();
            last.set(Calendar.MONTH, lastMonth - 1);
            last.set(Calendar.YEAR, lastYear);

            setFirst(first);
            setLast(last);

            normalize();
        }

        @Override
        public int getTimeUnit() {
            return Calendar.MONTH;
        }
    }
}
