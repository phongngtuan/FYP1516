package com.ntu.phongnt.healthdroid.graph.util.chartadapter;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class LineChartAdapter extends ChartAdapter {
    public static int[] colors = {
//            Color.BLACK,
//            Color.DKGRAY,
//            Color.GRAY,
//            Color.LTGRAY,
            Color.WHITE,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA
    };
    LineChart chart = null;
    List<LineDataSet> dataSets = null;

    public LineChartAdapter(LineChart chart) {
        this.chart = chart;
        this.dataSets = new ArrayList<>();
    }

    @Override
    public void addEntry(String label, float value, int index) {
        LineData data = chart.getData();
        if (data != null) {
            LineDataSet dataSet = data.getDataSetByLabel(label, true);
            if (dataSet == null) {
                dataSet = new LineDataSet(new ArrayList<Entry>(), label);
                final int color = colors[dataSets.size() % colors.length];
                dataSet.setValueTextColor(color);
                dataSet.setFillColor(color);
                dataSet.setCircleColor(color);
                dataSet.setColor(color);
                dataSets.add(dataSet);
                data.addDataSet(dataSet);
            }
            int dataSetIndex = data.getIndexOfDataSet(dataSet);
            //TODO: Adding entry to dataSet doesn't work. Don't know why
            //dataSet.addEntry(new Entry(value, index));
            data.addEntry(new Entry(value, index), dataSetIndex);
            chart.invalidate();
            chart.notifyDataSetChanged();
        }
    }

    @Override
    public void addXValue(String key) {
        chart.getData().addXValue(key);
    }

    @Override
    public void clearDataSets() {
        this.dataSets.clear();
        this.chart.getXAxis().getValues().clear();
        chart.getData().getDataSets().clear();
    }

    @Override
    public void showDataSetsByLabel(List<String> labels) {
        LineData data = chart.getData();
        data.getDataSets().clear();
        for (LineDataSet dataSet : this.dataSets) {
            if (labels.contains(dataSet.getLabel()))
                data.addDataSet(dataSet);
        }
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    @Override
    public List<String> getDataSetLabels() {
        List<String> result = new ArrayList<>();
        for (LineDataSet dataSet : this.dataSets) {
            result.add(dataSet.getLabel());
        }
        return result;
    }
}
