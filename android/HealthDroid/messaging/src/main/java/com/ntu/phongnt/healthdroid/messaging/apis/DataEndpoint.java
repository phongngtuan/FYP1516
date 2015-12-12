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

import java.util.ArrayList;
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
        )
)

public class DataEndpoint {
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @ApiMethod(name = "add", path = "data")
    public DataRecord addData(@Named("value") int value, @Named("date") @Nullable Date date, User user) throws OAuthRequestException {
        DataRecord dataRecord = new DataRecord();
        if (user != null) {
            Key<HealthDroidUser> healthDroidUserKey = Key.create(HealthDroidUser.class, user.getUserId());
            dataRecord.setValue(value);
            dataRecord.setDate(date);
            dataRecord.setIdentifier(healthDroidUserKey.toString());
            dataRecord.setUser(healthDroidUserKey);
            ofy().save().entity(dataRecord).now();
            assert dataRecord.id != null;
        } else {
            throw new OAuthRequestException("Data Endpoints exception");
        }
        return dataRecord;
    }

    @ApiMethod(name = "get")
    public List<DataRecord> getDataRecord(@Nullable @Named("id") Long id) {
        if (id == null) {
            return ofy().load().type(DataRecord.class).list();
        }
        Key<DataRecord> key = Key.create(DataRecord.class, id);
        DataRecord dataRecord = ofy().load().key(key).safe();
        List<DataRecord> dataRecordList = new ArrayList<DataRecord>();
        dataRecordList.add(dataRecord);
        return dataRecordList;
    }

    @ApiMethod(name = "getByUser")
    public List<DataRecord> getDataRecordByUser(@Named("userid") String userid) {
        Key<HealthDroidUser> key = Key.create(HealthDroidUser.class, userid);
        List<DataRecord> dataRecords = ofy().load().type(DataRecord.class).ancestor(key).list();
        return dataRecords;
    }
}
