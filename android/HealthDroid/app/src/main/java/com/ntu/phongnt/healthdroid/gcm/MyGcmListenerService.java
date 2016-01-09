package com.ntu.phongnt.healthdroid.gcm;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.ntu.phongnt.healthdroid.data.data.Data;
import com.ntu.phongnt.healthdroid.data.data.model.DataRecord;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.db.data.DataHelper;
import com.ntu.phongnt.healthdroid.services.DataFactory;
import com.ntu.phongnt.healthdroid.util.DateHelper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        //TODO Implement actual work to be done when receiving some pushed notification from cloud
        Log.d(TAG, from);
        Log.d(TAG, data.toString());
        String collapse_key = data.getString("collapse_key");
        Log.d(TAG, collapse_key);

        new GetDataRecordsFromEndpoint().execute();
//        Log.d(TAG, data.getString("reading"));
//        Notification notification = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.places_ic_search)
//                .setContentTitle("My Tittle")
//                .setContentText(data.getString("message")).build();
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, notification);
    }

    private class GetDataRecordsFromEndpoint extends AsyncTask<Void, Void, Void> {
        //TODO: Complete the features
        //TODO: Quite a big task, need refactoring
        @Override
        protected Void doInBackground(Void... params) {

            Date localLastUpdatedDate = getLocalLastUpdatedDate();
            Data dataService = DataFactory.getInstance();

            try {
                List<DataRecord> dataRecordList = dataService.get().execute().getItems();
                SQLiteOpenHelper db = DataHelper.getInstance(getApplicationContext());

                Date latestDateFromData = addDataToDatabase(db, dataRecordList, localLastUpdatedDate);
                setLocalLastUpdatedDate(latestDateFromData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        Date addDataToDatabase(SQLiteOpenHelper db, List<DataRecord> dataRecords, Date after) {
            Date latestDateFromData = new Date();
            latestDateFromData.setTime(after.getTime());

            Log.i(TAG, "processing dataRecords size = " + dataRecords.size());
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
            Log.d(TAG, "last Updated = " + latestDateFromData);
            Log.d(TAG, "Updated count= " + count);
            return latestDateFromData;
        }

        Date getLocalLastUpdatedDate() {
            SharedPreferences dataPreferences =
                    getSharedPreferences("DATA_PREFERENCES", Context.MODE_PRIVATE);
            String lastUpdatedPreference = dataPreferences.getString(DataHelper.LAST_UPDATED, DataHelper.ZERO_TIME);
            return DateHelper.getDate(lastUpdatedPreference);
        }

        void setLocalLastUpdatedDate(Date latestDateFromData) {
            SharedPreferences dataPreferences =
                    getSharedPreferences("DATA_PREFERENCES", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = dataPreferences.edit();
            editor.putString(DataHelper.LAST_UPDATED, DateHelper.toString(latestDateFromData));
            editor.apply();
        }
    }
}
