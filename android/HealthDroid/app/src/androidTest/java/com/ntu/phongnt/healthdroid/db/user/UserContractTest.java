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

import java.util.Date;
import java.util.List;

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
        Assert.assertEquals(1,
                countUsers(email, UserContract.UserEntry.UNSUBSCRIBED, UserContract.UserEntry.ZERO_DATE));
    }

    @Test
    public void testUpdateExistingUser() throws Exception {
        userContract.insertUser(email);
        userContract.updateOrNewUser(email, subscriptionStatus, DateHelper.getDate(latestDateFromData));
        Assert.assertEquals(1,
                countUsers(email, subscriptionStatus, latestDateFromData));
    }

    @Test
    public void testUpdateNewUser() throws Exception {
        userContract.updateOrNewUser(email, subscriptionStatus, DateHelper.getDate(latestDateFromData));
        Assert.assertEquals(1,
                countUsers(email, subscriptionStatus, latestDateFromData));
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

    @Test
    public void testUpdateSubscriptionStatus() throws Exception {
        userContract.insertUser(email);
        userContract.updateSubscriptionStatus(email, UserContract.UserEntry.PENDING);
        Assert.assertTrue(countUsers(email, UserContract.UserEntry.PENDING, UserContract.UserEntry.ZERO_DATE) >= 1);
        userContract.updateSubscriptionStatus(email, UserContract.UserEntry.SUBSCRIBED);
        Assert.assertTrue(countUsers(email, UserContract.UserEntry.SUBSCRIBED, UserContract.UserEntry.ZERO_DATE) >= 1);
    }

    @Test
    public void testDeleteUser() throws Exception {
        userContract.insertUser(email);
        String anotherEmail = "another@email.com";
        userContract.insertUser(anotherEmail);
        userContract.deleteUser(new String[]{email});
        Assert.assertEquals(0, countUsers(email));
        Assert.assertEquals(1, countUsers(anotherEmail));
    }

    @Test
    public void testUpdateLastUpdated() throws Exception {
        userContract.insertUser(email);
        Date lastUpdate = new Date();
        userContract.updateLastUpdated(email, lastUpdate);
        Assert.assertEquals(1, countUsers(email, subscriptionStatus, DateHelper.formatAsRfc3992(lastUpdate)));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        userContract.insertUser(email);
        String anotherEmail = "another@email.com";
        userContract.insertUser(anotherEmail);
        List<UserContract.UserEntry> allUsers = userContract.getAllUsers();
        Assert.assertEquals(2, allUsers.size());
    }

    private int countUsersWithSelection(String selection) {
        Cursor cursor = db.getReadableDatabase().query(
                UserContract.UserEntry.TABLE_NAME,
                new String[]{
                        UserContract.UserEntry.COLUMN_NAME_EMAIL,
                        UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS,
                        UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED
                },
                selection,
                null,
                null,
                null,
                null
        );
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private int countUsers(String email) {
        String selection =
                UserContract.UserEntry.COLUMN_NAME_EMAIL + " = '" + email + "'";
        return countUsersWithSelection(selection);
    }

    private int countUsers(String email, int subscriptionStatus, String lastUpdated) {
        String selection =
                UserContract.UserEntry.COLUMN_NAME_EMAIL + " = '" + email + "' AND " +
                        UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS + " = " + subscriptionStatus + " AND " +
                        UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED + " = '" + lastUpdated + "'";
        return countUsersWithSelection(selection);
    }

}