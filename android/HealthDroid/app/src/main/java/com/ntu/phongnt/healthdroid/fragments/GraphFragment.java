package com.ntu.phongnt.healthdroid.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ntu.phongnt.healthdroid.R;

import java.util.ArrayList;

public class GraphFragment extends Fragment {
    private LineChart chart;

    public GraphFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.graph_main, container, false);
        chart = new LineChart(inflater.getContext());
        chart.setData(new LineData());
        FrameLayout chart_container = (FrameLayout) view.findViewById(R.id.chart_container);

        chart.setHighlightEnabled(true);
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDragEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.LTGRAY);

        addEntry();
        addEntry();
        XAxis x1 = chart.getXAxis();
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);
        YAxis y1 = chart.getAxisLeft();
        y1.setDrawGridLines(true);
        chart.getAxisRight().setEnabled(false);

        chart.invalidate();

        chart_container.addView(chart);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 100; i++) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            addEntry();
//                        }
//                    });
//
//                    try {
//                        Thread.sleep(35);
//                    }
//                    catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
        return view;
    }

    private void addEntry() {
        LineData data = chart.getData();

        if (data != null) {
            LineDataSet dataSet = data.getDataSetByIndex(0);
            if (dataSet == null) {
                dataSet = createSet();
                data.addDataSet(dataSet);
            }
            data.addXValue("");
            data.addEntry(
                    new Entry((float) (Math.random()*10), dataSet.getEntryCount()),
                    0
            );
            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(20);
            chart.moveViewToX(data.getXValCount() - 7);
        }
    }

    private LineDataSet createSet(){
        return new LineDataSet(new ArrayList<Entry>(), "value");
    }
}
