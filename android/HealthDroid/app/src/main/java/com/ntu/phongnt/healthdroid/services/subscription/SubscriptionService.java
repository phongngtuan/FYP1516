package com.ntu.phongnt.healthdroid.services.subscription;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ntu.phongnt.healthdroid.MainActivity;
import com.ntu.phongnt.healthdroid.data.subscription.Subscription;
import com.ntu.phongnt.healthdroid.data.subscription.model.SubscriptionRecord;
import com.ntu.phongnt.healthdroid.data.user.User;
import com.ntu.phongnt.healthdroid.data.user.model.HealthDroidUser;
import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.user.UserContract;
import com.ntu.phongnt.healthdroid.gcm.QuickstartPreferences;
import com.ntu.phongnt.healthdroid.services.SubscriptionFactory;
import com.ntu.phongnt.healthdroid.services.UserFactory;
import com.ntu.phongnt.healthdroid.subscription.UserFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionService extends IntentService {
    private static HealthDroidDatabaseHelper db = null;

    // IntentService can perform
    private static final String ACTION_UPDATE_USER_LIST =
            "com.ntu.phongnt.healthdroid.services.subscription.action.update_user_list";
    private static final String ACTION_SUBSCRIBE_USER =
            "com.ntu.phongnt.healthdroid.services.subscription.action.add_subscribed_user";
    private static final String ACTION_UNSUBSCRIBED_USER =
            "com.ntu.phongnt.healthdroid.services.subscription.action.remove_subscribed_user";
    private static final String ACTION_CONFIRM_SUBSCRIBED =
            "com.ntu.phongnt.healthdroid.services.subscription.action.action_confirm_subscribed";

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

    public static void startSubscribeUser(Context context, String user) {
        Intent intent = new Intent(context, SubscriptionService.class);
        intent.setAction(ACTION_SUBSCRIBE_USER);
        intent.putExtra(EXTRA_PARAM_USER, user);
        context.startService(intent);
    }

    public static void startUnsubscribeUser(Context context, String user) {
        Intent intent = new Intent(context, SubscriptionService.class);
        intent.setAction(ACTION_UNSUBSCRIBED_USER);
        intent.putExtra(EXTRA_PARAM_USER, user);
        context.startService(intent);
    }

    public static void startConfirmSubscribed(Context context, String user) {
        Intent intent = new Intent(context, SubscriptionService.class);
        intent.setAction(ACTION_CONFIRM_SUBSCRIBED);
        intent.putExtra(EXTRA_PARAM_USER, user);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (db == null)
            db = HealthDroidDatabaseHelper.getInstance(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_USER_LIST.equals(action)) {
                updateUserList();
            } else if (ACTION_SUBSCRIBE_USER.equals(action)) {
                final String user = intent.getStringExtra(EXTRA_PARAM_USER);
                subscribedUser(user);
            } else if (ACTION_UNSUBSCRIBED_USER.equals(action)) {
                final String user = intent.getStringExtra(EXTRA_PARAM_USER);
                unsubscribeUser(user);
            } else if (ACTION_CONFIRM_SUBSCRIBED.equals(action)) {
                final String user = intent.getStringExtra(EXTRA_PARAM_USER);
                confirmSubscribed(user);
            }
        }
    }

    private void updateUserList() {
        new ListUserTask().execute();
    }

    private void subscribedUser(String user) {
        new SendSubscriptionTask().execute(user);
    }

    private void unsubscribeUser(String targetUser) {
        SharedPreferences settings = getSharedPreferences(MainActivity.SHARED_PREFERENCE_NAME, 0);
        String thisUser = settings.getString(MainActivity.PREF_ACCOUNT_NAME, "");
        if (!thisUser.isEmpty()) {
            new CancelSubscriptionTask(thisUser).execute(targetUser);
        }
    }

    private void confirmSubscribed(String user) {
        markSubscribed(user);
        broadcastSubscriptionStatusChanged();
    }

    private void broadcastSubscriptionStatusChanged() {
        LocalBroadcastManager localBroadcastManager =
                LocalBroadcastManager.getInstance(SubscriptionService.this.getApplicationContext());
        localBroadcastManager.sendBroadcast(new Intent(QuickstartPreferences.SUBSCRIPTION_REQUEST_CHANGED));
    }

    private class ListUserTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "ListUserTask";
        private UserContract userContract = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            userContract = new UserContract(db);
        }

        @Override
        protected Void doInBackground(Void... params) {
            User userService = UserFactory.getInstance();
            Subscription subscriptionService = SubscriptionFactory.getInstance();
            try {
                List<HealthDroidUser> healthDroidUsers = userService.get().execute().getItems();
                Log.d(UserFragment.TAG, "Received " + healthDroidUsers.size() + " users");

                List<String> userEmails = new ArrayList<>();
                for (HealthDroidUser user : healthDroidUsers) {
                    userEmails.add(user.getEmail());
                }
                Cursor cursor = db.getReadableDatabase().query(
                        UserContract.UserEntry.TABLE_NAME,
                        new String[]{UserContract.UserEntry.COLUMN_NAME_EMAIL},
                        null,
                        null,
                        null,
                        null,
                        null
                );

                List<String> obsoleteUsers = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        String email = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_EMAIL));
                        if (!userEmails.contains(email))
                            obsoleteUsers.add(email);
                    } while (cursor.moveToNext());
                }
                cursor.close();

                //Clear obsolete user
                userContract.deleteUser(obsoleteUsers.toArray(new String[obsoleteUsers.size()]));

                for (HealthDroidUser healthDroidUser : healthDroidUsers) {
                    userContract.updateOrNewUser(healthDroidUser.getEmail());
                }

                List<SubscriptionRecord> subscriptionRecords =
                        subscriptionService.subscribed().execute().getItems();
                for (SubscriptionRecord record : subscriptionRecords) {
                    if (record.getIsAccepted())
                        userContract.updateSubscriptionStatus(record.getTarget().getEmail(),
                                UserContract.UserEntry.SUBSCRIBED);
                    else
                        userContract.updateSubscriptionStatus(record.getTarget().getEmail(),
                                UserContract.UserEntry.PENDING);
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
                markPending(subscriptionRecord.getTarget().getEmail());
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
                markUnsubscribed(subscriptionRecord.getTarget().getEmail());
                broadcastSubscriptionStatusChanged();
            }
        }
    }

    private void changeSubscriptionStatus(String user, int status) {
        UserContract userContract = new UserContract(db);
        userContract.updateSubscriptionStatus(user, status);
    }

    private void markPending(String user) {
        changeSubscriptionStatus(user, UserContract.UserEntry.PENDING);
    }

    private void markSubscribed(String user) {
        changeSubscriptionStatus(user, UserContract.UserEntry.SUBSCRIBED);
    }

    private void markUnsubscribed(String user) {
        changeSubscriptionStatus(user, UserContract.UserEntry.UNSUBSCRIBED);
    }

}
