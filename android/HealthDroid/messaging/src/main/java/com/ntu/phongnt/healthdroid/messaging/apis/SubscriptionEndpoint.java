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
import com.ntu.phongnt.healthdroid.messaging.entities.Subscription;
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
    public Subscription subscribe(@Named("target") String target, User user) {
        Subscription subscription = new Subscription();

        HealthDroidUser targetUser = HealthDroidUser.getUser(target);
        HealthDroidUser subscriber = HealthDroidUser.getUser(user.getEmail());

        if (subscriber != null && targetUser != null) {
            Ref<HealthDroidUser> subscriberRef = Ref.create(subscriber);
            Ref<HealthDroidUser> targetUserRef = Ref.create(targetUser);

            subscription.setTarget(targetUserRef);
            subscription.setSubscriber(subscriberRef);
            ofy().save().entity(subscription).now();

            subscriber.subscribe(Ref.create(subscription));
            ofy().save().entity(subscriber).now();
        }

        return subscription;
    }

    @ApiMethod(name = "list")
    public List<Subscription> listSubscriptions() {
        return Subscription.getAllSubscriptions();
    }

    @ApiMethod(name = "get")
    public List<Subscription> getSubscribed(User user) {
        HealthDroidUser healthDroidUser = HealthDroidUser.getUser(user.getEmail());
        return ofy().load().type(Subscription.class).filter("subscriber", healthDroidUser).list();
    }

    @ApiMethod(name = "subscribers")
    public List<Subscription> subscribers(@Named("userId") String userId) {
        HealthDroidUser healthDroidUser = HealthDroidUser.getUser(userId);
        return ofy().load().type(Subscription.class).ancestor(healthDroidUser).list();
    }

    @ApiMethod(name = "unsubscribe")
    public List<Subscription> unsubscribe(@Named("userId") String userId, @Nullable @Named("target") String target) {
        HealthDroidUser healthDroidUser = HealthDroidUser.getUser(userId);
        Query<Subscription> query = ofy().load().type(Subscription.class).filter("subscriber", healthDroidUser);
        if (target != null) {
            HealthDroidUser targetUser = HealthDroidUser.getUser(target);
            query = query.ancestor(targetUser);
        }
        List<Subscription> subscriptions = query.list();
        ofy().delete().entities(subscriptions).now();
        return subscriptions;
    }
}
