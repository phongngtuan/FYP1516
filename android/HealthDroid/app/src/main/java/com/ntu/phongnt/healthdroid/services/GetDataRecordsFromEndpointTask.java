package com.ntu.phongnt.healthdroid.services;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.ntu.phongnt.healthdroid.MainActivity;
import com.ntu.phongnt.healthdroid.data.data.Data;
import com.ntu.phongnt.healthdroid.data.data.model.DataRecord;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.db.data.DataHelper;
import com.ntu.phongnt.healthdroid.graph.util.DateHelper;

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
        SharedPreferences dataPreferences =
                context.getSharedPreferences("DATA_PREFERENCES", Context.MODE_PRIVATE);
        Set<String> subscribedUsers = dataPreferences.getStringSet(SUBSCRIBED_USERS_KEY, new TreeSet<String>());

        SharedPreferences settings = context.getSharedPreferences("HealthDroid", 0);
        String accountName = settings.getString(MainActivity.PREF_ACCOUNT_NAME, null);

        subscribedUsers.add(accountName);

        Date localLastUpdatedDate = getLocalLastUpdatedDate();
        Data dataService = DataFactory.getInstance();

        Log.d(TAG, "Subscribed user set size: " + subscribedUsers.size());
        try {
            for (String user : subscribedUsers) {
                Log.d(TAG, "Getting data for user: " + user);
                List<DataRecord> dataRecordList = dataService.get().setUserId(user).execute().getItems();
                Log.d(TAG, "Found " + dataRecordList.size());
                SQLiteOpenHelper db = DataHelper.getInstance(context);

                Date latestDateFromData = addDataToDatabase(db, dataRecordList, localLastUpdatedDate);
                setLocalLastUpdatedDate(latestDateFromData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    Date addDataToDatabase(SQLiteOpenHelper db, List<DataRecord> dataRecords, Date after) {
        Date latestDateFromData = new Date();
        latestDateFromData.setTime(after.getTime());

        Log.i(TAG, "processing dataRecords size = " + dataRecords.size());
        Log.i(TAG, "local latest date: " + after);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        int count = 0;
        for (DataRecord d : dataRecords) {
            Date dateFromData = DateHelper.getDate(d.getDate().toStringRfc3339());
            if (dateFromData.after(after)) {
                count++;
                if (dateFromData.after(latestDateFromData))
                    latestDateFromData = dateFromData;
                ContentValues values = new ContentValues();
                values.put(DataContract.DataEntry.COLUMN_NAME_VALUE, d.getValue());
                values.put(DataContract.DataEntry.COLUMN_NAME_DATE, d.getDate().toStringRfc3339());
                values.put(DataContract.DataEntry.COLUMN_NAME_USER, d.getUser().getEmail());
                sqLiteDatabase.insert(DataContract.DataEntry.TABLE_NAME,
                        DataContract.DataEntry.COLUMN_NAME_DATE,
                        values);
            }
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
        editor.putString(DataHelper.LAST_UPDATED, DateHelper.toString(latestDateFromData));
        editor.apply();
    }
}
