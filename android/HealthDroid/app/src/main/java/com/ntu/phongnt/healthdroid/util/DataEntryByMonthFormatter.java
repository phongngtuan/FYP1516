package com.ntu.phongnt.healthdroid.util;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.ntu.phongnt.healthdroid.db.DataHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class DataEntryByMonthFormatter {
    private Cursor cursor = null;

    public DataEntryByMonthFormatter(Cursor cursor) {
        this.cursor = cursor;
    }

    public List<DataHelper.DataEntry> prepareData() {
        List<DataHelper.DataEntry> data = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String createdAt = cursor.getString(cursor.getColumnIndex(DataHelper.CREATED_AT));
                float value = cursor.getFloat(cursor.getColumnIndex(DataHelper.VALUE));
                data.add(new DataHelper.DataEntry(createdAt, value));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    @NonNull
    public String createKey(String createdAt) {
        int month = DataHelper.getMonth(createdAt);
        int year = DataHelper.getYear(createdAt);
        return String.format("%02d", month) + "/" + String.format("%04d", year);
    }


    public void accumulate(TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount, Float value, String key) {
        if (!reducedData.containsKey(key)) {
            reducedData.put(key, value);
            reducedDataCount.put(key, 1);
        } else {
            reducedData.put(key, value + reducedData.get(key));
            reducedDataCount.put(key, 1 + reducedDataCount.get(key));
        }
    }

    public void addDataToChart(LineChart lineChart, TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount, DataEntryFormatter.DateRangeByMonth rangeByMonth) {
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
}
