package com.ntu.phongnt.healthdroid.graph.util.bloodpressure;

import com.ntu.phongnt.healthdroid.graph.util.graphmanager.DataAccumulator;
import com.ntu.phongnt.healthdroid.graph.util.graphmanager.DateComparator;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.KeyCreator;

import java.util.TreeMap;

public class BloodPressureDataAccumulator extends DataAccumulator {
    TreeMap<String, BloodPressureEntry> reducedData;
    TreeMap<String, Integer> reducedDataCount;

    public BloodPressureDataAccumulator(String label, KeyCreator keyCreator) {
        super(label, keyCreator);
        this.reducedData = new TreeMap<>(new DateComparator());
        this.reducedDataCount = new TreeMap<>();
    }

    @Override
    public void accumulate(String value, String key) {
        BloodPressureEntry bloodPressureEntry = makeEntry(value);
        if (!reducedData.containsKey(key)) {
            reducedData.put(key, bloodPressureEntry);
            reducedDataCount.put(key, 1);
        } else {
            reducedData.put(key, reducedData.get(key).add(bloodPressureEntry));
            reducedDataCount.put(key, reducedDataCount.get(key) + 1);
        }
    }

    @Override
    public void averageResult() {
        for (String key : reducedData.keySet()) {
            int count = reducedDataCount.get(key);
            BloodPressureEntry average = reducedData.get(key).divides(count);
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

    protected BloodPressureEntry makeEntry(String value) {
        String[] valuePair = value.split("/");
        Float systolic = Float.valueOf(valuePair[0]);
        Float diastolic = Float.valueOf(valuePair[1]);
        return new BloodPressureEntry(systolic, diastolic);
    }

    public static class BloodPressureEntry {
        private Float systolic;
        private Float diastolic;

        public BloodPressureEntry(Float systolic, Float diastolic) {
            this.setSystolic(systolic);
            this.setDiastolic(diastolic);
        }

        public BloodPressureEntry add(BloodPressureEntry that) {
            return new BloodPressureEntry(this.getSystolic() + that.getSystolic(), this.getDiastolic() + that.getDiastolic());
        }

        public BloodPressureEntry divides(int count) {
            return new BloodPressureEntry(this.getSystolic() / count, this.getDiastolic() / count);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof BloodPressureEntry && ((BloodPressureEntry) o).getSystolic().equals(getSystolic()) && ((BloodPressureEntry) o).getDiastolic().equals(getDiastolic());
        }

        public Float getDiastolic() {
            return diastolic;
        }

        public void setDiastolic(Float diastolic) {
            this.diastolic = diastolic;
        }

        public Float getSystolic() {
            return systolic;
        }

        public void setSystolic(Float systolic) {
            this.systolic = systolic;
        }
    }
}
