package com.ntu.phongnt.healthdroid.db.data;

import android.content.ContentValues;
import android.provider.BaseColumns;

public final class DataContract {
    private DataHelper dataHelper;

    public DataContract(DataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    public long addData(Float value, String date, String user) {
        ContentValues values = new ContentValues();
        values.put(DataContract.DataEntry.COLUMN_NAME_VALUE, value);
        values.put(DataContract.DataEntry.COLUMN_NAME_DATE, date);
        values.put(DataContract.DataEntry.COLUMN_NAME_USER, user);
        return dataHelper
                .getWritableDatabase()
                .insert(
                        DataContract.DataEntry.TABLE_NAME,
                        DataContract.DataEntry.COLUMN_NAME_DATE,
                        values);
    }

    public class DataEntry implements BaseColumns {
        public static final String TABLE_NAME = "data";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_USER = "user";

        public String value;
        public String date;
        public String user;

        public DataEntry(String value, String date, String user) {
            this.value = value;
            this.date = date;
            this.user = user;
        }
    }
}
