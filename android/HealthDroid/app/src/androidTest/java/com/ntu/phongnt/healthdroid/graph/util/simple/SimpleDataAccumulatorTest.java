package com.ntu.phongnt.healthdroid.graph.util.simple;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleDataAccumulatorTest {
    SimpleDataAccumulator accumulator = new SimpleDataAccumulator("test_accumulator", null);

    private String value = "123";
    private String key = "15/2015";
    private String otherKey = "16/2015";

    @Test
    public void testAccumulate() {
        accumulator.accumulate(value, key);
        accumulator.accumulate("100", otherKey);
        accumulator.accumulate("120", otherKey);

        assertEquals(123f, accumulator.reducedData.get(key), 0.01);

        assertEquals(220f, accumulator.reducedData.get(otherKey), 0.01);
    }

}