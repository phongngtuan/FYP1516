package com.ntu.phongnt.healthdroid.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.services.GetDataRecordsFromEndpointTask;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

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
            String target = data.getString("target", "");
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.places_ic_search)
                    .setContentTitle("My Tittle")
                    .setContentText(
                            target + " accepted id " + id
                    )
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        } else
            new GetDataRecordsFromEndpointTask(getApplicationContext()).execute();
    }

}
