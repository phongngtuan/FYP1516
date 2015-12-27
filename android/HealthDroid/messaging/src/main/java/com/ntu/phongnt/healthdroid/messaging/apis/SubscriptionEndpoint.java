package com.ntu.phongnt.healthdroid.messaging.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import com.ntu.phongnt.healthdroid.messaging.entities.HealthDroidUser;
import com.ntu.phongnt.healthdroid.messaging.entities.SubscriptionRecord;
import com.ntu.phongnt.healthdroid.messaging.secured.Constants;

import java.util.List;

import static com.ntu.phongnt.healthdroid.messaging.OfyService.ofy;

@Api(
        name = "subscription",
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

public class SubscriptionEndpoint {
    @ApiMethod(name = "subscribe")
    public SubscriptionRecord subscribe(@Named("target") String target, User user) {
        SubscriptionRecord subscriptionRecord = new SubscriptionRecord();

        HealthDroidUser targetUser = HealthDroidUser.getUser(target);
        HealthDroidUser subscriber = HealthDroidUser.getUser(user.getEmail());

        if (subscriber != null && targetUser != null) {
            Ref<HealthDroidUser> subscriberRef = Ref.create(subscriber);
            Ref<HealthDroidUser> targetUserRef = Ref.create(targetUser);

            subscriptionRecord.setTarget(targetUserRef);
            subscriptionRecord.setSubscriber(subscriberRef);
            ofy().save().entity(subscriptionRecord).now();

            subscriber.subscribe(Ref.create(subscriptionRecord));
            ofy().save().entity(subscriber).now();
        }

        return subscriptionRecord;
    }

    @ApiMethod(name = "list")
    public List<SubscriptionRecord> listSubscriptions() {
        return SubscriptionRecord.getAllSubscriptions();
    }

    @ApiMethod(name = "get")
    public List<SubscriptionRecord> getSubscribed(User user) {
        HealthDroidUser healthDroidUser = HealthDroidUser.getUser(user.getEmail());
        return ofy().load().type(SubscriptionRecord.class).filter("subscriber", healthDroidUser).list();
    }

    @ApiMethod(name = "subscribers")
    public List<SubscriptionRecord> subscribers(@Named("userId") String userId) {
        HealthDroidUser healthDroidUser = HealthDroidUser.getUser(userId);
        return ofy().load().type(SubscriptionRecord.class).ancestor(healthDroidUser).list();
    }

    @ApiMethod(name = "unsubscribe")
    public List<SubscriptionRecord> unsubscribe(@Named("userId") String userId, @Nullable @Named("target") String target) {
        HealthDroidUser healthDroidUser = HealthDroidUser.getUser(userId);
        Query<SubscriptionRecord> query = ofy().load().type(SubscriptionRecord.class).filter("subscriber", healthDroidUser);
        if (target != null) {
            HealthDroidUser targetUser = HealthDroidUser.getUser(target);
            query = query.ancestor(targetUser);
        }
        List<SubscriptionRecord> subscriptionRecords = query.list();
        ofy().delete().entities(subscriptionRecords).now();
        return subscriptionRecords;
    }
}
