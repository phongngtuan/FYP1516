package com.ntu.phongnt.healthdroid.util.formatter;

import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.ntu.phongnt.healthdroid.util.DateHelper;
import com.ntu.phongnt.healthdroid.util.DateRange;

import java.text.DateFormat;
import java.util.List;
import java.util.TreeMap;

public interface DataEntryFormatter {
    void format(LineChart chart, String label);

    List<DateHelper.DataEntry> prepareData();

    DateRange getDateRange(String firstDate, String lastDate);

    DateFormat getDateFormat();

    @NonNull
    String createKey(String createdAt);

    void accumulate(TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount, Float value, String key);

    void addDataToChart(LineChart lineChart, TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount);
}
