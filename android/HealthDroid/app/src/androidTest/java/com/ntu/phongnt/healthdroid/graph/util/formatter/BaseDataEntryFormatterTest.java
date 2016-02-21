package com.ntu.phongnt.healthdroid.graph.util.formatter;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.data.DataContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BaseDataEntryFormatterTest {
    private String value = "123";
    private String date = "2016-01-10T02:08:50.889+07:00";
    private String user = "test@user.com";

    private HealthDroidDatabaseHelper db = null;
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

    @Test
    public void testGetDataByUser() throws Exception {
        dataContract.addData(value, date, user, 1);
        Cursor cursor = db.getReadableDatabase().query(
                DataContract.DataEntry.TABLE_NAME,
                new String[]{
                        DataContract.DataEntry.COLUMN_NAME_VALUE,
                        DataContract.DataEntry.COLUMN_NAME_DATE,
                        DataContract.DataEntry.COLUMN_NAME_USER,
                        DataContract.DataEntry.COLUMN_NAME_TYPE
                },
                DataContract.DataEntry.COLUMN_NAME_TYPE + " = 1",
                null,
                null,
                null,
                null
        );
        assertEquals(countBefore + 1, cursor.getCount());
        cursor.close();
    }
}