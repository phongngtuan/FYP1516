package com.ntu.phongnt.healthdroid.graph.util.formatter;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.ntu.phongnt.healthdroid.graph.util.DateHelper;
import com.ntu.phongnt.healthdroid.graph.util.DateRange;
import com.ntu.phongnt.healthdroid.graph.util.chart.ChartAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DataEntryByMonthFormatter extends BaseDataEntryFormatter {

    public DataEntryByMonthFormatter(Cursor cursor, ChartAdapter chartAdapter) {
        super(cursor, chartAdapter);
    }

    @Override
    @NonNull
    public String createKey(String createdAt) {
        int month = DateHelper.getMonth(createdAt);
        int year = DateHelper.getYear(createdAt);
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
