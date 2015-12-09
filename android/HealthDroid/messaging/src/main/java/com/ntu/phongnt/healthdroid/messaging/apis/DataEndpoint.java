package com.ntu.phongnt.healthdroid.messaging.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.ntu.phongnt.healthdroid.messaging.models.DataRecord;

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
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @ApiMethod(name = "add")
    public void addData(@Named("value") float value) {
        Entity dataRecord = new Entity("Data", 1234);
        dataRecord.setProperty("value", value);
        datastore.put(dataRecord);
    }

    @ApiMethod(name = "get")
    public DataRecord getDataRecord(@Named("key") int lookup) {
        Key key = KeyFactory.createKey("Data", 1234);
        DataRecord dataRecord = new DataRecord(0);
        Entity entity = null;
        try {
            entity = datastore.get(key);
            dataRecord.setValue((float) entity.getProperty("value"));
        } catch (Exception e) {
        }
        return dataRecord;
    }
}
