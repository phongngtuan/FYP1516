package com.ntu.phongnt.healthdroid.graph.util.chartadapter;

import android.graphics.Color;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.util.ArrayList;
import java.util.List;

public class ScatterChartAdapter extends ChartAdapter {
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
    ScatterChart chart;
    List<ScatterDataSet> dataSets;

    public ScatterChartAdapter(ScatterChart chart) {
        this.chart = chart;
        this.dataSets = new ArrayList<>();
    }

    @Override
    public void addEntry(String label, float value, int index) {
        ScatterData data = chart.getScatterData();
        if (data != null) {
            ScatterDataSet dataSet = data.getDataSetByLabel(label, true);
            if (dataSet == null) {
                dataSet = new ScatterDataSet(new ArrayList<Entry>(), label);
                final int color = colors[dataSets.size() % colors.length];
                dataSet.setValueTextColor(color);
                dataSet.setColor(color);
                dataSets.add(dataSet);
                data.addDataSet(dataSet);
            }

            int dataSetIndex = data.getIndexOfDataSet(dataSet);
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
        ScatterData data = chart.getData();
        data.getDataSets().clear();
        for (ScatterDataSet dataSet : this.dataSets) {
            if (labels.contains(dataSet.getLabel()))
                data.addDataSet(dataSet);
        }
        chart.notifyDataSetChanged();
        chart.invalidate();

    }

    @Override
    public List<String> getDataSetLabels() {
        List<String> result = new ArrayList<>();
        for (ScatterDataSet dataSet : this.dataSets) {
            result.add(dataSet.getLabel());
        }
        return result;
    }
}
