package com.ntu.phongnt.healthdroid.util.chart;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class LineChartAdapter extends ChartAdapter {
    LineChart chart = null;

    public LineChartAdapter(LineChart chart) {
        this.chart = chart;
    }

    @Override
    public void addEntry(String label, float value, int index) {
        LineData data = chart.getData();
        if (data != null) {
            LineDataSet dataSet = data.getDataSetByLabel(label, true);
            if (dataSet == null) {
                dataSet = new LineDataSet(new ArrayList<Entry>(), label);
                dataSet.setDrawFilled(true);
                dataSet.setValueTextColor(Color.WHITE);
                data.addDataSet(dataSet);
            }
            dataSet.addEntry(new Entry(value, index));
//            data.addEntry(new Entry(value, index), 0);
            chart.invalidate();
            chart.notifyDataSetChanged();
        }
    }

    @Override
    public void addXValue(String key) {
        chart.getData().addXValue(key);
    }
}
