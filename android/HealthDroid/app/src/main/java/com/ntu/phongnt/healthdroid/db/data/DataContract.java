package com.ntu.phongnt.healthdroid.db.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

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

    public static final int SIMPLE_TYPE = 0;
    public static final int BLOOD_PRESSURE_TYPE = 1;

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

    public long deleteDataOfUser(String user) {
        return healthDroidDatabaseHelper
                .getWritableDatabase()
                .delete(
                        DataEntry.TABLE_NAME,
                        DataEntry.COLUMN_NAME_USER + "=?",
                        new String[]{user}
                );
    }

    public List<DataEntry> getData(String selection, String[] selectionArgs) {
        List<DataEntry> dataList = new ArrayList<>();
        Cursor cursor = healthDroidDatabaseHelper
                .getReadableDatabase()
                .query(
                        DataEntry.TABLE_NAME,
                        getColumns(),
                        selection,
                        selectionArgs,
                        null,
                        null,
                        DataEntry.COLUMN_NAME_DATE
                );

        if (cursor.moveToFirst()) {
            do {
                String user = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_USER));
                String date = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_DATE));
                String value = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_VALUE));
                int type = cursor.getType(cursor.getColumnIndex(DataEntry.COLUMN_NAME_TYPE));
                dataList.add(new DataEntry(value, date, user, type));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return dataList;
    }

    public List<DataEntry> getAllData() {
        return getData(null, null);
    }

    public List<DataEntry> getDataWithType(int type) {
        String selection = DataEntry.COLUMN_NAME_TYPE + "=?";
        String[] selectionArgs = new String[]{String.valueOf(type)};
        return getData(selection, selectionArgs);
    }

    public String[] getColumns() {
        return new String[]{
                DataEntry.COLUMN_NAME_VALUE,
                DataEntry.COLUMN_NAME_DATE,
                DataEntry.COLUMN_NAME_USER,
                DataEntry.COLUMN_NAME_TYPE
        };
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
        public int type;

        public DataEntry(String value, String date, String user, int type) {
            this.value = value;
            this.date = date;
            this.user = user;
            this.type = type;
        }


        public DataEntry(String value, String date, String user) {
            this(value, date, user, 0);
        }
    }
}
