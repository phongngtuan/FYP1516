package com.ntu.phongnt.healthdroid.graph.util.simple;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.graph.util.chartadapter.LineChartAdapter;
import com.ntu.phongnt.healthdroid.graph.util.graphmanager.DataPool;
import com.ntu.phongnt.healthdroid.graph.view.GraphFragment;

public class SimpleDataFragment extends GraphFragment {
    @Override
    protected Cursor getQuery(HealthDroidDatabaseHelper db) {
        return db
                .getReadableDatabase()
                .query(DataContract.DataEntry.TABLE_NAME,
                        new String[]{DataContract.DataEntry._ID,
                                DataContract.DataEntry.COLUMN_NAME_VALUE,
                                DataContract.DataEntry.COLUMN_NAME_DATE,
                                DataContract.DataEntry.COLUMN_NAME_USER,
                                DataContract.DataEntry.COLUMN_NAME_TYPE
                        },
                        DataContract.DataEntry.COLUMN_NAME_TYPE + " = 0",
                        null,
                        null,
                        null,
                        DataContract.DataEntry.COLUMN_NAME_DATE);
    }

    @Override
    protected Chart makeChart(Context context) {
        LineChart chart = new LineChart(context);
        setChartAdapter(new LineChartAdapter(chart));
        chart.setDescription(getDescription());
        chart.setData(new LineData());
        chart.setExtraOffsets(5, 20, 20, 20);
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDragEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.BLACK);
        chart.setDescriptionColor(Color.WHITE);

        //Legend
        Legend legend = chart.getLegend();
        legend.setTextColor(Color.WHITE);

        //Appearance
        XAxis x1 = chart.getXAxis();
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);
        x1.setTextColor(Color.WHITE);
        YAxis y1 = chart.getAxisLeft();
        y1.setDrawGridLines(true);
        y1.setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);

        chart.invalidate();
        return chart;
    }

    @Override
    public String getDescription() {
        return "Simple";
    }

    @Override
    protected DataPool makeDataPool() {
        return new SimpleDataPool();
    }
}
