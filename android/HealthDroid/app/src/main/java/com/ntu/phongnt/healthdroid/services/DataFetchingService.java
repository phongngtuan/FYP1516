package com.ntu.phongnt.healthdroid.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DataFetchingService extends IntentService {
    // IntentService can perform
    private static final String ACTION_FETCH_DATA = "com.ntu.phongnt.healthdroid.action.FOO";

    public DataFetchingService() {
        super("DataFetchingService");
    }

    public static void startFetchingData(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DataFetchingService.class);
        intent.setAction(ACTION_FETCH_DATA);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_DATA.equals(action)) {
                fetchData();
            }
        }
    }

    private void fetchData() {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
