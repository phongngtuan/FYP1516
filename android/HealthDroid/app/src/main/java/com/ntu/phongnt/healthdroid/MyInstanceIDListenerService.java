package com.ntu.phongnt.healthdroid;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by phongnt on 10/19/15.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    private static final String TAG = "MyInstanceIDLS";
    @Override
    public void onTokenRefresh() {
        Log.i(TAG, "onTokenRefresh()");
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
