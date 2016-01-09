package com.ntu.phongnt.healthdroid.services;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ntu.phongnt.healthdroid.data.subscription.Subscription;

import java.io.IOException;

public class SubscriptionFactory {
    private static Subscription instance = null;

    public static Subscription getInstance() {
        return instance;
    }

    public static Subscription build(GoogleAccountCredential credential) {
        Subscription.Builder builder = new Subscription.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential)
                .setRootUrl("http://192.168.1.28:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        instance = builder.build();
        return instance;
    }
}
