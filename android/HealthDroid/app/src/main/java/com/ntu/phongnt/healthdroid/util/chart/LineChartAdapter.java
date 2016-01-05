package com.ntu.phongnt.healthdroid.util.chart;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class LineChartAdapter extends ChartAdapter {
    LineChart chart = null;
    String username = null;

    LineData data;
    LineDataSet dataSet;

    public LineChartAdapter(LineChart chart, String username) {
        this.chart = chart;
        this.username = username;

        data = this.chart.getData();
        if (data != null) {
            LineDataSet dataSet = data.getDataSetByIndex(0);
            if (dataSet == null) {
                dataSet = new LineDataSet(new ArrayList<Entry>(), username);
                dataSet.setDrawFilled(true);
                dataSet.setValueTextColor(Color.WHITE);
                data.addDataSet(dataSet);
                this.dataSet = dataSet;
            }
        }
    }

    @Override
    public void addEntry(float value, String label) {
        data.addXValue(label);
        data.addEntry(
                new Entry(value, dataSet.getEntryCount()),
                0
        );
        chart.notifyDataSetChanged();
    }
}
