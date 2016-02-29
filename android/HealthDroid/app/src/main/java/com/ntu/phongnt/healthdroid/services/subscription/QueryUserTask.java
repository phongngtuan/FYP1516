package com.ntu.phongnt.healthdroid.services.subscription;

import android.os.AsyncTask;
import android.util.Log;

import com.ntu.phongnt.healthdroid.data.user.User;
import com.ntu.phongnt.healthdroid.data.user.model.HealthDroidUser;
import com.ntu.phongnt.healthdroid.services.UserFactory;

import java.io.IOException;
import java.util.List;

public class QueryUserTask extends AsyncTask<String, Void, List<HealthDroidUser>> {
    private static final String TAG = "QueryUserTask";

    Receiver receiver;

    public QueryUserTask(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected List<HealthDroidUser> doInBackground(String... params) {
        String queryString = params[0];
        User userService = UserFactory.getInstance();
        try {
            return userService.query(queryString).execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<HealthDroidUser> healthDroidUsers) {
        for (HealthDroidUser user : healthDroidUsers) {
            if (user != null && !user.getEmail().isEmpty()) {
                Log.d(TAG, "query result: " + user.getEmail());
            }
        }
        receiver.queryUserReceived(healthDroidUsers);
    }

    public interface Receiver {
        void queryUserReceived(List<HealthDroidUser> healthDroidUsers);
    }
}
