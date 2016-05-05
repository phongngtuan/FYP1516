package com.ntu.phongnt.healthdroid.services;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ntu.phongnt.healthdroid.data.user.User;

import java.io.IOException;

public class UserFactory {
    private static User instance = null;

    public static User getInstance() {
        return instance;
    }

    public static User build(GoogleAccountCredential credential, String rootUrl) {
        User.Builder builder = new User.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential)
                .setApplicationName("HealthDroid")
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
