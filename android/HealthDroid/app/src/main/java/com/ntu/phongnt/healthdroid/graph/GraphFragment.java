package com.ntu.phongnt.healthdroid.graph;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.graph.dialogs.DataSetPickerFragment;
import com.ntu.phongnt.healthdroid.graph.dialogs.TimeRangeDialogFragment;
import com.ntu.phongnt.healthdroid.graph.util.TitleUtil;
import com.ntu.phongnt.healthdroid.graph.util.chart.ChartAdapter;
import com.ntu.phongnt.healthdroid.graph.util.chart.LineChartAdapter;
import com.ntu.phongnt.healthdroid.graph.util.formatter.DataEntryByDayFormatter;
import com.ntu.phongnt.healthdroid.graph.util.formatter.DataEntryByMonthFormatter;
import com.ntu.phongnt.healthdroid.graph.util.formatter.DataEntryByWeekFormatter;
import com.ntu.phongnt.healthdroid.graph.util.formatter.DataEntryFormatter;

import java.util.List;

public class GraphFragment extends Fragment implements
        TimeRangeDialogFragment.TimeRangePickerListener, DataSetPickerFragment.DataSetPickerListener {
    public static String TAG = "GRAPH_FRAG";
    public static String TITLE = "Graph";

    private String type = "N/A";
    private LineChart chart = null;
    private HealthDroidDatabaseHelper db = null;
    private int formatter_choice;
    private List<String> dataSetChoices = null;
    private DataEntryFormatter formatter = null;
    private LineChartAdapter chartAdapter = null;

    public GraphFragment() {
        super();
        formatter_choice = 0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        setRetainInstance(true);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.content_graph, container, false);

        //Set title
        TitleUtil.setSupportActionBarTitle(getActivity(), TITLE);

        chart = new LineChart(inflater.getContext());
        chart.setDescription(getDescription());
        chartAdapter = new LineChartAdapter(chart);
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

        db = HealthDroidDatabaseHelper.getInstance(getActivity());

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

        onTimeRangePicked(formatter_choice);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.graph_menu, menu);
        menu.findItem(R.id.time_range).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                TimeRangeDialogFragment dialogFragment = new TimeRangeDialogFragment();
                dialogFragment.listener = GraphFragment.this;
                dialogFragment.show(getActivity().getSupportFragmentManager(), TAG);
                return true;
            }
        });
        menu.findItem(R.id.data_sets).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DataSetPickerFragment dialogFragment = DataSetPickerFragment.getInstance(dataSetChoices);
                dialogFragment.listener = GraphFragment.this;
                dialogFragment.show(getActivity().getSupportFragmentManager(), TAG);
                return true;
            }
        });
    }

    @Override
    public void onDataSetPicked(List<String> items) {
        chartAdapter.showDataSetsByLabel(items);
    }

    @Override
    public void onTimeRangePicked(int timeRange) {
        //TODO: retain the previous choice here
        formatter_choice = timeRange;
        switch (TimeRangeDialogFragment.choices[timeRange]) {
            case TimeRangeDialogFragment.DAY:
                new DisplayDataByDayTask().execute();
                break;
            case TimeRangeDialogFragment.WEEK:
                new DisplayDataByWeekTask().execute();
                break;
            case TimeRangeDialogFragment.MONTH:
                new DisplayDataByMonthTask().execute();
                break;
        }
    }

    public String getDescription() {
        return "BaseGraph";
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
            formatter = getDataEntryFormatter(cursor, chartAdapter);
            //TODO: The 2 following lines need to be executed in order, may need to refactor this
            formatter.format(chart);
            dataSetChoices = formatter.getDataSetLabels();
            chart.invalidate();
        }

        abstract DataEntryFormatter getDataEntryFormatter(Cursor cursor, ChartAdapter chartAdapter);
    }

    private class DisplayDataByDayTask extends DisplayDataTask {
        @Override
        DataEntryFormatter getDataEntryFormatter(Cursor cursor, ChartAdapter chartAdapter) {
            return new DataEntryByDayFormatter(cursor, chartAdapter);
        }
    }

    private class DisplayDataByWeekTask extends DisplayDataTask {
        @Override
        DataEntryFormatter getDataEntryFormatter(Cursor cursor, ChartAdapter chartAdapter) {
            return new DataEntryByWeekFormatter(cursor, chartAdapter);
        }
    }

    private class DisplayDataByMonthTask extends DisplayDataTask {
        @Override
        DataEntryFormatter getDataEntryFormatter(Cursor cursor, ChartAdapter chartAdapter) {
            return new DataEntryByMonthFormatter(cursor, chartAdapter);
        }
    }

}
