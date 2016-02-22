package com.ntu.phongnt.healthdroid.graph.view;

import android.content.Context;
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

import com.github.mikephil.charting.charts.Chart;
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
import com.ntu.phongnt.healthdroid.graph.util.chartadapter.ChartAdapter;
import com.ntu.phongnt.healthdroid.graph.util.chartadapter.LineChartAdapter;
import com.ntu.phongnt.healthdroid.graph.util.graphmanager.DataPool;
import com.ntu.phongnt.healthdroid.graph.util.graphmanager.GraphManager;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.ByDayKeyCreator;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.ByMonthKeyCreator;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.ByWeekKeyCreator;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.KeyCreator;
import com.ntu.phongnt.healthdroid.graph.util.simple.SimpleDataPool;

import java.util.List;

public class GraphFragment extends Fragment implements
        TimeRangeDialogFragment.TimeRangePickerListener, DataSetPickerFragment.DataSetPickerListener {
    public static String TAG = "GRAPH_FRAG";
    public static String TITLE = "Graph";

    private String type = "N/A";
    private Chart chart = null;
    private HealthDroidDatabaseHelper db = null;
    private int formatter_choice;
    private List<String> dataSetChoices = null;

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

        db = HealthDroidDatabaseHelper.getInstance(getActivity());

        chart = makeChart(inflater.getContext());
        FrameLayout chart_container = (FrameLayout) view.findViewById(R.id.chart_container);
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
//        chartAdapter.showDataSetsByLabel(items);
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
        protected Cursor doQuery(HealthDroidDatabaseHelper db) {
            Cursor result = getQuery(db);
            Log.i("GraphFragment", "cursor return " + result.getCount() + " records");
            return (result);
        }
    }

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

    protected Chart makeChart(Context context) {
        LineChart chart = new LineChart(context);
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

    protected DataPool makeDataPool() {
        return new SimpleDataPool();
    }

    protected ChartAdapter makeChartAdapter(Chart chart) {
        return new LineChartAdapter((LineChart) chart);
    }

    private abstract class DisplayDataTask extends GetCursorTask<String> {
        GraphManager graphManager = null;
        @Override
        protected Cursor doInBackground(String... params) {
            return (doQuery(db));
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            graphManager = new GraphManager(cursor);
            graphManager.setKeyCreator(makeKeyCreator());
            graphManager.setDataPool(makeDataPool());
            graphManager.addDataToChart(makeChartAdapter(chart));
            chart.invalidate();
        }

        protected abstract KeyCreator makeKeyCreator();
    }

    private class DisplayDataByDayTask extends DisplayDataTask {
        @Override
        protected KeyCreator makeKeyCreator() {
            return new ByDayKeyCreator();
        }
    }

    private class DisplayDataByWeekTask extends DisplayDataTask {
        @Override
        protected KeyCreator makeKeyCreator() {
            return new ByWeekKeyCreator();
        }
    }

    private class DisplayDataByMonthTask extends DisplayDataTask {
        @Override
        protected KeyCreator makeKeyCreator() {
            return new ByMonthKeyCreator();
        }
    }

}
