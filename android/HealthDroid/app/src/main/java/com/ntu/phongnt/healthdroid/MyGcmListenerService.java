package com.ntu.phongnt.healthdroid;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by phongnt on 10/19/15.
 */
public class MyGcmListenerService extends GcmListenerService{
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i(TAG, from);
    }
}
