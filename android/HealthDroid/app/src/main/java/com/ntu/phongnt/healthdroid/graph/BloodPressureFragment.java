package com.ntu.phongnt.healthdroid.graph;

import android.database.Cursor;

import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.data.DataContract;

public class BloodPressureFragment extends GraphFragment {
    @Override
    public String getDescription() {
        return "Blood Pressure";
    }

    @Override
    public String getType() {
        return "Blood Pressure";
    }

    @Override
    protected Cursor getQuery(HealthDroidDatabaseHelper db) {
        return db
                .getReadableDatabase()
                .query(DataContract.DataEntry.TABLE_NAME,
                        new String[]{DataContract.DataEntry._ID,
                                DataContract.DataEntry.COLUMN_NAME_VALUE,
                                DataContract.DataEntry.COLUMN_NAME_DATE,
                                DataContract.DataEntry.COLUMN_NAME_USER,
                                DataContract.DataEntry.COLUMN_NAME_TYPE
                        },
                        DataContract.DataEntry.COLUMN_NAME_TYPE + " = 1",
                        null,
                        null,
                        null,
                        DataContract.DataEntry.COLUMN_NAME_DATE);
    }
}
