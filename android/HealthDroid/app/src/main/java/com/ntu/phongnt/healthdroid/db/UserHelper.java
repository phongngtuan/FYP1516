package com.ntu.phongnt.healthdroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_USERS =
            "CREATE TABLE " + UserContract.User.TABLE_NAME + " (" +
                    UserContract.User._ID + " INTEGER PRIMARY KEY, " +
                    UserContract.User.COLUMN_NAME_EMAIL + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_USERS =
            "DROP TABLE IF EXISTS " + UserContract.User.TABLE_NAME;

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
        Log.d("DBHelper", "Creating User table");
        db.execSQL(SQL_CREATE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
