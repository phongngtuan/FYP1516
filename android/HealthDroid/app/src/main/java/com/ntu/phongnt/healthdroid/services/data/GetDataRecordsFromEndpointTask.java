package com.ntu.phongnt.healthdroid.services.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ntu.phongnt.healthdroid.data.data.model.DataRecord;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.db.data.DataHelper;
import com.ntu.phongnt.healthdroid.graph.util.DateHelper;

import java.util.Date;
import java.util.List;

public class GetDataRecordsFromEndpointTask extends AsyncTask<Void, Void, Void> {
    public static final String TAG = "Getting data";
    public static final String SUBSCRIBED_USERS_KEY = "SUBSCRIBED_USERS_KEY";
    private DataHelper dataHelper = null;
    private DataContract dataContract = null;

    public GetDataRecordsFromEndpointTask(Context context) {
        this.dataContract = new DataContract(dataHelper);
    }

    public GetDataRecordsFromEndpointTask(DataContract dataContract) {
        this.dataContract = dataContract;
    }

    @Override
    protected Void doInBackground(Void... params) {
        /*
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
                DateTime after = DateTime.parseRfc3339(DateHelper.formatAsRfc3992(localLastUpdatedDate));
                //Get the data records for this user, after last updated time
                List<DataRecord> dataRecordList = dataService.get().setUserId(user).setAfter(after).execute().getItems();
                Log.d(TAG, "Found " + dataRecordList.size());
                SQLiteOpenHelper db = DataHelper.getInstance(context);

                Date latestDateFromData = addDataToDatabase(db, dataRecordList);
                setUserLastUpdatedTime(user, latestDateFromData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        return null;
    }

//    private void setUserLastUpdatedTime(String user, Date latestDateFromData) {
//        if (latestDateFromData != null) {
//            Log.d(TAG, "Updating lastUpdated for user: " + user + " - " + latestDateFromData);
//            SQLiteDatabase writableDatabase = UserHelper.getInstance(context).getWritableDatabase();
//            ContentValues cv = new ContentValues();
//            cv.put(
//                    UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED,
//                    DateHelper.formatAsRfc3992(latestDateFromData)
//            );
//            writableDatabase.update(
//                    UserContract.UserEntry.TABLE_NAME,
//                    cv,
//                    UserContract.UserEntry.COLUMN_NAME_EMAIL + "=?",
//                    new String[]{user}
//            );
//        } else
//            Log.d(TAG, "No data. Latest date not changed");
//    }

    Date addDataToDatabase(List<DataRecord> dataRecords) {
        Date latestDateFromData = null;

        Log.i(TAG, "processing dataRecords size = " + dataRecords.size());
        int count = 0;
        for (DataRecord d : dataRecords) {
            Date createDate = DateHelper.getDate(d.getCreatedAt().toStringRfc3339());
            if (createDate != null) {
                if (latestDateFromData == null || latestDateFromData.before(createDate))
                    latestDateFromData = createDate;
                dataContract.addData(d.getValue(), d.getDate().toStringRfc3339(), d.getUser().getEmail());
                count++;
            }
        }

        Log.d(TAG, "latest date from data: " + latestDateFromData);
        Log.d(TAG, "Updated count: " + count);
        return latestDateFromData;
    }

}
