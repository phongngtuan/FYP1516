package com.ntu.phongnt.healthdroid.util.formatter;

import android.database.Cursor;

import com.github.mikephil.charting.charts.LineChart;
import com.ntu.phongnt.healthdroid.db.DataHelper;
import com.ntu.phongnt.healthdroid.util.DateHelper;
import com.ntu.phongnt.healthdroid.util.DateRange;
import com.ntu.phongnt.healthdroid.util.LineChartHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeMap;

public abstract class BaseDataEntryFormatter implements DataEntryFormatter {
    Cursor cursor = null;

    @SuppressWarnings("ResourceType")
    @Override
    public void addDataToChart(LineChart lineChart, TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount) {
        DateRange rangeByDay = getDateRange(reducedData.firstKey(), reducedData.lastKey());
        GregorianCalendar first = new GregorianCalendar();
        first.setTime(rangeByDay.getFirst().getTime());

        GregorianCalendar last = new GregorianCalendar();
        last.setTime(rangeByDay.getLast().getTime());
        last.add(Calendar.DAY_OF_YEAR, 1);

        DateFormat format = getDateFormat();
        while (first.before(last)) {
            String key = format.format(first.getTime());
            if (reducedData.containsKey(key))
                LineChartHelper.addEntry(lineChart, (float) reducedData.get(key) / reducedDataCount.get(key), key);
            else
                LineChartHelper.addEntry(lineChart, 0, key);
            first.add(rangeByDay.getTimeUnit(), 1);
        }
    }


    @Override
    public void format(LineChart chart) {
        List<DateHelper.DataEntry> data = prepareData();

        //get by month
        TreeMap<String, Float> reducedData = new TreeMap<String, Float>(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                String[] lhsKeys = lhs.split("/");
                String[] rhsKeys = rhs.split("/");

                for (int i = lhsKeys.length - 1; i >= 0; i--) {
                    int lhsValue = Integer.parseInt(lhsKeys[i]);
                    int rhsValue = Integer.parseInt(rhsKeys[i]);
                    if (lhsValue != rhsValue)
                        return lhsValue - rhsValue;
                }
                return 0;
            }
        });
        TreeMap<String, Integer> reducedDataCount = new TreeMap<String, Integer>();
        for (DateHelper.DataEntry entry : data) {
            String createdAt = entry.createdAt;
            Float value = entry.value;

            String key = createKey(createdAt);
            accumulate(reducedData, reducedDataCount, value, key);
        }

        addDataToChart(chart, reducedData, reducedDataCount);
    }

    @Override
    public List<DateHelper.DataEntry> prepareData() {
        List<DateHelper.DataEntry> data = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String createdAt = cursor.getString(cursor.getColumnIndex(DataHelper.CREATED_AT));
                float value = cursor.getFloat(cursor.getColumnIndex(DataHelper.VALUE));
                data.add(new DateHelper.DataEntry(createdAt, value));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    @Override
    public void accumulate(TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount, Float value, String key) {
        if (!reducedData.containsKey(key)) {
            reducedData.put(key, value);
            reducedDataCount.put(key, 1);
        } else {
            reducedData.put(key, value + reducedData.get(key));
            reducedDataCount.put(key, 1 + reducedDataCount.get(key));
        }
    }
}
