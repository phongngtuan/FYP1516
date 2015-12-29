package com.ntu.phongnt.healthdroid.util;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class LineChartHelper {

    public static void addEntry(LineChart chart, float value) {
        addEntry(chart, value, "");
    }

    public static void addEntry(LineChart chart, float value, String label) {
        LineData data = chart.getData();

        if (data != null) {
            LineDataSet dataSet = data.getDataSetByIndex(0);
            if (dataSet == null) {
                dataSet = createSet();
                dataSet.setDrawFilled(true);
                dataSet.setDrawCubic(true);
                data.addDataSet(dataSet);
                dataSet.setValueTextColor(Color.WHITE);
            }
            data.addXValue(label);
            data.addEntry(
                    new Entry(value, dataSet.getEntryCount()),
                    0
            );
            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(20);
            chart.moveViewToX(data.getXValCount() - 7);
        }
    }

    private static LineDataSet createSet() {
        return new LineDataSet(new ArrayList<Entry>(), "value");
    }

}
