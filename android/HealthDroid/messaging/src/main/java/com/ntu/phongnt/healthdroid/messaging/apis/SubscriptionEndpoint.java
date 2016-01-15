package com.ntu.phongnt.healthdroid.messaging.apis;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.cmd.Query;
import com.ntu.phongnt.healthdroid.messaging.entities.HealthDroidUser;
import com.ntu.phongnt.healthdroid.messaging.entities.RegistrationRecord;
import com.ntu.phongnt.healthdroid.messaging.entities.SubscriptionRecord;
import com.ntu.phongnt.healthdroid.messaging.secured.AppConstants;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static com.ntu.phongnt.healthdroid.messaging.OfyService.ofy;

@Api(
        name = "subscription",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "data.healthdroid.phongnt.ntu.com",
                ownerName = "data.healthdroid.phongnt.ntu.com",
                packagePath = ""
        ),
        clientIds = {AppConstants.WEB_CLIENT_ID, AppConstants.ANDROID_CLIENT_ID, AppConstants.IOS_CLIENT_ID, com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID},
        audiences = {AppConstants.ANDROID_AUDIENCE},
        scopes = {"https://www.googleapis.com/auth/userinfo.email"}
)

public class SubscriptionEndpoint {

    private static final String API_KEY = AppConstants.GCM_API_KEY;
    private static final Logger log = Logger.getLogger(SubscriptionRecord.class.getName());

    @ApiMethod(name = "subscribe")
    public SubscriptionRecord subscribe(@Named("target") String target, User user) {
        SubscriptionRecord subscriptionRecord = new SubscriptionRecord();

        HealthDroidUser targetUser = HealthDroidUser.getUser(target);
        HealthDroidUser subscriber = HealthDroidUser.getUser(user.getEmail());

        if (subscriber != null && targetUser != null) {
            List<SubscriptionRecord> records =
                    ofy().load().type(SubscriptionRecord.class).
                            ancestor(targetUser).
                            filter("subscriber", subscriber).
                            list();
            if (records != null && !records.isEmpty()) {
                subscriptionRecord = records.get(0);
            } else {
                subscriptionRecord.setTarget(targetUser);
                subscriptionRecord.setSubscriber(subscriber);
                ofy().save().entity(subscriptionRecord).now();
            }
        }

        return subscriptionRecord;
    }

    @ApiMethod(name = "accept")
    public SubscriptionRecord acceptSubscription(@Named("subscriptionId") Long subscriptionId, User user) throws IOException {
        HealthDroidUser healthDroidUser = HealthDroidUser.getUser(user.getEmail());
        SubscriptionRecord subscriptionRecord =
                ofy().load().type(SubscriptionRecord.class).parent(healthDroidUser).id(subscriptionId).now();
        if (subscriptionRecord != null) {
            subscriptionRecord.setIsAccepted(true);
            ofy().save().entity(subscriptionRecord);

            //Sending notifications
            Sender sender = new Sender(API_KEY);
            Message msg = new Message.Builder()
                    .addData("opCode", "subscriptionAccepted")
                    .addData("subscriptionId", subscriptionId.toString())
                    .addData("subscriber", subscriptionRecord.getSubscriber().getEmail())
                    .addData("target", subscriptionRecord.getTarget().getEmail())
                    .build();
            HealthDroidUser subscriber = subscriptionRecord.getSubscriber();
            List<RegistrationRecord> registrationRecords = ofy().load().type(RegistrationRecord.class).ancestor(subscriber).list();
            for (RegistrationRecord record : registrationRecords) {
                Result result = sender.send(msg, record.getRegId(), 5);
                if (result.getMessageId() != null) {
                    log.info("Message sent to " + record.getRegId());
                    String canonicalRedId = result.getCanonicalRegistrationId();
                    if (canonicalRedId != null) {
                        log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRedId);
                        record.setRegId(canonicalRedId);
                        ofy().save().entity(record).now();
                    }
                } else {
                    String error = result.getErrorCodeName();
                    if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                        log.warning("Registration Id " + record.getRegId() + "no longer registered with GCM, removing from datastore");
                        ofy().delete().entity(record).now();
                    } else {
                        log.warning("Error when sending message : " + error);
                    }
                }
            }
            //copy-paste ends
        }

        return subscriptionRecord;
    }

    @ApiMethod(name = "list")
    public List<SubscriptionRecord> listSubscriptions() {
        return ofy().load().type(SubscriptionRecord.class).list();
    }

    @ApiMethod(name = "subscribed")
    public Collection<SubscriptionRecord> getSubscribed(User user) {
        HealthDroidUser healthDroidUser = HealthDroidUser.getUser(user.getEmail());
        return ofy().load().type(SubscriptionRecord.class).filter("subscriber", healthDroidUser).list();
    }

    @ApiMethod(name = "subscribers")
    public Collection<SubscriptionRecord> subscribers(@Named("userId") String userId) {
        HealthDroidUser healthDroidUser = HealthDroidUser.getUser(userId);
        return ofy().load().type(SubscriptionRecord.class).ancestor(healthDroidUser).list();
    }

    @ApiMethod(name = "pending")
    public Collection<SubscriptionRecord> pending(@Named("userId") String userId) {
        HealthDroidUser healthDroidUser = HealthDroidUser.getUser(userId);
        return ofy().load().type(SubscriptionRecord.class).ancestor(healthDroidUser).filter("isAccepted", false).list();
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
