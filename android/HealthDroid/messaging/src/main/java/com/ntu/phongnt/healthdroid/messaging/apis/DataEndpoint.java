package com.ntu.phongnt.healthdroid.messaging.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.googlecode.objectify.Key;
import com.ntu.phongnt.healthdroid.messaging.models.DataRecord;

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

    @ApiMethod(name = "add")
    public DataRecord addData(@Named("value") int value) {
        DataRecord dataRecord = new DataRecord();
        dataRecord.setValue(value);
        ofy().save().entity(dataRecord).now();
        assert dataRecord.id != null;
        return dataRecord;
    }

    @ApiMethod(name = "get")
    public DataRecord getDataRecord(@Named("id") long id) {
        Key<DataRecord> key = Key.create(DataRecord.class, id);
        DataRecord dataRecordResult = ofy().load().key(key).safe();
        dataRecordResult.setValue(3223);
        return dataRecordResult;
    }
}
