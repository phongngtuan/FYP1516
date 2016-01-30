package com.ntu.phongnt.healthdroid.services.data;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.google.api.client.util.DateTime;
import com.ntu.phongnt.healthdroid.data.data.model.DataRecord;
import com.ntu.phongnt.healthdroid.data.data.model.HealthDroidUser;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.db.data.DataHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GetDataRecordsFromEndpointTaskTest extends AndroidTestCase {

    GetDataRecordsFromEndpointTask task;

    @Before
    public void setUp() throws Exception {
        RenamingDelegatingContext renamingDelegatingContext = new RenamingDelegatingContext(InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext(), "test_");

        DataHelper dataHelper = new DataHelper(renamingDelegatingContext, null);
        DataContract dataContract = new DataContract(dataHelper);
        task = new GetDataRecordsFromEndpointTask(dataContract);
    }

    @Test
    public void testAddDataToDatabase() throws Exception {
        List<DataRecord> dataRecords = new ArrayList<>();

        HealthDroidUser user = new HealthDroidUser();
        user.setEmail("user@test.com");

        Date firstDate = new Date();
        DataRecord firstDataRecord = new DataRecord();
        firstDataRecord.setUser(user);
        firstDataRecord.setCreatedAt(new DateTime(firstDate));
        firstDataRecord.setDate(new DateTime(firstDate));
        dataRecords.add(firstDataRecord);

        Date secondDate = new Date();
        DataRecord secondDataRecord = new DataRecord();
        secondDataRecord.setUser(user);
        secondDataRecord.setCreatedAt(new DateTime(secondDate));
        secondDataRecord.setDate(new DateTime(secondDate));
        dataRecords.add(secondDataRecord);

        Date latestDateFromData = task.addDataToDatabase(dataRecords);
        assertEquals(secondDate, latestDateFromData);
    }
}