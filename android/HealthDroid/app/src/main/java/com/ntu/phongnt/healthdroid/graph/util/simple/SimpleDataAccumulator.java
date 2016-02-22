package com.ntu.phongnt.healthdroid.graph.util.simple;

import com.ntu.phongnt.healthdroid.graph.util.graphmanager.DataAccumulator;
import com.ntu.phongnt.healthdroid.graph.util.graphmanager.DateComparator;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.KeyCreator;

import java.util.TreeMap;

public class SimpleDataAccumulator extends DataAccumulator {
    TreeMap<String, Float> reducedData;
    TreeMap<String, Integer> reducedDataCount;

    public SimpleDataAccumulator(String label, KeyCreator keyCreator) {
        super(label, keyCreator);
        reducedData = new TreeMap<>(new DateComparator());
        reducedDataCount = new TreeMap<>();
    }

    @Override
    public void accumulate(String value, String key) {
        Float fvalue = Float.valueOf(value);
        if (!reducedData.containsKey(key)) {
            reducedData.put(key, fvalue);
            reducedDataCount.put(key, 1);
        } else {
            reducedData.put(key, fvalue + reducedData.get(key));
            reducedDataCount.put(key, 1 + reducedDataCount.get(key));
        }
    }

    @Override
    public void averageResult() {
        for (String key : reducedData.keySet()) {
            int count = reducedDataCount.get(key);
            float average = reducedData.get(key) / count;
            reducedData.put(key, average);
            reducedDataCount.put(key, 1);
        }
    }

    @Override
    public String getFirstKey() {
        return reducedData.firstKey();
    }

    @Override
    public String getLastKey() {
        return reducedData.lastKey();
    }
}
