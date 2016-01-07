package com.ntu.phongnt.healthdroid.util.formatter;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.ntu.phongnt.healthdroid.util.DateHelper;
import com.ntu.phongnt.healthdroid.util.DateRange;
import com.ntu.phongnt.healthdroid.util.chart.ChartAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DataEntryByWeekFormatter extends BaseDataEntryFormatter {

    public DataEntryByWeekFormatter(Cursor cursor, ChartAdapter chartAdapter) {
        super(cursor, chartAdapter);
    }

    @NonNull
    @Override
    public String createKey(String createdAt) {
        int weekOfYear = DateHelper.getProperty(createdAt, Calendar.WEEK_OF_YEAR);
        int year = DateHelper.getProperty(createdAt, Calendar.YEAR);
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

    public static class DateRangeByWeek extends DateRange {
        public DateRangeByWeek(String firstDate, String lastDate) {
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
