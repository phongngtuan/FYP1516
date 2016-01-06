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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.db.data.DataHelper;
import com.ntu.phongnt.healthdroid.util.formatter.DataEntryByDayFormatter;
import com.ntu.phongnt.healthdroid.util.formatter.DataEntryByMonthFormatter;
import com.ntu.phongnt.healthdroid.util.formatter.DataEntryByWeekFormatter;
import com.ntu.phongnt.healthdroid.util.formatter.DataEntryFormatter;

public class GraphFragment extends Fragment implements TimeRangeInteractionListener {
    public static String TAG = "GraphFragment";
    public static final String DAY = "Day";
    public static final String WEEK = "Week";
    public static final String MONTH = "Month";
    public static final String[] choices = {DAY, WEEK, MONTH};
    private LineChart chart = null;
    private DataHelper db = null;
    private String formatter_choice = MONTH;

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

        new DisplayDataByMonthTask().execute();

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

        chart_container.addView(chart);
        return view;
    }

    @Override
    public void onTimeRangeSelected(int timeRange) {
        switch (choices[timeRange]) {
            case DAY:
                formatter_choice = DAY;
                new DisplayDataByDayTask().execute();
                break;
            case WEEK:
                formatter_choice = WEEK;
                new DisplayDataByWeekTask().execute();
                break;
            case MONTH:
                formatter_choice = MONTH;
                new DisplayDataByMonthTask().execute();
                break;
        }
    }

    abstract private class GetCursorTask<T> extends AsyncTask<T, Void, Cursor> {
        protected Cursor doQuery() {
            Cursor result =
                    db
                            .getReadableDatabase()
                            .query(DataContract.DataEntry.TABLE_NAME,
                                    new String[]{DataContract.DataEntry._ID,
                                            DataContract.DataEntry.COLUMN_NAME_VALUE,
                                            DataContract.DataEntry.COLUMN_NAME_DATE,
                                            DataContract.DataEntry.COLUMN_NAME_USER},
                                    null,
                                    null,
                                    null,
                                    null,
                                    DataContract.DataEntry.COLUMN_NAME_DATE);
            Log.i("GraphFragment", String.valueOf(result.getCount()));
            return (result);
        }
    }

    private abstract class DisplayDataTask extends GetCursorTask<String> {
        @Override
        protected Cursor doInBackground(String... params) {
            return (doQuery());
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            chart.getLineData().clearValues();
            chart.getXAxis().getValues().clear();
            DataEntryFormatter formatter = getDataEntryFormatter(cursor);
            formatter.format(chart);
            chart.invalidate();
        }

        abstract DataEntryFormatter getDataEntryFormatter(Cursor cursor);
    }


    private class DisplayDataByDayTask extends DisplayDataTask {
        @Override
        DataEntryFormatter getDataEntryFormatter(Cursor cursor) {
            return new DataEntryByDayFormatter(cursor);
        }
    }

    private class DisplayDataByWeekTask extends DisplayDataTask {
        @Override
        DataEntryFormatter getDataEntryFormatter(Cursor cursor) {
            return new DataEntryByWeekFormatter(cursor);
        }
    }

    private class DisplayDataByMonthTask extends DisplayDataTask {
        @Override
        DataEntryFormatter getDataEntryFormatter(Cursor cursor) {
            return new DataEntryByMonthFormatter(cursor);
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
