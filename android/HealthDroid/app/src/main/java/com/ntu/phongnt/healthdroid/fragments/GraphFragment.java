package com.ntu.phongnt.healthdroid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import java.util.List;
import java.util.TreeMap;

public class GraphFragment extends Fragment implements TimeRangeInteractionListener {
    public static String TAG = "GraphFragment";
    public static final String DAY = "Day";
    public static final String WEEK = "Week";
    public static final String MONTH = "Month";
    public static final String[] choices = {DAY, WEEK, MONTH};
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

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeRangeDialogFragment dialogFragment = new TimeRangeDialogFragment();
                dialogFragment.listener = GraphFragment.this;
                dialogFragment.show(getActivity().getSupportFragmentManager(), TAG);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        chart = new LineChart(inflater.getContext());
        chart.setData(new LineData());
        FrameLayout chart_container = (FrameLayout) view.findViewById(R.id.chart_container);

        chart.setExtraOffsets(5, 20, 20, 20);
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

    @Override
    public void onTimeRangeSelected(int timeRange) {
        switch (choices[timeRange]) {
            case DAY:
                break;
            case WEEK:
                break;
            case MONTH:
                break;
        }
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
            List<DataHelper.DataEntry> data = prepareData(cursor);

            //get by month
            TreeMap<String, Float> reducedData = new TreeMap<String, Float>();
            TreeMap<String, Integer> reducedDataCount = new TreeMap<String, Integer>();
            for (DataHelper.DataEntry entry : data) {
                String createdAt = entry.createdAt;
                Float value = entry.value;

                String key = createKeyByMonth(createdAt);
                accumulate(reducedData, reducedDataCount, value, key);
            }

            DateRangeByMonth rangeByMonth = new DateRangeByMonth(reducedData.firstKey(), reducedData.lastKey());

            //Add entries to graph
            Log.d(TAG, "range = " + rangeByMonth.getRange());

            addDataToChart(reducedData, reducedDataCount, rangeByMonth);

            chart.invalidate();
        }

        private void addDataToChart(TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount, DateRangeByMonth rangeByMonth) {
            int month = rangeByMonth.firstMonth;
            int year = rangeByMonth.firstYear;

            while (month != rangeByMonth.lastMonth || year != rangeByMonth.lastYear) {
                String key = String.format("%02d", month) + "/" + String.format("%02d", year);
                if (reducedData.containsKey(key))
                    addEntry((float) reducedData.get(key) / reducedDataCount.get(key), key);
                else
                    addEntry(0, key);
                month += 1;
                if (month > 12) {
                    year += 1;
                    month %= 12;
                }
            }
        }

        @NonNull
        private String createKeyByMonth(String createdAt) {
            int month = DataHelper.getMonth(createdAt);
            int year = DataHelper.getYear(createdAt);
            return String.format("%02d", month) + "/" + String.format("%04d", year);
        }

        private void accumulate(TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount, Float value, String key) {
            if (!reducedData.containsKey(key)) {
                reducedData.put(key, value);
                reducedDataCount.put(key, 1);
            } else {
                reducedData.put(key, value + reducedData.get(key));
                reducedDataCount.put(key, 1 + reducedDataCount.get(key));
            }
        }

        private List<DataHelper.DataEntry> prepareData(Cursor cursor) {
            List<DataHelper.DataEntry> data = new ArrayList<DataHelper.DataEntry>();
            if (cursor.moveToFirst()) {
                do {
                    String createdAt = cursor.getString(cursor.getColumnIndex(DataHelper.CREATED_AT));
                    float value = cursor.getFloat(cursor.getColumnIndex(DataHelper.VALUE));
                    data.add(new DataHelper.DataEntry(createdAt, value));
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            return data;
        }


        @Override
        protected Cursor doInBackground(Void... params) {
            return (doQuery());
        }
    }

    private class DateRangeByMonth {
        public int firstMonth;
        public int firstYear;
        public int lastMonth;
        public int lastYear;

        public DateRangeByMonth(String first, String last) {
            String[] firstKey = first.split("/");
            String[] lastKey = last.split("/");
            firstMonth = Integer.parseInt(firstKey[0]);
            firstYear = Integer.parseInt(firstKey[1]);
            lastMonth = Integer.parseInt(lastKey[0]);
            lastYear = Integer.parseInt(lastKey[1]);
            normalize();
        }

        public void normalize() {
            if (getRange() < 10) {
                lastMonth = lastMonth + 10;
                if (lastMonth > 12) {
                    lastYear += 1;
                    lastMonth %= 12;
                }
            }
        }

        public int getRange() {
            return (lastYear - firstYear) * 12 + lastMonth - firstMonth;
        }
    }

    public static class TimeRangeDialogFragment extends DialogFragment {
        public TimeRangeInteractionListener listener = null;

        public TimeRangeDialogFragment() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            Log.d(TAG, "Creating dialog");
            builder
                    .setItems(choices, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onTimeRangeSelected(which);
                        }
                    })
                    .setPositiveButton("Wai?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            return builder.create();
        }
    }
}
