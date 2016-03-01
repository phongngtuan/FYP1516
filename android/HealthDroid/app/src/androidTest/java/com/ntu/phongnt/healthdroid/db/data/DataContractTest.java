package com.ntu.phongnt.healthdroid.db.data;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DataContractTest {
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
                null,
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
    public void testAddData() throws Exception {
        dataContract.addData(value, date, user);
        Cursor cursor = db.getReadableDatabase().query(
                DataContract.DataEntry.TABLE_NAME,
                new String[]{
                        DataContract.DataEntry.COLUMN_NAME_VALUE,
                        DataContract.DataEntry.COLUMN_NAME_DATE,
                        DataContract.DataEntry.COLUMN_NAME_USER
                },
                null,
                null,
                null,
                null,
                null
        );
        assertEquals(cursor.getCount(), countBefore + 1);
        cursor.close();
    }

    @Test
    public void testAddData2() throws Exception {
        dataContract.addData(value, date, user);
        Cursor cursor = db.getReadableDatabase().query(
                DataContract.DataEntry.TABLE_NAME,
                new String[]{
                        DataContract.DataEntry.COLUMN_NAME_VALUE,
                        DataContract.DataEntry.COLUMN_NAME_DATE,
                        DataContract.DataEntry.COLUMN_NAME_USER
                },
                DataContract.DataEntry.COLUMN_NAME_USER + "= '" + user + "' AND " +
                        DataContract.DataEntry.COLUMN_NAME_DATE + "= '" + date + "' AND " +
                        DataContract.DataEntry.COLUMN_NAME_VALUE + "=" + value,
                null,
                null,
                null,
                null
        );
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(
                cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_VALUE)),
                value);
        assertEquals(
                cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_USER)),
                user);
        assertEquals(
                cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_DATE)),
                date);
        cursor.close();
    }

    @Test
    public void testAddData3() throws Exception {
        dataContract.addData(value, date, user, 0);
        dataContract.addData(value, date, user, 1);
        Cursor cursor = db.getReadableDatabase().query(
                DataContract.DataEntry.TABLE_NAME,
                new String[]{
                        DataContract.DataEntry.COLUMN_NAME_VALUE,
                        DataContract.DataEntry.COLUMN_NAME_DATE,
                        DataContract.DataEntry.COLUMN_NAME_USER,
                        DataContract.DataEntry.COLUMN_NAME_TYPE
                },
                DataContract.DataEntry.COLUMN_NAME_USER + "= '" + user + "' AND " +
                        DataContract.DataEntry.COLUMN_NAME_DATE + "= '" + date + "' AND " +
                        DataContract.DataEntry.COLUMN_NAME_VALUE + "=" + value + " AND " +
                        DataContract.DataEntry.COLUMN_NAME_TYPE + "= 1",
                null,
                null,
                null,
                null
        );
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(
                cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_VALUE)),
                value);
        assertEquals(
                cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_USER)),
                user);
        assertEquals(
                cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_DATE)),
                date);
        cursor.close();
    }

    @Test
    public void testDeleteDataOfUser() throws Exception {
        dataContract.addData(value, date, user);
        String anotherUser = "another@user.com";
        dataContract.addData(value, date, anotherUser);
        Cursor cursor = db.getReadableDatabase().query(
                DataContract.DataEntry.TABLE_NAME,
                new String[]{
                        DataContract.DataEntry.COLUMN_NAME_VALUE,
                        DataContract.DataEntry.COLUMN_NAME_DATE,
                        DataContract.DataEntry.COLUMN_NAME_USER
                },
                null,
                null,
                null,
                null,
                null
        );
        dataContract.deleteDataOfUser(anotherUser);
        assertEquals(cursor.getCount(), countBefore + 1);
        cursor.close();
    }
}
