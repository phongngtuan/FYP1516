package com.ntu.phongnt.healthdroid.graph.util.keycreator;

import com.ntu.phongnt.healthdroid.graph.util.DateHelper;
import com.ntu.phongnt.healthdroid.graph.util.DateRange;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ByWeekKeyCreator implements KeyCreator {
    @Override
    public String createKey(String date) {
        int weekOfYear = DateHelper.getProperty(date, Calendar.WEEK_OF_YEAR);
        int year = DateHelper.getProperty(date, Calendar.YEAR);
        return String.format("%02d", weekOfYear) + "/"
                + String.format("%04d", year);
    }

    @Override
    public DateRange getDateRange(String firstDate, String lastDate) {
        return new DateRangeByWeek(firstDate, lastDate);
    }

    @Override
    public DateFormat getDateFormat() {
        return new SimpleDateFormat("ww/yyyy", Locale.getDefault());
    }

    @Override
    public int getTimeUnit() {
        return Calendar.WEEK_OF_YEAR;
    }

    public static class DateRangeByWeek extends DateRange {
        public DateRangeByWeek(String firstDate, String lastDate) {
            //TODO: year is incorrect, to be fix later (off by 1)
            String[] firstKey = firstDate.split("/");
            String[] lastKey = lastDate.split("/");
            int firstWeek = Integer.parseInt(firstKey[0]);
            int firstYear = Integer.parseInt(firstKey[1]);
            int lastWeek = Integer.parseInt(lastKey[0]);
            int lastYear = Integer.parseInt(lastKey[1]);

            GregorianCalendar first = new GregorianCalendar();
            GregorianCalendar last = new GregorianCalendar();

            first.set(Calendar.WEEK_OF_YEAR, firstWeek);
            first.set(Calendar.YEAR, firstYear);
            last.set(Calendar.WEEK_OF_YEAR, lastWeek);
            last.set(Calendar.YEAR, lastYear);

            setFirst(first);
            setLast(last);

            normalize();
        }

        @Override
        public int getTimeUnit() {
            return Calendar.WEEK_OF_YEAR;
        }
    }
}
