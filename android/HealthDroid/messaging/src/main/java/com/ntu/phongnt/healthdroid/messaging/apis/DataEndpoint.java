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
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import com.ntu.phongnt.healthdroid.messaging.entities.DataRecord;
import com.ntu.phongnt.healthdroid.messaging.entities.HealthDroidUser;
import com.ntu.phongnt.healthdroid.messaging.entities.RegistrationRecord;
import com.ntu.phongnt.healthdroid.messaging.entities.SubscriptionRecord;
import com.ntu.phongnt.healthdroid.messaging.secured.AppConstants;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.ntu.phongnt.healthdroid.messaging.OfyService.ofy;

@Api(
        name = "data",
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

public class DataEndpoint {
    private static final String API_KEY = AppConstants.GCM_API_KEY;
    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());

    @ApiMethod(name = "add", path = "data")
    public DataRecord addData(@Named("value") int value, @Named("date") Date date, User user) throws OAuthRequestException, IOException {
        DataRecord dataRecord = new DataRecord();
        if (user != null) {
            Key<HealthDroidUser> healthDroidUserKey = Key.create(HealthDroidUser.class, user.getEmail());
            HealthDroidUser healthDroidUser = ofy().load().key(healthDroidUserKey).now();

            dataRecord.setValue(value);
            dataRecord.setDate(date);
            dataRecord.setCreatedAt(new Date());
            dataRecord.setIdentifier(healthDroidUserKey.toString());
            dataRecord.setUser(healthDroidUser);
            ofy().save().entity(dataRecord).now();
            assert dataRecord.id != null;

            //TODO: Need to refactor this, copy paste for now
            //Sending notifications
            Sender sender = new Sender(API_KEY);
            Message msg = new Message.Builder().collapseKey("data_updated").build();
            System.out.println("DEBUG: Publisher " + healthDroidUser.getEmail());
            List<SubscriptionRecord> subscriptionRecords = ofy().load().type(SubscriptionRecord.class).ancestor(healthDroidUser).list();
            System.out.println("DEBUG: Subscription records count: " + subscriptionRecords.size());
            for (SubscriptionRecord subscriptionRecord : subscriptionRecords) {
                HealthDroidUser subscriber = subscriptionRecord.getSubscriber();
                List<RegistrationRecord> registrationRecords = ofy().load().type(RegistrationRecord.class).ancestor(subscriber).list();
                System.out.println("DEBUG: Registrations count: " + registrationRecords.size());
                for (RegistrationRecord record : registrationRecords) {
                    System.out.println("DBUG: Sending");
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
            }
            //copy-paste ends
        } else {
            throw new OAuthRequestException("Data Endpoints exception: User= " + user);
        }
        return dataRecord;
    }

    @ApiMethod(name = "get")
    public List<DataRecord> getDataRecord(@Nullable @Named("userId") String userId, @Nullable @Named("after") Date after) {
        List<DataRecord> dataRecordList = null;
        Query<DataRecord> query = ofy().load().type(DataRecord.class);
        if (userId != null) {
            Key<HealthDroidUser> parent = Key.create(HealthDroidUser.class, userId);
            Ref<HealthDroidUser> user = Ref.create(parent);
            query = query.ancestor(user);
        }
        if (after != null) {
            query = query.filter("createdAt >", after);
        }
        dataRecordList = query.list();
        return dataRecordList;
    }
}
