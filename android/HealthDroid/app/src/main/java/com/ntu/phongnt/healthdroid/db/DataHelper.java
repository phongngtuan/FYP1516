package com.ntu.phongnt.healthdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {
    public static final String VALUE = "value";
    public static final String TABLE = "readings";
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE readings (value REAL)");
        ContentValues cv = new ContentValues();
        cv.put(VALUE, 1);
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 4);
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 8);
        db.insert(TABLE, VALUE, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("How did we get here?");
    }
}
