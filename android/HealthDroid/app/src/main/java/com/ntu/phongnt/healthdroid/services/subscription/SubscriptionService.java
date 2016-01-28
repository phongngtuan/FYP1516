package com.ntu.phongnt.healthdroid.services.subscription;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ntu.phongnt.healthdroid.MainActivity;
import com.ntu.phongnt.healthdroid.data.subscription.Subscription;
import com.ntu.phongnt.healthdroid.data.subscription.model.SubscriptionRecord;
import com.ntu.phongnt.healthdroid.data.user.User;
import com.ntu.phongnt.healthdroid.data.user.model.HealthDroidUser;
import com.ntu.phongnt.healthdroid.db.user.UserContract;
import com.ntu.phongnt.healthdroid.db.user.UserHelper;
import com.ntu.phongnt.healthdroid.gcm.QuickstartPreferences;
import com.ntu.phongnt.healthdroid.services.SubscriptionFactory;
import com.ntu.phongnt.healthdroid.services.UserFactory;
import com.ntu.phongnt.healthdroid.subscription.UserFragment;

import java.io.IOException;
import java.util.List;

public class SubscriptionService extends IntentService {
    private static UserHelper db = null;

    // IntentService can perform
    private static final String ACTION_UPDATE_USER_LIST =
            "com.ntu.phongnt.healthdroid.services.subscription.action.update_user_list";
    private static final String ACTION_UPDATE_SUBSCRIBED_USER =
            "com.ntu.phongnt.healthdroid.services.subscription.action.update_subscribed_user";
    private static final String ACTION_ADD_SUBSCRIBED_USER =
            "com.ntu.phongnt.healthdroid.services.subscription.action.add_subscribed_user";
    private static final String ACTION_REMOVE_SUBSCRIBED_USER =
            "com.ntu.phongnt.healthdroid.services.subscription.action.remove_subscribed_user";

    private static final String EXTRA_PARAM_USER =
            "com.ntu.phongnt.healthdroid.services.subscription.param.email";

    public SubscriptionService() {
        super("SubscriptionService");
    }

    public static void startUpdateUserList(Context context) {
        Intent intent = new Intent(context, SubscriptionService.class);
        intent.setAction(ACTION_UPDATE_USER_LIST);
        context.startService(intent);
    }

    public static void startUpdateSubscribedUser(Context context) {
        Intent intent = new Intent(context, SubscriptionService.class);
        intent.setAction(ACTION_ADD_SUBSCRIBED_USER);
        context.startService(intent);
    }

    public static void startAddSubscribedUser(Context context, String user) {
        Intent intent = new Intent(context, SubscriptionService.class);
        intent.setAction(ACTION_ADD_SUBSCRIBED_USER);
        intent.putExtra(EXTRA_PARAM_USER, user);
        context.startService(intent);
    }

