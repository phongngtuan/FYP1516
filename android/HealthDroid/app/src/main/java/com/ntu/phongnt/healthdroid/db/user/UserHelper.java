package com.ntu.phongnt.healthdroid.db.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ntu.phongnt.healthdroid.db.Constants;
import com.ntu.phongnt.healthdroid.graph.util.DateHelper;

public class UserHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATE";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_USERS =
            "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                    UserContract.UserEntry._ID + " INTEGER PRIMARY KEY, " +
                    UserContract.UserEntry.COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
                    UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS + INTEGER_TYPE + COMMA_SEP +
                    UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED + DATE_TYPE +
                    " )";
    private static final String SQL_DELETE_USERS =
            "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;

    private static UserHelper userHelper = null;

    private UserHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    public static synchronized UserHelper getInstance(Context context) {
        if (userHelper == null)
            userHelper = new UserHelper(context.getApplicationContext());
        return userHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHelper", "Creating UserEntry table");
        db.execSQL(SQL_CREATE_USERS);

        ContentValues cv = new ContentValues();
        String TABLE = UserContract.UserEntry.TABLE_NAME;
        String EMAIL = UserContract.UserEntry.COLUMN_NAME_EMAIL;
        //TODO: Hard coded again, fix later. Currently, this entry will get flushed anyway
        cv.put(UserContract.UserEntry.COLUMN_NAME_EMAIL, "otacon.ptnk@gmail.com");
        cv.put(UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS, UserContract.UserEntry.UNSUBSCRIBED);
        cv.put(UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED, DateHelper.ZERO_DATE);
        db.insert(TABLE, EMAIL, cv);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
