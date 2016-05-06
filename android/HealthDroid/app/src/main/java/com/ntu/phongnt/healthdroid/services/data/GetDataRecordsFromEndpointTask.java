package com.ntu.phongnt.healthdroid.services.data;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.ntu.phongnt.healthdroid.AppPreferences;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.data.data.Data;
import com.ntu.phongnt.healthdroid.data.data.model.DataRecord;
import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.db.user.UserContract;
import com.ntu.phongnt.healthdroid.graph.util.DateHelper;
import com.ntu.phongnt.healthdroid.services.DataFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetDataRecordsFromEndpointTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = "GetDataTask";
    public static final String SUBSCRIBED_USERS_KEY = "SUBSCRIBED_USERS_KEY";
    String accountName;
    Context context;
    private HealthDroidDatabaseHelper healthDroidDatabaseHelper = null;
    private UserContract userContract = null;
    private DataContract dataContract = null;

    public GetDataRecordsFromEndpointTask(Context context) {
        this.context = context;
        SharedPreferences settings = context.getSharedPreferences("HealthDroid", 0);
        accountName = settings.getString(AppPreferences.PREF_ACCOUNT_NAME, null);

        healthDroidDatabaseHelper = HealthDroidDatabaseHelper.getInstance(context.getApplicationContext());
        this.dataContract = new DataContract(healthDroidDatabaseHelper);
        this.userContract = new UserContract(healthDroidDatabaseHelper);
    }

    public GetDataRecordsFromEndpointTask(DataContract dataContract, UserContract userContract) {
        this.dataContract = dataContract;
        this.userContract = userContract;
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<UserContract.UserEntry> subscribedUsers = userContract.getSubscribedUsers(accountName);
        Log.d(TAG, "Subscribed email set size: " + subscribedUsers.size());

        //Get the new data only
        Data dataService = DataFactory.getInstance();
        try {
            for (UserContract.UserEntry user : subscribedUsers) {
                getDataBelongsToUser(dataService, user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getDataBelongsToUser(Data dataService, UserContract.UserEntry user) throws IOException {
        Log.d(TAG, "Getting data for email: " + user.email);

        //Get the data records for this user, after last updated time
        DateTime after = DateTime.parseRfc3339(DateHelper.formatAsRfc3992(DateHelper.getDate(user.lastUpdated)));
        List<DataRecord> dataRecordList = dataService.get().setUserId(user.email).setAfter(after).execute().getItems();
        //List<DataRecord> dataRecordList = dataService.get().setUserId(user.email).execute().getItems();

        Log.d(TAG, "Found " + dataRecordList.size());

        Date latestDateFromData = addDataToDatabase(dataRecordList);
        setUserLastUpdatedTime(user.email, latestDateFromData);
    }

    void setUserLastUpdatedTime(String user, Date latestDateFromData) {
        if (latestDateFromData != null) {
            Log.d(TAG, "Updating lastUpdated for user: " + user + " - " + latestDateFromData);
            userContract.updateLastUpdated(user, latestDateFromData);
        }
    }

    Date addDataToDatabase(List<DataRecord> dataRecords) {
        Date latestDateFromData = null;

        boolean dataSafe = true;
        List<String> unsafeUsers = new ArrayList<>();
        Log.i(TAG, "processing dataRecords size = " + dataRecords.size());
        int count = 0;
        for (DataRecord d : dataRecords) {
            Date createDate = DateHelper.getDate(d.getCreatedAt().toStringRfc3339());
            if (createDate != null) {
                if (latestDateFromData == null || latestDateFromData.before(createDate))
                    latestDateFromData = createDate;
                dataContract.addData(
                        String.valueOf(d.getValue()),
                        d.getDate().toStringRfc3339(),
                        d.getUser().getEmail(),
                        d.getType()
                );
                count++;
                if (!dataIsSafe(d.getType(), String.valueOf(d.getValue()))) {
                    dataSafe = false;
                    unsafeUsers.add(d.getUser().getEmail());
                }
            }
        }
        if (!dataSafe) {
            sendNotificationUnsafeData(unsafeUsers);
        }

        Log.d(TAG, "latest date from data: " + latestDateFromData);
        Log.d(TAG, "Updated count: " + count);
        return latestDateFromData;
    }

    void sendNotificationUnsafeData(List<String> users) {
        for (String user : users) {
            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Health Droid")
                    .setContentText(
                            "Unsafe data: " + user
                    )
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(new long[0])
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }
    }

    boolean dataIsSafe(int type, String value) {
        switch (type) {
            case DataContract.SIMPLE_TYPE:
                return bloodGlucoseSafetyCheck(value);
            case DataContract.BLOOD_PRESSURE_TYPE:
                return bloodPressureSafetyCheck(value);
        }
        return true;
    }

    boolean bloodGlucoseSafetyCheck(String value) {
        float numerical = Float.parseFloat(value);
        return !(numerical < 3 || numerical > 9);
    }

    boolean bloodPressureSafetyCheck(String value) {
        String[] values = value.split("/");
        float systolic = Float.parseFloat(values[1]);
        float diastolic = Float.parseFloat(values[0]);

        return !(systolic > 140 || diastolic < 90);
    }

}
