package com.ntu.phongnt.healthdroid.graph.util.bloodpressure;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BloodPressureDataAccumulatorTest {
    BloodPressureDataAccumulator accumulator = new BloodPressureDataAccumulator("test_accumulator", null);

    private String systolic = "123";
    private String diastolic = "90";
    private String value = systolic + "/" + diastolic;
    private String key = "15/2015";
    private String otherKey = "16/2015";

    @Test
    public void testMakeEntry() {
        BloodPressureDataAccumulator.BloodPressureEntry bloodPressureEntry = accumulator.makeEntry(value);
        assertEquals(Float.valueOf(systolic), bloodPressureEntry.getSystolic());
        assertEquals(Float.valueOf(diastolic), bloodPressureEntry.getDiastolic());
    }

    @Test
    public void testAccumulate() {
        accumulator.accumulate(value, key);
        accumulator.accumulate("100/90", otherKey);
        accumulator.accumulate("120/80", otherKey);

        BloodPressureDataAccumulator.BloodPressureEntry firstValue
                = new BloodPressureDataAccumulator.BloodPressureEntry(123f, 90f);
        assertEquals(firstValue, accumulator.reducedData.get(key));

        BloodPressureDataAccumulator.BloodPressureEntry otherValue
                = new BloodPressureDataAccumulator.BloodPressureEntry(220f, 170f);
        assertEquals(220f, accumulator.reducedData.get(otherKey).getSystolic(), 0.01);
        assertEquals(170f, accumulator.reducedData.get(otherKey).getDiastolic(), 0.01);
    }
}