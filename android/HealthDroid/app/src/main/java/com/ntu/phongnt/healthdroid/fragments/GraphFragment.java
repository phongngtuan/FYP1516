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
import com.github.mikephil.charting.data.LineData;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.DataHelper;
import com.ntu.phongnt.healthdroid.util.DataEntryByWeekFormatter;
import com.ntu.phongnt.healthdroid.util.DataEntryFormatter;
import com.ntu.phongnt.healthdroid.util.DateHelper;

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
//            DataEntryFormatter formatter = new DataEntryByMonthFormatter(cursor);
            DataEntryFormatter formatter = new DataEntryByWeekFormatter(cursor);
            List<DateHelper.DataEntry> data = formatter.prepareData();

            //get by month
            TreeMap<String, Float> reducedData = new TreeMap<String, Float>();
            TreeMap<String, Integer> reducedDataCount = new TreeMap<String, Integer>();
            for (DateHelper.DataEntry entry : data) {
                String createdAt = entry.createdAt;
                Float value = entry.value;

                String key = formatter.createKey(createdAt);
                formatter.accumulate(reducedData, reducedDataCount, value, key);
            }

            formatter.addDataToChart(chart, reducedData, reducedDataCount);

            chart.invalidate();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            return (doQuery());
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
