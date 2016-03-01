package com.ntu.phongnt.healthdroid.graph.util.simple;

import com.ntu.phongnt.healthdroid.graph.util.DateRange;
import com.ntu.phongnt.healthdroid.graph.util.chartadapter.ChartAdapter;
import com.ntu.phongnt.healthdroid.graph.util.graphmanager.DataAccumulator;
import com.ntu.phongnt.healthdroid.graph.util.graphmanager.DataPool;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.KeyCreator;

import java.text.DateFormat;
import java.util.GregorianCalendar;

public class SimpleDataPool extends DataPool {
    @Override
    protected DataAccumulator makeDataAccumulator(String label, KeyCreator keyCreator) {
        return new SimpleDataAccumulator(label, keyCreator);
    }

    @SuppressWarnings("ResourceType")
    public void insertToChart(ChartAdapter chartAdapter) {
        chartAdapter.clearDataSets();

        DateRange range = findRange();
        GregorianCalendar first = new GregorianCalendar();
        first.setTime(range.getFirst().getTime());

        GregorianCalendar last = new GregorianCalendar();
        last.setTime(range.getLast().getTime());

        DateFormat dateFormat = getKeyCreator().getDateFormat();

        int index = 0;
        while (first.before(last)) {
            String key = dateFormat.format(first.getTime());
            chartAdapter.addXValue(key);

            for (DataAccumulator accumulator : getReducedData().values()) {
                SimpleDataAccumulator acc = (SimpleDataAccumulator) accumulator;
                if (acc.reducedData.containsKey(key)) {
                    chartAdapter.addEntry(
                            acc.getLabel(),
                            acc.reducedData.get(key),
                            index
                    );
                }
            }
            first.add(getTimeUnit(), 1);
            index++;
        }
    }

}
