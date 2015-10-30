package com.ntu.phongnt.healthdroid.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.ntu.phongnt.healthdroid.R;

public class MyGcmListenerService extends GcmListenerService{
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, from);
        Log.d(TAG, data.getString("reading"));
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.powered_by_google_dark)
                .setContentTitle("My Tittle")
                .setContentText("My Content").build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
