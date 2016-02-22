package com.ntu.phongnt.healthdroid.graph.util.graphmanager;

import android.database.Cursor;

import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.graph.util.chartadapter.ChartAdapter;
import com.ntu.phongnt.healthdroid.graph.util.keycreator.KeyCreator;

public class GraphManager {
    Cursor cursor;
    DataPool dataPool;
    KeyCreator keyCreator;

    public GraphManager(Cursor cursor) {
        this.cursor = cursor;
    }

    public void accumulateDataByUser() {
        dataPool.accumulate();
    }

    public void getDataByUser() {
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_DATE));
                String value = cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_VALUE));
                String user = cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_USER));

                dataPool.addAccumulatorDataEntry(date, value, user);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
    }

    @SuppressWarnings("ResourceType")
    public void addDataToChart(ChartAdapter chartAdapter) {
        getDataByUser();
        dataPool.accumulate();
        dataPool.insertToChart(chartAdapter);
    }

    public KeyCreator getKeyCreator() {
        return keyCreator;
    }

    public void setKeyCreator(KeyCreator keyCreator) {
        this.keyCreator = keyCreator;
    }

    public DataPool getDataPool() {
        return dataPool;
    }

    public void setDataPool(DataPool dataPool) {
        this.dataPool = dataPool;
        this.dataPool.setKeyCreator(keyCreator);
    }
}
