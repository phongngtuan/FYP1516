package com.ntu.phongnt.healthdroid.services.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.ntu.phongnt.healthdroid.data.data.Data;
import com.ntu.phongnt.healthdroid.data.data.model.DataRecord;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.db.data.DataHelper;
import com.ntu.phongnt.healthdroid.db.user.UserContract;
import com.ntu.phongnt.healthdroid.graph.util.DateHelper;
import com.ntu.phongnt.healthdroid.services.DataFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class GetDataRecordsFromEndpointTask extends AsyncTask<Void, Void, Void> {
    public static final String TAG = "Getting data";
    public static final String SUBSCRIBED_USERS_KEY = "SUBSCRIBED_USERS_KEY";
    private Context context = null;
    private DataHelper dataHelper = null;
    private UserContract userContract = null;
    private DataContract dataContract = null;

    public GetDataRecordsFromEndpointTask(Context context) {
        this.dataContract = new DataContract(dataHelper);
    }

    public GetDataRecordsFromEndpointTask(DataContract dataContract, UserContract userContract) {
        this.dataContract = dataContract;
        this.userContract = userContract;
    }

    @Override
    protected Void doInBackground(Void... params) {

        List<UserContract.UserEntry> subscribedUsers = userContract.getAllUsers();
        Log.d(TAG, "Subscribed email set size: " + subscribedUsers.size());

        //Add the current user
        //TODO: current not get data of local user, need to handle that too
//        SharedPreferences settings = context.getSharedPreferences("HealthDroid", 0);
//        String accountName = settings.getString(MainActivity.PREF_ACCOUNT_NAME, null);

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
        Log.d(TAG, "Getting data for email: " + user);
        DateTime after = DateTime.parseRfc3339(DateHelper.formatAsRfc3992(DateHelper.getDate(user.lastUpdated)));
        //Get the data records for this user, after last updated time
        List<DataRecord> dataRecordList = dataService.get().setUserId(user.email).setAfter(after).execute().getItems();
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
