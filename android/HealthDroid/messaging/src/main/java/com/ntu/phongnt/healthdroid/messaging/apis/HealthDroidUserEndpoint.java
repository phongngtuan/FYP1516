package com.ntu.phongnt.healthdroid.messaging.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.users.User;
import com.ntu.phongnt.healthdroid.messaging.models.HealthDroidUser;

import static com.ntu.phongnt.healthdroid.messaging.OfyService.ofy;

@Api(
        name = "user",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "data.healthdroid.phongnt.ntu.com",
                ownerName = "data.healthdroid.phongnt.ntu.com",
                packagePath = ""
        )
)

public class HealthDroidUserEndpoint {
    @ApiMethod(name = "add")
    public HealthDroidUser addData(User user) {
        HealthDroidUser healthDroidUser = new HealthDroidUser(user.getUserId());
        ofy().save().entity(healthDroidUser);
        return healthDroidUser;
    }
}
