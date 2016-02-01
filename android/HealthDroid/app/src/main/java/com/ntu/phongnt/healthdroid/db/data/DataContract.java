package com.ntu.phongnt.healthdroid.db.data;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;

public final class DataContract {

    public static final String SQL_CREATE_DATA_TABLE =
            "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +
                    DataEntry._ID + HealthDroidDatabaseHelper.INTEGER_PRIMARY_KEY +
                    DataEntry.COLUMN_NAME_VALUE + HealthDroidDatabaseHelper.REAL_TYPE + HealthDroidDatabaseHelper.COMMA_SEP +
                    DataEntry.COLUMN_NAME_DATE + HealthDroidDatabaseHelper.DATE_TYPE + HealthDroidDatabaseHelper.COMMA_SEP +
                    DataEntry.COLUMN_NAME_USER + HealthDroidDatabaseHelper.TEXT_TYPE +
                    " )";
    public static final String SQL_DELETE_DATA_TABLE =
            "DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME;

    private HealthDroidDatabaseHelper healthDroidDatabaseHelper;

    public DataContract(HealthDroidDatabaseHelper healthDroidDatabaseHelper) {
        this.healthDroidDatabaseHelper = healthDroidDatabaseHelper;
    }

    public long addData(Float value, String date, String user) {
        ContentValues values = new ContentValues();
        values.put(DataContract.DataEntry.COLUMN_NAME_VALUE, value);
        values.put(DataContract.DataEntry.COLUMN_NAME_DATE, date);
        values.put(DataContract.DataEntry.COLUMN_NAME_USER, user);
        return healthDroidDatabaseHelper
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
