package com.ntu.phongnt.healthdroid.util.formatter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.ntu.phongnt.healthdroid.util.DateHelper;
import com.ntu.phongnt.healthdroid.util.LineChartHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

public class DataEntryByDayFormatter extends BaseDataEntryFormatter {
    public DataEntryByDayFormatter(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public String createKey(String createdAt) {
        int dayOfMonth = DateHelper.getProperty(createdAt, Calendar.DAY_OF_MONTH);
        int month = DateHelper.getProperty(createdAt, Calendar.MONTH);
        int year = DateHelper.getProperty(createdAt, Calendar.YEAR);
        return String.format("%02d", dayOfMonth) + "/"
                + String.format("%02d", month) + "/"
                + String.format("%04d", year);
    }

    @Override
    public void addDataToChart(LineChart lineChart, TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount) {
        DateRangeByDay rangeByDay = new DateRangeByDay(reducedData.firstKey(), reducedData.lastKey());
        GregorianCalendar first = new GregorianCalendar();
        first.setTime(rangeByDay.first.getTime());

        GregorianCalendar last = new GregorianCalendar();
        last.setTime(rangeByDay.last.getTime());
        last.add(Calendar.DAY_OF_YEAR, 1);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        while (first.before(last)) {
            String key = format.format(first.getTime());
            if (reducedData.containsKey(key))
                LineChartHelper.addEntry(lineChart, (float) reducedData.get(key) / reducedDataCount.get(key), key);
            else
                LineChartHelper.addEntry(lineChart, 0, key);
            first.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    public static class DateRangeByDay implements DateHelper.DateRange {
        public int firstDayOfMonth;
        public int firstMonth;
        public int firstYear;

        public int lastDayOfMonth;
        public int lastMonth;
        public int lastYear;

        public GregorianCalendar first;
        public GregorianCalendar last;

        public DateRangeByDay(String first, String last) {
            String[] firstKey = first.split("/");
            String[] lastKey = last.split("/");
            firstDayOfMonth = Integer.parseInt(firstKey[0]);
            firstMonth = Integer.parseInt(firstKey[1]);
            firstYear = Integer.parseInt(firstKey[2]);

            lastDayOfMonth = Integer.parseInt(lastKey[0]);
            lastMonth = Integer.parseInt(lastKey[1]);
            lastYear = Integer.parseInt(lastKey[2]);

            this.first = new GregorianCalendar();
            this.first.set(firstYear, firstMonth, firstDayOfMonth);
            this.last = new GregorianCalendar();
            this.last.set(lastYear, lastMonth, lastDayOfMonth);

            normalize();
        }

        @Override
        public void normalize() {
            GregorianCalendar temp = new GregorianCalendar();
            temp.set(firstYear, firstMonth, firstDayOfMonth);
            temp.add(Calendar.DAY_OF_YEAR, 10);
            if (!temp.before(last)) {
                last.add(Calendar.DAY_OF_YEAR, 10);
                Log.d("Formatter", "This should never happens");
            }
        }

        @Override
        public int getRange() {
            //TODO: Refactor this, because we can implement the loop without range
            //using the calendar before() method
            first = new GregorianCalendar();
            first.set(firstYear, firstMonth, firstDayOfMonth);
            last = new GregorianCalendar();
            last.set(lastYear, lastMonth, lastDayOfMonth);
            int firstDayOfYear = first.get(Calendar.DAY_OF_YEAR);
            int lastDayOfYear = last.get(Calendar.DAY_OF_YEAR);
            int range = (lastYear - firstYear) * 365 + (lastDayOfYear - firstDayOfYear);
            first.add(Calendar.DAY_OF_YEAR, range);
            while (first.before(last)) {
                first.add(Calendar.DAY_OF_YEAR, range);
                range += 1;
            }
            return range;
        }
    }
}
