package com.ntu.phongnt.healthdroid.graph.util.formatter;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

public interface DataEntryFormatter {
    void format(LineChart chart);
    List<String> getDataSetLabels();
}
