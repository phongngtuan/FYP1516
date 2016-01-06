package com.ntu.phongnt.healthdroid.util.chart;

public abstract class ChartAdapter {
    public abstract void addEntry(String label, float value, int index);

    public abstract void addXValue(String key);
}
