package com.ntu.phongnt.healthdroid.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.ntu.phongnt.healthdroid.R;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        //TODO Implement actual work to be done when receiving some pushed notification from cloud
        Log.d(TAG, from);
        Log.d(TAG, data.toString());
        String collapse_key = data.getString("collapse_key");
        Log.d(TAG, collapse_key);

//        Log.d(TAG, data.getString("reading"));
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.places_ic_search)
                .setContentTitle("My Tittle")
                .setContentText(data.getString("message")).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
