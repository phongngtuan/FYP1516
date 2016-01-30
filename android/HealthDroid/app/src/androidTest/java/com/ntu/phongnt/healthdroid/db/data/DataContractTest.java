package com.ntu.phongnt.healthdroid.db.data;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DataContractTest {
    private String value = "123";
    private String date = "2016-01-10T02:08:50.889+07:00";
    private String user = "test@user.com";

    private DataHelper db = null;
    private DataContract dataContract = null;
    private int countBefore = -1;

    @Before
    public void setUp() throws Exception {
        RenamingDelegatingContext renamingDelegatingContext
                = new RenamingDelegatingContext(InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext(), "test_");

        db = new DataHelper(renamingDelegatingContext, null);
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
        Assert.assertTrue(countBefore >= 0);
        cursor.close();
    }

    @Test
    public void addData() throws Exception {
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
        Assert.assertEquals(cursor.getCount(), countBefore + 1);
    }

    @Test
    public void addData2() throws Exception {
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
        Assert.assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        Assert.assertEquals(
                cursor.getString(
                        cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_VALUE)),
                value);
        Assert.assertEquals(
                cursor.getString(
                        cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_USER)),
                user);
        Assert.assertEquals(
                cursor.getString(
                        cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME_DATE)),
                date);
        cursor.close();
    }
}
