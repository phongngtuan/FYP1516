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
        String TYPE = DataContract.DataEntry.COLUMN_NAME_TYPE;

        //TODO: Hard coded value for dev purpose, change later
        cv.put(TYPE, 0);
        cv.put(USER, "phongnt.ptnk@gmail.com");

        cv.put(VALUE, 1.5);
        cv.put(DATE, "2015-01-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-02-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2.5);
        cv.put(DATE, "2015-03-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        //Multiple entries in a month
        cv.put(VALUE, 2);
        cv.put(DATE, "2015-04-01T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-04-03T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-04-05T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-04-07T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-04-08T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-04-10T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-04-15T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-04-20T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-04-22T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-04-27T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        //Month data ends here
        cv.put(VALUE, 2.5);
        cv.put(DATE, "2015-05-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 4);
        cv.put(DATE, "2015-06-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 3.5);
        cv.put(DATE, "2015-07-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2.75);
        cv.put(DATE, "2015-08-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 3.5);
        cv.put(DATE, "2015-09-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 4);
        cv.put(DATE, "2015-10-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 5);
        cv.put(DATE, "2015-11-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 4);
        cv.put(DATE, "2015-12-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);
        /* First user data ends */

        /* Second user data starts */
        cv.put(USER, "email@example.com");

        cv.put(VALUE, 2);
        cv.put(DATE, "2015-01-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2.5);
        cv.put(DATE, "2015-02-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2.25);
        cv.put(DATE, "2015-03-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        //Multiple entries in a month
        cv.put(VALUE, 3);
        cv.put(DATE, "2015-04-01T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2.5);
        cv.put(DATE, "2015-04-03T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2.75);
        cv.put(DATE, "2015-04-05T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 3);
        cv.put(DATE, "2015-04-07T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 4);
        cv.put(DATE, "2015-04-08T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 5);
        cv.put(DATE, "2015-04-10T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 4.5);
        cv.put(DATE, "2015-04-15T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 4);
        cv.put(DATE, "2015-04-20T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 3);
        cv.put(DATE, "2015-04-22T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 3.5);
        cv.put(DATE, "2015-04-27T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        //Month data ends here
        cv.put(VALUE, 2.5);
        cv.put(DATE, "2015-05-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 4.5);
        cv.put(DATE, "2015-06-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 3.5);
        cv.put(DATE, "2015-07-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 3);
        cv.put(DATE, "2015-08-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 2.75);
        cv.put(DATE, "2015-09-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 4);
        cv.put(DATE, "2015-10-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 3.25);
        cv.put(DATE, "2015-11-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, 3.5);
        cv.put(DATE, "2015-12-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);
        /*Second user data ends */

        /* Blood pressure data */
        cv.put(TYPE, 1);

        cv.put(USER, "phongnt.ptnk@gmail.com");

        cv.put(VALUE, "80/120");
        cv.put(DATE, "2015-12-24T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, "70/110");
        cv.put(DATE, "2015-12-25T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, "80/110");
        cv.put(DATE, "2015-12-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, "70/100");
        cv.put(DATE, "2015-12-23T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(USER, "email@example.com");

        cv.put(VALUE, "90/130");
        cv.put(DATE, "2015-12-24T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, "100/120");
        cv.put(DATE, "2015-12-25T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, "90/110");
        cv.put(DATE, "2015-12-26T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);

        cv.put(VALUE, "110/140");
        cv.put(DATE, "2015-12-23T03:37:48.038+07:00");
        db.insert(TABLE, VALUE, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("How did we get here?");
    }

}
