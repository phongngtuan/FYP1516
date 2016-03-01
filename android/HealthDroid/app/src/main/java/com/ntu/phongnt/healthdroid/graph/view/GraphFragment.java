package com.ntu.phongnt.healthdroid.graph.view;

import android.content.Context;
import android.database.Cursor;
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
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.graph.dialogs.DataSetPickerFragment;
import com.ntu.phongnt.healthdroid.graph.dialogs.TimeRangeDialogFragment;
import com.ntu.phongnt.healthdroid.graph.util.TitleUtil;
import com.ntu.phongnt.healthdroid.graph.util.chartadapter.ChartAdapter;
import com.ntu.phongnt.healthdroid.graph.util.graphmanager.DataPool;
import com.ntu.phongnt.healthdroid.graph.util.graphmanager.GraphManager;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.ByDayKeyCreator;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.ByMonthKeyCreator;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.ByWeekKeyCreator;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.KeyCreator;

import java.util.ArrayList;
import java.util.List;

public abstract class GraphFragment extends Fragment implements
        TimeRangeDialogFragment.TimeRangePickerListener, DataSetPickerFragment.DataSetPickerListener {
    public static String TAG = "GRAPH_FRAG";
    public static String TITLE = "Graph";

    private String type = "N/A";
    private Chart chart;
    private ChartAdapter chartAdapter;
    private HealthDroidDatabaseHelper db;
    private int formatter_choice;
    private List<String> dataSetChoices = new ArrayList<>();

    public GraphFragment() {
        super();
        formatter_choice = 0;
    }

    public ChartAdapter getChartAdapter() {
        return chartAdapter;
    }

    public void setChartAdapter(ChartAdapter chartAdapter) {
        this.chartAdapter = chartAdapter;
    }

    public List<String> getDataSetChoices() {
        return dataSetChoices;
    }

    public void setDataSetChoices(List<String> dataSetChoices) {
        this.dataSetChoices = dataSetChoices;
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
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.content_graph, container, false);

        //Set title
        TitleUtil.setSupportActionBarTitle(getActivity(), TITLE);

        db = HealthDroidDatabaseHelper.getInstance(getActivity());

        chart = makeChart(inflater.getContext());
        List<String> xVals = chart.getData().getXVals();
        chart.setMarkerView(new DateMarkerView(getContext(), R.layout.date_marker_view, xVals));
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
                DataSetPickerFragment dialogFragment = DataSetPickerFragment.getInstance(chartAdapter.getDataSetLabels());
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

    abstract public String getDescription();

    abstract protected Cursor getQuery(HealthDroidDatabaseHelper db);

    abstract protected Chart makeChart(Context context);

    abstract protected DataPool makeDataPool();

    abstract private class GetCursorTask<T> extends AsyncTask<T, Void, Cursor> {
        protected Cursor doQuery(HealthDroidDatabaseHelper db) {
            Cursor result = getQuery(db);
            Log.i("GraphFragment", "cursor return " + result.getCount() + " records");
            return (result);
        }
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
            graphManager.addDataToChart(getChartAdapter());
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
