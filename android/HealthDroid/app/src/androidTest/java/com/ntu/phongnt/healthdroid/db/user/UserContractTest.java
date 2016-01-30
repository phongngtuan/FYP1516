package com.ntu.phongnt.healthdroid.db.user;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import com.ntu.phongnt.healthdroid.graph.util.DateHelper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserContractTest {

    private UserHelper db = null;
    private UserContract userContract = null;

    private String email = "user@test.com";
    private int subscriptionStatus = UserContract.UserEntry.UNSUBSCRIBED;
    private String lastUpdated = "2016-01-10T02:08:50.889+08:00";
    private String latestDateFromData = "2016-02-10T02:08:50.889+08:00";

    @Before
    public void setUp() throws Exception {
        RenamingDelegatingContext renamingDelegatingContext
                = new RenamingDelegatingContext(InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext(), "test_");

        db = new UserHelper(renamingDelegatingContext, null);
        userContract = new UserContract(db);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testInsertUser() throws Exception {
        userContract.insertUser(email);
        checkMatchedRow(email, UserContract.UserEntry.UNSUBSCRIBED, UserContract.UserEntry.ZERO_DATE);
    }

    @Test
    public void testUpdateExistingUser() throws Exception {
        userContract.insertUser(email);
        userContract.updateOrNewUser(email, subscriptionStatus, DateHelper.getDate(latestDateFromData));
        checkMatchedRow(email, subscriptionStatus, latestDateFromData);

    }

    @Test
    public void testUpdateNewUser() throws Exception {
        userContract.updateOrNewUser(email, subscriptionStatus, DateHelper.getDate(latestDateFromData));
        checkMatchedRow(email, subscriptionStatus, latestDateFromData);
    }

    private void checkDate(String date) {
        Cursor cursor = db.getReadableDatabase().query(
                UserContract.UserEntry.TABLE_NAME,
                new String[]{
                        UserContract.UserEntry.COLUMN_NAME_EMAIL,
                        UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS,
                        UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED
                },
                UserContract.UserEntry.COLUMN_NAME_EMAIL + " = '" + email + "' AND " +
                        UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS + " = " + subscriptionStatus,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        String lastUpdatedFromDb = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED));
        Assert.assertEquals(date, lastUpdatedFromDb);
        cursor.close();
    }

    private void checkMatchedRow(String email, int subscriptionStatus, String lastUpdated) {
        Cursor cursor = db.getReadableDatabase().query(
                UserContract.UserEntry.TABLE_NAME,
                new String[]{
                        UserContract.UserEntry.COLUMN_NAME_EMAIL,
                        UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS,
                        UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED
                },
                UserContract.UserEntry.COLUMN_NAME_EMAIL + " = '" + email + "' AND " +
                        UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS + " = " + subscriptionStatus + " AND " +
                        UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED + " = '" + lastUpdated + "'"
                ,
                null,
                null,
                null,
                null
        );
        Assert.assertEquals(1, cursor.getCount());
        cursor.close();
    }
}