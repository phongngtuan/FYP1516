package com.ntu.phongnt.healthdroid.graph.util.keycreator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ByDayKeyCreatorTest {

    ByDayKeyCreator byDayKeyCreator = new ByDayKeyCreator();

    @Test
    public void testCreateKey() throws Exception {
        assertEquals("23/12/2016", byDayKeyCreator.createKey("2016-12-23T02:08:50.889+07:00"));
    }
}