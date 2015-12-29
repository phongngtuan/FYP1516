package com.ntu.phongnt.healthdroid.util;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;

import java.util.Calendar;
import java.util.TreeMap;

public class DataEntryByWeekFormatter extends BaseDataEntryFormatter {
    public DataEntryByWeekFormatter(Cursor cursor) {
        this.cursor = cursor;
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
    public void addDataToChart(LineChart lineChart, TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount) {
        DateRangeByWeek rangeByWeek = new DateRangeByWeek(reducedData.firstKey(), reducedData.lastKey());
        int week = rangeByWeek.firstWeek;
        int year = rangeByWeek.firstYear;

        while (week != rangeByWeek.lastWeek || year != rangeByWeek.lastYear) {
            String key = String.format("%02d", week) + "/" + String.format("%04d", year);
            if (reducedData.containsKey(key))
                LineChartHelper.addEntry(lineChart, (float) reducedData.get(key) / reducedDataCount.get(key), key);
            else
                LineChartHelper.addEntry(lineChart, 0, key);
            week += 1;
            if (week > 52) {
                year += 1;
                week -= 52;
            }
        }
    }

    public static class DateRangeByWeek implements DateHelper.DateRange {
        public int firstWeek;
        public int firstYear;
        public int lastWeek;
        public int lastYear;

        public DateRangeByWeek(String first, String last) {
            String[] firstKey = first.split("/");
            String[] lastKey = last.split("/");
            firstWeek = Integer.parseInt(firstKey[0]);
            firstYear = Integer.parseInt(firstKey[1]);
            lastWeek = Integer.parseInt(lastKey[0]);
            lastYear = Integer.parseInt(lastKey[1]);
            normalize();
        }

        @Override
        public void normalize() {
            if (getRange() < 10) {
                lastWeek = lastWeek + 10;
                if (lastWeek > 52) {
                    lastYear += 1;
                    lastWeek -= 52;
                }
            }
        }

        @Override
        public int getRange() {
            return (lastYear - firstYear) * 52 + lastWeek - firstWeek;
        }
    }
}
