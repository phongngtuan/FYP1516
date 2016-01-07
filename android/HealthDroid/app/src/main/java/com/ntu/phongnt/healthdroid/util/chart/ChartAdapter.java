package com.ntu.phongnt.healthdroid.util.chart;

import java.util.List;

public abstract class ChartAdapter {
    public abstract void addEntry(String label, float value, int index);

    public abstract void addXValue(String key);

    public abstract void clearDataSets();

    public abstract void showDataSetsByLabel(List<String> labels);

}
