package com.ntu.phongnt.healthdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper databaseHelper;
    private static final String DATABASE_NAME = "healthdroid";
    private static final int DATABASE_VERSION = 1;
    public static final String VALUE = "value";
    public static final String TABLE = "readings";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context){
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper(context.getApplicationContext());
        return databaseHelper;
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