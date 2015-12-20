package com.ntu.phongnt.healthdroid.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.DataHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphFragment extends Fragment {
    private LineChart chart = null;
    private DataHelper db = null;

    public GraphFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.content_graph, container, false);
        chart = new LineChart(inflater.getContext());
        chart.setData(new LineData());
        FrameLayout chart_container = (FrameLayout) view.findViewById(R.id.chart_container);

        chart.setTouchEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDragEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.BLACK);
        chart.setDescriptionColor(Color.WHITE);

        db = DataHelper.getInstance(getActivity());

        new LoadCursorTask().execute();

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

        chart_container.addView(chart);
        return view;
    }

    private void addEntry(float value) {
        addEntry(value, "");
    }

    private void addEntry(float value, String label) {
        LineData data = chart.getData();

        if (data != null) {
            LineDataSet dataSet = data.getDataSetByIndex(0);
            if (dataSet == null) {
                dataSet = createSet();
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

    private LineDataSet createSet() {
        return new LineDataSet(new ArrayList<Entry>(), "value");
    }

    abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {
        protected Cursor doQuery() {
            Cursor result =
                    db
                            .getReadableDatabase()
                            .query(DataHelper.TABLE,
                                    new String[]{"ROWID AS _id",
                                            DataHelper.VALUE, DataHelper.CREATED_AT},
                                    null, null, null, null, DataHelper.VALUE);
            Log.i("GraphFragment", String.valueOf(result.getCount()));
            return (result);
        }
    }

    private class LoadCursorTask extends BaseTask<Void> {
        @Override
        protected void onPostExecute(Cursor cursor) {
            HashMap<String, Float> monthToValue = new HashMap<String, Float>();

            if (cursor.moveToFirst()) {
                do {
                    float data = cursor.getFloat(cursor.getColumnIndex(DataHelper.VALUE));
                    int month = DataHelper.getMonth(cursor.getString(cursor.getColumnIndex(DataHelper.CREATED_AT)));
                    int year = DataHelper.getYear(cursor.getString(cursor.getColumnIndex(DataHelper.CREATED_AT)));
                    String key = String.valueOf(month) + "/" + String.valueOf(year);
                    Float value = monthToValue.get(key);
                    if (value == null) {
                        monthToValue.put(key, data);
                    } else {
                        monthToValue.put(key, value + data);
                    }
//                    addEntry(data, String.valueOf(month));
                } while (cursor.moveToNext());
            }
            cursor.close();

            for (Map.Entry<String, Float> entry : monthToValue.entrySet()) {
                addEntry(entry.getValue(), entry.getKey());
            }

            chart.invalidate();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            return (doQuery());
        }
    }
}
