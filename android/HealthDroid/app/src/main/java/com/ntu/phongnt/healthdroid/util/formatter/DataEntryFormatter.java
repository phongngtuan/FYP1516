package com.ntu.phongnt.healthdroid.util.formatter;

import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.ntu.phongnt.healthdroid.util.DateHelper;

import java.util.List;
import java.util.TreeMap;

public interface DataEntryFormatter {
    void format(LineChart chart);

    List<DateHelper.DataEntry> prepareData();

    @NonNull
    String createKey(String createdAt);

    void accumulate(TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount, Float value, String key);

    void addDataToChart(LineChart lineChart, TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount);
}
