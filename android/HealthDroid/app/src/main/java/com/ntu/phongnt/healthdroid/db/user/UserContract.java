package com.ntu.phongnt.healthdroid.db.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.ntu.phongnt.healthdroid.graph.util.DateHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class UserContract {

    private static final String TAG = "UserContract";
    private UserHelper db = null;

    public UserContract(UserHelper db) {
        this.db = db;
    }

    private long insertUser(String email, Integer subscriptionStatus, String lastUpdated) {
        ContentValues cv = new ContentValues();
        cv.put(UserEntry.COLUMN_NAME_EMAIL, email);
        cv.put(UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS, subscriptionStatus);
        cv.put(UserEntry.COLUMN_NAME_LAST_UPDATED, lastUpdated);
        return db.getWritableDatabase()
                .insert(
                        UserEntry.TABLE_NAME,
                        UserEntry.COLUMN_NAME_EMAIL,
                        cv
                );
    }

    public long insertUser(String email) {
        return insertUser(email, UserEntry.UNSUBSCRIBED, UserEntry.ZERO_DATE);
    }

    public long deleteUser(String[] emails) {
        return db.getWritableDatabase().delete(
                UserContract.UserEntry.TABLE_NAME,
                UserContract.UserEntry.COLUMN_NAME_EMAIL + "=?",
                emails
        );
    }

    public long updateOrNewUser(String email, Integer subscriptionStatus, Date date) {
        SQLiteDatabase writableDatabase = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(
                UserEntry.COLUMN_NAME_EMAIL,
                email
        );
        if (date != null)
            cv.put(
                    UserEntry.COLUMN_NAME_LAST_UPDATED,
                    DateHelper.formatAsRfc3992(date)
            );
        if (subscriptionStatus != null)
            cv.put(
                    UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS,
                    subscriptionStatus
            );
        long updateCount = writableDatabase.update(
                UserEntry.TABLE_NAME,
                cv,
                UserEntry.COLUMN_NAME_EMAIL + "=?",
                new String[]{email}
        );
        if (updateCount <= 0) {
            Log.d(TAG, "Inserting new row: " + email);
            return writableDatabase.insert(
                    UserEntry.TABLE_NAME,
                    UserEntry.COLUMN_NAME_EMAIL,
                    cv
            );
        } else {
            Log.d(TAG, "Updating row: " + email);
            return updateCount;
        }
    }

    public long updateSubscriptionStatus(String email, Integer subscriptionStatus) {
        ContentValues cv = new ContentValues();
        cv.put(UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS, subscriptionStatus);
        return db.getWritableDatabase().update(
                UserContract.UserEntry.TABLE_NAME,
                cv,
                " email=?",
                new String[]{email}
        );
    }

    public long updateLastUpdated(String email, Date lastUpdate) {
        ContentValues cv = new ContentValues();
        cv.put(
                UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED,
                DateHelper.formatAsRfc3992(lastUpdate)
        );
        return db.getWritableDatabase().update(
                UserContract.UserEntry.TABLE_NAME,
                cv,
                UserContract.UserEntry.COLUMN_NAME_EMAIL + "=?",
                new String[]{email});
    }

    public List<UserEntry> getAllUsers() {
        List<UserEntry> subscribedUsers = new ArrayList<>();
        Cursor cursor = db.getReadableDatabase().query(
                true,
                UserEntry.TABLE_NAME,
                new String[]{
                        UserEntry._ID,
                        UserEntry.COLUMN_NAME_EMAIL,
                        UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS,
                        UserEntry.COLUMN_NAME_LAST_UPDATED
                },
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                String email = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_EMAIL));
                Integer subscriptionStatus = cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_EMAIL));
                String lastUpdated = cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_NAME_LAST_UPDATED));
                subscribedUsers.add(new UserEntry(email, subscriptionStatus, lastUpdated));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return subscribedUsers;
    }

    public class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_SUBSCRIPTION_STATUS = "subscription_status";
        public static final String COLUMN_NAME_LAST_UPDATED = "last_updated";
        public static final int UNSUBSCRIBED = 0;
        public static final int PENDING = 1;
        public static final int SUBSCRIBED = 2;
        public static final String ZERO_DATE = "2000-01-01T00:00:00.000+07:00";

        public String email;
        public Integer subscriptionStatus;
        public String lastUpdated;

        public UserEntry(String email, Integer subscriptionStatus, String lastUpdated) {
            this.email = email;
            this.subscriptionStatus = subscriptionStatus;
            this.lastUpdated = lastUpdated;
        }
    }
}
