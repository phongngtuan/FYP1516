package com.ntu.phongnt.healthdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataHelper extends SQLiteOpenHelper {
    public static final String VALUE = "value";
    public static final String CREATED_AT = "created_at";
    public static final String TABLE = "readings";
    public static final String RFC3339_TEMPLATE = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
    private static final String DATABASE_NAME = "healthdroid";
    private static final int DATABASE_VERSION = 1;
    private static DataHelper dataHelper;

    private DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DataHelper getInstance(Context context) {
        if (dataHelper == null)
            dataHelper = new DataHelper(context.getApplicationContext());
        return dataHelper;
    }

    public static int getMonth(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(RFC3339_TEMPLATE, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(date));
            return calendar.get(Calendar.MONTH);
        } catch (ParseException e) {
            Log.d("DataHelper", "Cannot parse string: " + date);
        }
        return -1;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHelper", "Inserting into the database");
        db.execSQL("CREATE TABLE readings (value REAL, created_at date)");
        ContentValues cv = new ContentValues();
        cv.put(VALUE, 1);
        cv.put(CREATED_AT, getDateTime());
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(CREATED_AT, getDateTime());
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 4);
        cv.put(CREATED_AT, getDateTime());
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 8);
        cv.put(CREATED_AT, getDateTime());
        db.insert(TABLE, VALUE, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("How did we get here?");
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                RFC3339_TEMPLATE, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}