package com.ntu.phongnt.healthdroid.graph.util.keycreator;

import com.ntu.phongnt.healthdroid.graph.util.DateRange;

import java.text.DateFormat;

public interface KeyCreator {
    String createKey(String date);

    DateRange getDateRange(String firstDate, String lastDate);

    DateFormat getDateFormat();

    int getTimeUnit();
}
