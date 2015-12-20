package com.ntu.phongnt.healthdroid.messaging.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.ntu.phongnt.healthdroid.messaging.entities.DataRecord;
import com.ntu.phongnt.healthdroid.messaging.entities.HealthDroidUser;
import com.ntu.phongnt.healthdroid.messaging.secured.Constants;

import java.util.Date;
import java.util.List;

import static com.ntu.phongnt.healthdroid.messaging.OfyService.ofy;

@Api(
        name = "data",
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

public class DataEndpoint {
    private static final String API_KEY = Constants.GCM_API_KEY;

    @ApiMethod(name = "add", path = "data")
    public DataRecord addData(@Named("value") int value, @Named("date") Date date, User user) throws OAuthRequestException {
        DataRecord dataRecord = new DataRecord();
        if (user != null) {
            Key<HealthDroidUser> healthDroidUserKey = Key.create(HealthDroidUser.class, user.getEmail());
            dataRecord.setValue(value);
            dataRecord.setDate(date);
            dataRecord.setCreatedAt(new Date());
            dataRecord.setIdentifier(healthDroidUserKey.toString());
            dataRecord.setUser(healthDroidUserKey);
            ofy().save().entity(dataRecord).now();
            assert dataRecord.id != null;
        } else {
            throw new OAuthRequestException("Data Endpoints exception: User= " + user);
        }
        return dataRecord;
    }

    @ApiMethod(name = "get")
    public List<DataRecord> getDataRecord(@Nullable @Named("userId") String userId, @Nullable @Named("after") Date after) {
        List<DataRecord> dataRecordList = null;
        if (userId == null) {
            dataRecordList = ofy().load().type(DataRecord.class).list();
        } else {
            Key<HealthDroidUser> parent = Key.create(HealthDroidUser.class, userId);
            HealthDroidUser user = ofy().load().key(parent).safe();
            dataRecordList = ofy().load().type(DataRecord.class).ancestor(user).list();
        }
        return dataRecordList;
    }
}
