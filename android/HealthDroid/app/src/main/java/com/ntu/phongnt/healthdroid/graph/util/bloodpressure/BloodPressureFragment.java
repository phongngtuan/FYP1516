package com.ntu.phongnt.healthdroid.graph.util.bloodpressure;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ScatterData;
import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.graph.util.chartadapter.ScatterChartAdapter;
import com.ntu.phongnt.healthdroid.graph.util.graphmanager.DataPool;
import com.ntu.phongnt.healthdroid.graph.view.GraphFragment;

public class BloodPressureFragment extends GraphFragment {
    @Override
    public String getDescription() {
        return "Blood Pressure";
    }

    @Override
    public String getType() {
        return "Blood Pressure";
    }

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
                        DataContract.DataEntry.COLUMN_NAME_TYPE + " = 1",
                        null,
                        null,
                        null,
                        DataContract.DataEntry.COLUMN_NAME_DATE);
    }

    @Override
    protected DataPool makeDataPool() {
        return new BloodPressureDataPool();
    }

    @Override
    protected Chart makeChart(Context context) {
        ScatterChart chart = new ScatterChart(context);
        setChartAdapter(new ScatterChartAdapter(chart));
        chart.setDescription(getDescription());
        chart.setData(new ScatterData());
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
}
