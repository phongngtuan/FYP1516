package com.ntu.phongnt.healthdroid.services.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.ntu.phongnt.healthdroid.MainActivity;
import com.ntu.phongnt.healthdroid.data.data.Data;
import com.ntu.phongnt.healthdroid.data.data.model.DataRecord;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.db.data.DataHelper;
import com.ntu.phongnt.healthdroid.db.user.UserContract;
import com.ntu.phongnt.healthdroid.db.user.UserHelper;
import com.ntu.phongnt.healthdroid.graph.util.DateHelper;
import com.ntu.phongnt.healthdroid.services.DataFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class GetDataRecordsFromEndpointTask extends AsyncTask<Void, Void, Void> {
    public static final String TAG = "Getting data";
    public static final String SUBSCRIBED_USERS_KEY = "SUBSCRIBED_USERS_KEY";
    private Context context;

    public GetDataRecordsFromEndpointTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Set<String> subscribedUsers = new TreeSet<>();
        UserHelper userHelper = UserHelper.getInstance(context);
        SQLiteDatabase readableDatabase = userHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.query(
                true,
                UserContract.UserEntry.TABLE_NAME,
                new String[]{
                        UserContract.UserEntry._ID,
                        UserContract.UserEntry.COLUMN_NAME_EMAIL},
                UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS + "=?",
                new String[]{String.valueOf(UserContract.UserEntry.SUBSCRIBED)},
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                String email = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_EMAIL));
                subscribedUsers.add(email);
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        Log.d(TAG, "Subscribed email set size: " + subscribedUsers.size());

        //Add the current user
        SharedPreferences settings = context.getSharedPreferences("HealthDroid", 0);
        String accountName = settings.getString(MainActivity.PREF_ACCOUNT_NAME, null);
        subscribedUsers.add(accountName);

        //Get the new data only
        Date localLastUpdatedDate = getLocalLastUpdatedDate();
        Data dataService = DataFactory.getInstance();
        try {
            for (String user : subscribedUsers) {
                Log.d(TAG, "Getting data for email: " + user);
                DateTime after = DateTime.parseRfc3339(DateHelper.toRfc3339(localLastUpdatedDate));
                List<DataRecord> dataRecordList = dataService.get().setUserId(user).setAfter(after).execute().getItems();
                Log.d(TAG, "Found " + dataRecordList.size());
                SQLiteOpenHelper db = DataHelper.getInstance(context);

                Date latestDateFromData = addDataToDatabase(db, dataRecordList);
                setLocalLastUpdatedDate(latestDateFromData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    Date addDataToDatabase(SQLiteOpenHelper db, List<DataRecord> dataRecords) {
        Date latestDateFromData = null;

        Log.i(TAG, "processing dataRecords size = " + dataRecords.size());
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        int count = 0;
        for (DataRecord d : dataRecords) {
            count++;
            Date createDate = DateHelper.getDate(d.getCreatedAt().toStringRfc3339());
            if (latestDateFromData == null)
                latestDateFromData = createDate;
            else if (createDate.after(latestDateFromData))
                latestDateFromData = createDate;

            ContentValues values = new ContentValues();
            values.put(DataContract.DataEntry.COLUMN_NAME_VALUE, d.getValue());
            values.put(DataContract.DataEntry.COLUMN_NAME_DATE, d.getDate().toStringRfc3339());
            values.put(DataContract.DataEntry.COLUMN_NAME_USER, d.getUser().getEmail());
            sqLiteDatabase.insert(DataContract.DataEntry.TABLE_NAME,
                    DataContract.DataEntry.COLUMN_NAME_DATE,
                    values);
        }

        Log.d(TAG, "latest date from data: " + latestDateFromData);
        Log.d(TAG, "Updated count: " + count);
        return latestDateFromData;
    }

    Date getLocalLastUpdatedDate() {
        SharedPreferences dataPreferences =
                context.getSharedPreferences("DATA_PREFERENCES", Context.MODE_PRIVATE);
        String lastUpdatedPreference = dataPreferences.getString(DataHelper.LAST_UPDATED, DataHelper.ZERO_TIME);
        return DateHelper.getDate(lastUpdatedPreference);
    }

    void setLocalLastUpdatedDate(Date latestDateFromData) {
        SharedPreferences dataPreferences =
                context.getSharedPreferences("DATA_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = dataPreferences.edit();
        editor.putString(DataHelper.LAST_UPDATED, DateHelper.toRfc3339(latestDateFromData));
        editor.apply();
    }
}
