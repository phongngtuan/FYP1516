package com.ntu.phongnt.healthdroid.graph.util.graphmanager;

import com.ntu.phongnt.healthdroid.graph.util.DateRange;
import com.ntu.phongnt.healthdroid.graph.util.chartadapter.ChartAdapter;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.KeyCreator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public abstract class DataPool {
    TreeMap<String, List<AccumulatorDataEntry>> dataByUser = new TreeMap<>();
    TreeMap<String, DataAccumulator> reducedData = new TreeMap<>();
    KeyCreator keyCreator;

    public TreeMap<String, DataAccumulator> getReducedData() {
        return reducedData;
    }

    public void addAccumulatorDataEntry(String date, String value, String user) {
        List<AccumulatorDataEntry> userDataList;
        if (!dataByUser.containsKey(user))
            userDataList = new ArrayList<>();
        else
            userDataList = dataByUser.get(user);
        userDataList.add(new AccumulatorDataEntry(date, value, user));
        dataByUser.put(user, userDataList);
    }

    public void accumulate() {
        for (String user : dataByUser.keySet()) {
            List<AccumulatorDataEntry> dataOfUser = dataByUser.get(user);
            DataAccumulator dataAccumulator = makeDataAccumulator(user, keyCreator);
            dataAccumulator.accumulateAndAverage(dataOfUser);
            reducedData.put(user, dataAccumulator);
        }
    }

    public DateRange findRange() {
        Comparator<String> comp = new DateComparator();
        String globalFirst = null;
        String globalLast = null;
        for (DataAccumulator accumulator : reducedData.values()) {
            String first = accumulator.getFirstKey();
            String last = accumulator.getLastKey();
            if (globalFirst == null || comp.compare(first, globalFirst) < 0)
                globalFirst = first;
            if (globalLast == null || comp.compare(last, globalLast) > 0)
                globalLast = last;
        }
        return keyCreator.getDateRange(globalFirst, globalLast);
    }

    protected abstract DataAccumulator makeDataAccumulator(String label, KeyCreator keyCreator);

    public abstract void insertToChart(ChartAdapter chartAdapter);

    public KeyCreator getKeyCreator() {
        return keyCreator;
    }

    public void setKeyCreator(KeyCreator keyCreator) {
        this.keyCreator = keyCreator;
    }

    public int getTimeUnit() {
        return keyCreator.getTimeUnit();
    }
}
