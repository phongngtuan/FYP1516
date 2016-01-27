package com.ntu.phongnt.healthdroid.graph.util.formatter;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;

public class DataAccumulator {
    TreeMap<String, Float> reducedData;
    TreeMap<String, Integer> reducedDataCount;
    String label;
    KeyCreator keyCreator;

    public DataAccumulator(String label, KeyCreator keyCreator) {
        this.label = label;
        this.keyCreator = keyCreator;

        reducedData = new TreeMap<>(new DateComparator());

        reducedDataCount = new TreeMap<>();
    }

    public void accumulate(Collection<DataEntry> data) {
        for (DataEntry d : data) {
            String key = keyCreator.createKey(d.date);
            accumulate(d.value, key);
        }
    }

    public void accumulate(Float value, String key) {
        if (!reducedData.containsKey(key)) {
            reducedData.put(key, value);
            reducedDataCount.put(key, 1);
        } else {
            reducedData.put(key, value + reducedData.get(key));
            reducedDataCount.put(key, 1 + reducedDataCount.get(key));
        }
    }

    public static class DataEntry {
        public String date;
        public Float value;
        public String user;

        public DataEntry(String date, Float value, String user) {
            this.date = date;
            this.value = value;
            this.user = user;
        }
    }

    public static class DateComparator implements Comparator<String> {
        @Override
        public int compare(String lhs, String rhs) {
            String[] lhsKeys = lhs.split("/");
            String[] rhsKeys = rhs.split("/");

            for (int i = lhsKeys.length - 1; i >= 0; i--) {
                int lhsValue = Integer.parseInt(lhsKeys[i]);
                int rhsValue = Integer.parseInt(rhsKeys[i]);
                if (lhsValue != rhsValue)
                    return lhsValue - rhsValue;
            }
            return 0;
        }
    }
}
