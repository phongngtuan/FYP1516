package com.ntu.phongnt.healthdroid.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.services.subscription.SubscriptionService;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";
    public static final String TARGET_USER = "target";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        //TODO Implement actual work to be done when receiving some pushed notification from cloud
        Log.d(TAG, from);
        Log.d(TAG, data.toString());
        String collapse_key = data.getString("collapse_key");
        Log.d(TAG, collapse_key);

        String opCode = data.getString("opCode", "");
        if (opCode.equalsIgnoreCase("subscriptionAccepted")) {
            String id = data.getString("subscriptionId", "");
            String subscriber = data.getString("subscriber", "");
            String targetUser = data.getString("target", "");
            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("My Tittle")
                    .setContentText(
                            targetUser + " accepted id " + id
                    )
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVibrate(new long[0])
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
            SubscriptionService.startConfirmSubscribed(this, targetUser);
        }
//        else
//            new GetDataRecordsFromEndpointTask(getApplicationContext()).execute();
    }

}
