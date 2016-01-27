package com.ntu.phongnt.healthdroid.graph.util.formatter;

import android.database.Cursor;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.graph.util.DateRange;
import com.ntu.phongnt.healthdroid.graph.util.chart.ChartAdapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeMap;

public abstract class BaseDataEntryFormatter implements DataEntryFormatter, KeyCreator {
    private Cursor cursor = null;
    private TreeMap<String, List<DataAccumulator.DataEntry>> dataByUser = new TreeMap<>();
    private List<DataAccumulator> dataAccumulators = new ArrayList<>();
    private ChartAdapter chartAdapter = null;

    public BaseDataEntryFormatter(Cursor cursor, ChartAdapter chartAdapter) {
        this.cursor = cursor;
        this.chartAdapter = chartAdapter;
    }

    protected abstract DateFormat getDateFormat();

    protected abstract DateRange getDateRange(String first, String last);

    @Override
    public void format(LineChart chart) {
        getDataByUser();
        accumulateDataByUser();
        DateRange dateRange = findRange();
        addDataToChart(chart, dateRange);
    }

    @Override
    public List<String> getDataSetLabels() {
        return new ArrayList<>(dataByUser.keySet());
    }

    //TODO: Use correct type for chart
    @SuppressWarnings("ResourceType")
    private void addDataToChart(LineChart chart, DateRange dateRange) {
        chartAdapter.clearDataSets();

        GregorianCalendar first = new GregorianCalendar();
        first.setTime(dateRange.getFirst().getTime());

        GregorianCalendar last = new GregorianCalendar();
        last.setTime(dateRange.getLast().getTime());
        last.add(Calendar.DAY_OF_YEAR, 1);

        DateFormat format = getDateFormat();

        int index = 0;
        while (first.before(last)) {
            String key = format.format(first.getTime());
            chartAdapter.addXValue(key);

            for (DataAccumulator accumulator : dataAccumulators) {
                if (accumulator.reducedData.containsKey(key)) {
                    chartAdapter.addEntry(
                            accumulator.label,
                            (float) accumulator.reducedData.get(key) / accumulator.reducedDataCount.get(key),
                            index);
                }
                //TODO: remove the else clause to only display values in db i.e. nonzero
                else
                    chartAdapter.addEntry(accumulator.label, 0, index);
            }
            first.add(dateRange.getTimeUnit(), 1);
            index++;
        }
        Log.d("Add Data", "Iterated through " + index);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private DateRange findRange() {
        Comparator<String> comp = new DataAccumulator.DateComparator();
        String globalFirst = null;
        String globalLast = null;
        for (DataAccumulator acc : dataAccumulators) {
            String first = acc.reducedData.firstKey();
            String last = acc.reducedData.lastKey();
            if (globalFirst == null || comp.compare(first, globalFirst) < 0)
                globalFirst = first;
            if (globalLast == null || comp.compare(last, globalLast) > 0)
                globalLast = last;
        }
        return getDateRange(globalFirst, globalLast);
    }

    private void accumulateDataByUser() {
        for (String user : dataByUser.keySet()) {
            List<DataAccumulator.DataEntry> data = dataByUser.get(user);
            DataAccumulator accumulator = new DataAccumulator(user, this);
            accumulator.accumulate(data);
            dataAccumulators.add(accumulator);
        }
    }

    public void getDataByUser() {
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_DATE));
                float value = cursor.getFloat(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_VALUE));
                String user = cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_USER));

                List<DataAccumulator.DataEntry> userList;
                if (dataByUser.containsKey(user))
                    userList = dataByUser.get(user);
                else
                    userList = new ArrayList<>();
                userList.add(new DataAccumulator.DataEntry(date, value, user));
                dataByUser.put(user, userList);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
    }
}