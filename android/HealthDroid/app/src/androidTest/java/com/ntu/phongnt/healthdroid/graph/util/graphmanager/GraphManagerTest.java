package com.ntu.phongnt.healthdroid.graph.util.graphmanager;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;

import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.data.DataContract;

import org.junit.Before;

import static org.junit.Assert.assertTrue;

public class GraphManagerTest {
    private String value = "123";
    private String date = "2016-01-10T02:08:50.889+07:00";
    private String user = "test@user.com";

    private HealthDroidDatabaseHelper db = null;
    //TODO: revisit test here
    private DataContract dataContract = null;
    private int countBefore = -1;

    @Before
    public void setUp() throws Exception {
        RenamingDelegatingContext renamingDelegatingContext
                = new RenamingDelegatingContext(InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext(), "test_");

        db = new HealthDroidDatabaseHelper(renamingDelegatingContext, null);
        dataContract = new DataContract(db);

        Cursor cursor = db.getReadableDatabase().query(
                DataContract.DataEntry.TABLE_NAME,
                new String[]{
                        DataContract.DataEntry.COLUMN_NAME_VALUE,
                        DataContract.DataEntry.COLUMN_NAME_DATE,
                        DataContract.DataEntry.COLUMN_NAME_USER
                },
                DataContract.DataEntry.COLUMN_NAME_TYPE + " = 1",
                null,
                null,
                null,
                null
        );
        countBefore = cursor.getCount();
        assertTrue(countBefore >= 0);
        cursor.close();
    }

//    @Test
//    public void testGetDataByUser() throws Exception {
//        Cursor cursor = db.getReadableDatabase().query(
//                DataContract.DataEntry.TABLE_NAME,
//                new String[]{
//                        DataContract.DataEntry.COLUMN_NAME_VALUE,
//                        DataContract.DataEntry.COLUMN_NAME_DATE,
//                        DataContract.DataEntry.COLUMN_NAME_USER,
//                        DataContract.DataEntry.COLUMN_NAME_TYPE
//                },
//                DataContract.DataEntry.COLUMN_NAME_TYPE + " = 0",
//                null,
//                null,
//                null,
//                null
//        );
//        GraphManager adapter
//                = new GraphManager(cursor);
//        DataPool dataPool = new SimpleDataPool();
//        KeyCreator keyCreator = new ByMonthKeyCreator();
//        adapter.setKeyCreator(keyCreator);
//        adapter.setDataPool(dataPool);
//        adapter.getDataByUser();
//        adapter.accumulateDataByUser();
//        DateRange range = dataPool.findRange();
//        cursor.close();
//    }
}