package com.ntu.phongnt.healthdroid.messaging.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.api.users.User;
import com.ntu.phongnt.healthdroid.messaging.entities.HealthDroidUser;
import com.ntu.phongnt.healthdroid.messaging.secured.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.ntu.phongnt.healthdroid.messaging.OfyService.ofy;

@Api(
        name = "user",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "data.healthdroid.phongnt.ntu.com",
                ownerName = "data.healthdroid.phongnt.ntu.com",
                packagePath = ""
        ),
        clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE},
        scopes = {"https://www.googleapis.com/auth/userinfo.email"}
)

public class HealthDroidUserEndpoint {
    @ApiMethod(name = "add")
    public HealthDroidUser addData(User user) {
        if (user != null) {
            HealthDroidUser healthDroidUser = new HealthDroidUser(user.getEmail());
            healthDroidUser.setEmail(user.getEmail());
            ofy().save().entity(healthDroidUser);
            return healthDroidUser;
        }
        return null;
    }

    @ApiMethod(name = "get")
    public List<HealthDroidUser> getUser(@Nullable @Named("userId") String userId) {
        List<HealthDroidUser> list = null;
        if (userId == null) {
            list = HealthDroidUser.getAllUsers();
        } else {
            HealthDroidUser user = HealthDroidUser.getUser(userId);
            list = new ArrayList<HealthDroidUser>();
            list.add(user);
        }
        return list;
    }
}
