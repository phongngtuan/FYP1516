package com.ntu.phongnt.healthdroid.services;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ntu.phongnt.healthdroid.data.data.Data;

import java.io.IOException;

public class DataFactory {
    private static Data instance = null;

    public static Data getInstance() {
        return instance;
    }

    public static Data build(GoogleAccountCredential credential, String rootUrl) {
        Data.Builder builder = new Data.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential)
                .setRootUrl(rootUrl)
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
