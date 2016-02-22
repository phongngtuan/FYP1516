package com.ntu.phongnt.healthdroid.graph.util.graphmanager;

import com.ntu.phongnt.healthdroid.graph.util.keycreator.KeyCreator;

import java.util.Collection;
import java.util.List;

public abstract class DataAccumulator {
    String label;
    KeyCreator keyCreator;

    public DataAccumulator(String label, KeyCreator keyCreator) {
        this.label = label;
        this.keyCreator = keyCreator;
    }

    public void accumulateAndAverage(List<AccumulatorDataEntry> dataOfUser) {
        accumulate(dataOfUser);
        averageResult();
    }

    public void accumulate(Collection<AccumulatorDataEntry> data) {
        for (AccumulatorDataEntry d : data) {
            String key = keyCreator.createKey(d.date);
            accumulate(d.value, key);
        }
    }

    public abstract void accumulate(String value, String key);

    public abstract void averageResult();

    public abstract String getFirstKey();

    public abstract String getLastKey();

    public String getLabel() {
        return label;
    }
}
