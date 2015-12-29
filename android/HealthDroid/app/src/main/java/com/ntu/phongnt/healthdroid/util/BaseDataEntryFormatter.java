package com.ntu.phongnt.healthdroid.util;

import android.database.Cursor;

import com.ntu.phongnt.healthdroid.db.DataHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public abstract class BaseDataEntryFormatter implements DataEntryFormatter {
    Cursor cursor = null;

    @Override
    public List<DateHelper.DataEntry> prepareData() {
        List<DateHelper.DataEntry> data = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String createdAt = cursor.getString(cursor.getColumnIndex(DataHelper.CREATED_AT));
                float value = cursor.getFloat(cursor.getColumnIndex(DataHelper.VALUE));
                data.add(new DateHelper.DataEntry(createdAt, value));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    @Override
    public void accumulate(TreeMap<String, Float> reducedData, TreeMap<String, Integer> reducedDataCount, Float value, String key) {
        if (!reducedData.containsKey(key)) {
            reducedData.put(key, value);
            reducedDataCount.put(key, 1);
        } else {
            reducedData.put(key, value + reducedData.get(key));
            reducedDataCount.put(key, 1 + reducedDataCount.get(key));
        }
    }
}
