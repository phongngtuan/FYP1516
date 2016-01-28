package com.ntu.phongnt.healthdroid.services.subscription;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class SubscriptionService extends IntentService {
    // IntentService can perform
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
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_SUBSCRIBED_USER.equals(action)) {
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

    private void updateSubscribedUser() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void addSubscribedUser(String user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void removeSubscribedUser(String user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
