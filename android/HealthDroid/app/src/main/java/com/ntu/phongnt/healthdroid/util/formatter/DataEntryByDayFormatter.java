package com.ntu.phongnt.healthdroid.util.formatter;

import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.ntu.phongnt.healthdroid.util.DateHelper;

import java.util.Calendar;
import java.util.TreeMap;

public class DataEntryByDayFormatter extends BaseDataEntryFormatter {
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

    }
}