    public static void startRemoveSubscribedUser(Context context, String user) {
        Intent intent = new Intent(context, SubscriptionService.class);
        intent.setAction(ACTION_REMOVE_SUBSCRIBED_USER);
        intent.putExtra(EXTRA_PARAM_USER, user);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (db == null)
            db = UserHelper.getInstance(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_USER_LIST.equals(action)) {
                updateUserList();
            } else if (ACTION_UPDATE_SUBSCRIBED_USER.equals(action)) {
                updateSubscribedUser();
            } else if (ACTION_ADD_SUBSCRIBED_USER.equals(action)) {
                final String user = intent.getStringExtra(EXTRA_PARAM_USER);
                addSubscribedUser(user);
            } else if (ACTION_REMOVE_SUBSCRIBED_USER.equals(action)) {
                final String user = intent.getStringExtra(EXTRA_PARAM_USER);
                removeSubscribedUser(user);
            }
        }
    }

    private void updateUserList() {
        new ListUserTask().execute();
    }

    private void updateSubscribedUser() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void addSubscribedUser(String user) {
        new SendSubscriptionTask().execute(user);
    }

    private void removeSubscribedUser(String targetUser) {
        SharedPreferences settings = getSharedPreferences(MainActivity.SHARED_PREFERENCE_NAME, 0);
        String thisUser = settings.getString(MainActivity.PREF_ACCOUNT_NAME, "");
        if (!thisUser.isEmpty()) {
            new CancelSubscriptionTask(thisUser).execute(targetUser);
        }
    }

    private void broadcastSubscriptionStatusChanged() {
        LocalBroadcastManager localBroadcastManager =
                LocalBroadcastManager.getInstance(SubscriptionService.this.getApplicationContext());
        localBroadcastManager.sendBroadcast(new Intent(QuickstartPreferences.SUBSCRIPTION_REQUEST_CHANGED));
    }

    private static class ListUserTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            User userService = UserFactory.getInstance();
            Subscription subscriptionService = SubscriptionFactory.getInstance();
            try {
                List<HealthDroidUser> healthDroidUsers = userService.get().execute().getItems();
                Log.d(UserFragment.TAG, "Received " + healthDroidUsers.size() + " users");
                SQLiteDatabase writableDatabase = db.getWritableDatabase();
                writableDatabase.delete(
                        UserContract.UserEntry.TABLE_NAME,
                        null,
                        null
                );

                for (HealthDroidUser healthDroidUser : healthDroidUsers) {
                    ContentValues cv = new ContentValues();
                    cv.put(
                            UserContract.UserEntry.COLUMN_NAME_EMAIL,
                            healthDroidUser.getEmail());
                    cv.put(
                            UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS,
                            UserContract.UserEntry.UNSUBSCRIBED);
                    writableDatabase.insert(
                            UserContract.UserEntry.TABLE_NAME,
                            UserContract.UserEntry.COLUMN_NAME_EMAIL,
                            cv);
                }

                List<SubscriptionRecord> subscriptionRecords =
                        subscriptionService.subscribed().execute().getItems();
                for (SubscriptionRecord record : subscriptionRecords) {
                    ContentValues cv = new ContentValues();
                    if (record.getIsAccepted())
                        cv.put(
                                UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS,
                                UserContract.UserEntry.SUBSCRIBED
                        );
                    else
                        cv.put(
                                UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS,
                                UserContract.UserEntry.PENDING
                        );

                    writableDatabase.update(
                            UserContract.UserEntry.TABLE_NAME,
                            cv,
                            " email=?",
                            new String[]{record.getTarget().getEmail()}
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SendSubscriptionTask extends AsyncTask<String, Void, SubscriptionRecord> {
        @Override
        protected SubscriptionRecord doInBackground(String... params) {
            Subscription subscriptionService = SubscriptionFactory.getInstance();
            SubscriptionRecord subscriptionRecord = null;
            String targetUser = params[0];
            try {
                subscriptionRecord = subscriptionService.subscribe(targetUser).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return subscriptionRecord;
        }

        @Override
        protected void onPostExecute(SubscriptionRecord subscriptionRecord) {
            super.onPostExecute(subscriptionRecord);
            if (subscriptionRecord != null) {
                SQLiteDatabase writableDatabase = db.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(
                        UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS,
                        UserContract.UserEntry.PENDING);
                writableDatabase.update(
                        UserContract.UserEntry.TABLE_NAME,
                        cv,
                        UserContract.UserEntry.COLUMN_NAME_EMAIL + "=?",
                        new String[]{subscriptionRecord.getTarget().getEmail()}
                );
            }
            broadcastSubscriptionStatusChanged();
        }
    }

    private class CancelSubscriptionTask extends AsyncTask<String, Void, SubscriptionRecord> {
        private String user;

        public CancelSubscriptionTask(String user) {
            this.user = user;
        }

        @Override
        protected SubscriptionRecord doInBackground(String... params) {
            Subscription subscriptionService = SubscriptionFactory.getInstance();
            String targetUser = params[0];
            try {
                List<SubscriptionRecord> results = subscriptionService.unsubscribe(user).setTarget(targetUser).execute().getItems();
                if (!results.isEmpty())
                    return results.get(0);
                else
                    return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(SubscriptionRecord subscriptionRecord) {
            super.onPostExecute(subscriptionRecord);
            if (subscriptionRecord != null) {
                SQLiteDatabase writableDatabase = db.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(
                        UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS,
                        UserContract.UserEntry.UNSUBSCRIBED);
                writableDatabase.update(
                        UserContract.UserEntry.TABLE_NAME,
                        cv,
                        UserContract.UserEntry.COLUMN_NAME_EMAIL + "=?",
                        new String[]{subscriptionRecord.getTarget().getEmail()}
                );
                broadcastSubscriptionStatusChanged();
            }
        }
    }
}
