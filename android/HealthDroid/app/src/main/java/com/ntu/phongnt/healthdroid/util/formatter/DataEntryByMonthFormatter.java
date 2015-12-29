package com.ntu.phongnt.healthdroid.util.formatter;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.ntu.phongnt.healthdroid.util.DateHelper;
import com.ntu.phongnt.healthdroid.util.LineChartHelper;

import java.util.TreeMap;

public class DataEntryByMonthFormatter extends BaseDataEntryFormatter {
    public DataEntryByMonthFormatter(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    @NonNull
    public String createKey(String createdAt) {
        int month = DateHelper.getMonth(createdAt);
        int year = DateHelper.getYear(createdAt);
        return String.format("%02d", month) + "/" + String.format("%04d", year);
    }

    @Override
    public void addDataToChart(LineChart lineChart, TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount) {
        DateRangeByMonth rangeByMonth = new DateRangeByMonth(reducedData.firstKey(), reducedData.lastKey());
        int month = rangeByMonth.firstMonth;
        int year = rangeByMonth.firstYear;

        while (month != rangeByMonth.lastMonth || year != rangeByMonth.lastYear) {
            String key = String.format("%02d", month) + "/" + String.format("%02d", year);
            if (reducedData.containsKey(key))
                LineChartHelper.addEntry(lineChart, (float) reducedData.get(key) / reducedDataCount.get(key), key);
            else
                LineChartHelper.addEntry(lineChart, 0, key);
            month += 1;
            if (month > 12) {
                year += 1;
                month %= 12;
            }
        }
    }

    public static class DateRangeByMonth implements DateHelper.DateRange {
        public int firstMonth;
        public int firstYear;
        public int lastMonth;
        public int lastYear;

        public DateRangeByMonth(String first, String last) {
            String[] firstKey = first.split("/");
            String[] lastKey = last.split("/");
            firstMonth = Integer.parseInt(firstKey[0]);
            firstYear = Integer.parseInt(firstKey[1]);
            lastMonth = Integer.parseInt(lastKey[0]);
            lastYear = Integer.parseInt(lastKey[1]);
            normalize();
        }

        @Override
        public void normalize() {
            if (getRange() < 10) {
                lastMonth = lastMonth + 10;
                if (lastMonth > 12) {
                    lastYear += 1;
                    lastMonth %= 12;
                }
            }
        }

        @Override
        public int getRange() {
            return (lastYear - firstYear) * 12 + lastMonth - firstMonth;
        }
    }
}
