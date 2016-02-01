package com.ntu.phongnt.healthdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.db.user.UserContract;

public class HealthDroidDatabaseHelper extends SQLiteOpenHelper {
    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String DATE_TYPE = " DATE";
    public static final String REAL_TYPE = " REAL";
    public static final String COMMA_SEP = ",";
    public static final String INTEGER_PRIMARY_KEY = " INTEGER PRIMARY KEY, ";

    public static final String ZERO_TIME = "2000-12-19T02:08:50.889+07:00";
    public static final String LAST_UPDATED = "last_updated";

    private static HealthDroidDatabaseHelper healthDroidDatabaseHelper;

    public HealthDroidDatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    public HealthDroidDatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, Constants.DATABASE_VERSION);
    }

    public static synchronized HealthDroidDatabaseHelper getInstance(Context context) {
        if (healthDroidDatabaseHelper == null)
            healthDroidDatabaseHelper = new HealthDroidDatabaseHelper(context.getApplicationContext());
        return healthDroidDatabaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHelper", "Inserting into the database");
        db.execSQL(DataContract.SQL_CREATE_DATA_TABLE);
        db.execSQL(UserContract.SQL_CREATE_USERS_TABLE);
        fillDummyData(db);
    }

    private void fillDummyData(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        String TABLE = DataContract.DataEntry.TABLE_NAME;
        String VALUE = DataContract.DataEntry.COLUMN_NAME_VALUE;
        String DATE = DataContract.DataEntry.COLUMN_NAME_DATE;
        String USER = DataContract.DataEntry.COLUMN_NAME_USER;

        //TODO: Hard coded value for dev purpose, change later
        cv.put(VALUE, 1);
        cv.put(DATE, "2015-10-26T03:37:48.038+07:00");
        cv.put(USER, "phongnt.ptnk@gmail.com");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-11-26T03:37:48.038+07:00");
        cv.put(USER, "phongnt.ptnk@gmail.com");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 4);
        cv.put(DATE, "2015-12-26T03:37:48.038+07:00");
        cv.put(USER, "phongnt.ptnk@gmail.com");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 8);
        cv.put(DATE, "2015-12-23T03:37:48.038+07:00");
        cv.put(USER, "phongnt.ptnk@gmail.com");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 8);
        cv.put(DATE, "2015-12-24T03:37:48.038+07:00");
        cv.put(USER, "phongnt.ptnk@gmail.com");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 8);
        cv.put(DATE, "2015-12-25T03:37:48.038+07:00");
        cv.put(USER, "phongnt.ptnk@gmail.com");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 6);
        cv.put(DATE, "2015-12-26T03:37:48.038+07:00");
        cv.put(USER, "email@example.com");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 10);
        cv.put(DATE, "2015-12-23T03:37:48.038+07:00");
        cv.put(USER, "email@example.com");
        db.insert(TABLE, VALUE, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("How did we get here?");
    }

}
