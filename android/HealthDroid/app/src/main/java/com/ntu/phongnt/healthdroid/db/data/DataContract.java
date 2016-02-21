package com.ntu.phongnt.healthdroid.db.data;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;

public final class DataContract {

    public static final String SQL_CREATE_DATA_TABLE =
            "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +
                    DataEntry._ID + HealthDroidDatabaseHelper.INTEGER_PRIMARY_KEY +
                    DataEntry.COLUMN_NAME_VALUE + HealthDroidDatabaseHelper.TEXT_TYPE + HealthDroidDatabaseHelper.COMMA_SEP +
                    DataEntry.COLUMN_NAME_DATE + HealthDroidDatabaseHelper.DATE_TYPE + HealthDroidDatabaseHelper.COMMA_SEP +
                    DataEntry.COLUMN_NAME_USER + HealthDroidDatabaseHelper.TEXT_TYPE + HealthDroidDatabaseHelper.COMMA_SEP +
                    DataEntry.COLUMN_NAME_TYPE + HealthDroidDatabaseHelper.INTEGER_TYPE +
                    " )";
    public static final String SQL_DELETE_DATA_TABLE =
            "DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME;

    private HealthDroidDatabaseHelper healthDroidDatabaseHelper;

    public DataContract(HealthDroidDatabaseHelper healthDroidDatabaseHelper) {
        this.healthDroidDatabaseHelper = healthDroidDatabaseHelper;
    }

    public long addData(String value, String date, String user, int type) {
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_NAME_VALUE, value);
        values.put(DataEntry.COLUMN_NAME_DATE, date);
        values.put(DataEntry.COLUMN_NAME_USER, user);
        values.put(DataEntry.COLUMN_NAME_TYPE, type);
        return healthDroidDatabaseHelper
                .getWritableDatabase()
                .insert(
                        DataContract.DataEntry.TABLE_NAME,
                        DataContract.DataEntry.COLUMN_NAME_DATE,
                        values);
    }

    public long addData(String value, String date, String user) {
        return addData(value, date, user, 0);
    }

    public long deleteDataOfUser(String user) {
        return healthDroidDatabaseHelper
                .getWritableDatabase()
                .delete(
                        DataEntry.TABLE_NAME,
                        DataEntry.COLUMN_NAME_USER + "=?",
                        new String[]{user}
                );
    }

    public class DataEntry implements BaseColumns {
        public static final String TABLE_NAME = "data";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_USER = "user";
        public static final String COLUMN_NAME_TYPE = "type";

        public String value;
        public String date;
        public String user;
        public String type;

        public DataEntry(String value, String date, String user, String type) {
            this.value = value;
            this.date = date;
            this.user = user;
            this.type = type;
        }


        public DataEntry(String value, String date, String user) {
            this(value, date, user, "0");
        }
    }
}
