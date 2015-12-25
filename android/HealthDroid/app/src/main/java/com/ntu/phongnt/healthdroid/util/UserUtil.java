package com.ntu.phongnt.healthdroid.util;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ntu.phongnt.healthdroid.data.user.User;

import java.io.IOException;

public class UserUtil {
    public static final String USER_SERVICE = "UserFragment";

    private static User userService = null;

    public static User getUserService(GoogleAccountCredential credential) {
        if (userService == null) {
            User.Builder builder = new User.Builder(
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
            userService = builder.build();
        }
        return userService;
    }

}
