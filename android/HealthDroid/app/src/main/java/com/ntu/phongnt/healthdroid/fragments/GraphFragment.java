package com.ntu.phongnt.healthdroid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ntu.phongnt.healthdroid.R;

import java.util.ArrayList;

public class GraphFragment extends Fragment {
    public GraphFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.graph_main, container, false);
        LineChart chart = new LineChart(inflater.getContext());
        FrameLayout chart_container = (FrameLayout) view.findViewById(R.id.chart_container);

        chart.setAutoScaleMinMaxEnabled(true);
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("1"); xVals.add("2"); xVals.add("3"); xVals.add("4");
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry((float)1.0, 1));
        yVals.add(new Entry((float)2.0, 2));
        yVals.add(new Entry((float)4.0, 4));
        LineDataSet lineDataSet = new LineDataSet(yVals, "Value");
        LineData lineData = new LineData(xVals, lineDataSet);
        chart.setData(lineData);
        chart.invalidate();
        chart_container.addView(chart);
        return view;
    }
}
