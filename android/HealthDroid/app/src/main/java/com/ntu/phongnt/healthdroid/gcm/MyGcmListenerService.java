package com.ntu.phongnt.healthdroid.gcm;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService{
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i(TAG, from);
        Log.i(TAG, String.valueOf(data.getFloat("reading")));
    }
}
