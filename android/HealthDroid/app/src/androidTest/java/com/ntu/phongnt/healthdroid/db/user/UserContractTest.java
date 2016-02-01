package com.ntu.phongnt.healthdroid.db.user;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.graph.util.DateHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UserContractTest {

    private HealthDroidDatabaseHelper db = null;
    private UserContract userContract = null;

    private String email = "user@test.com";
    private String anotherEmail = "another@email.com";
    private int subscriptionStatus = UserContract.UserEntry.UNSUBSCRIBED;
    private String lastUpdated = "2016-01-10T02:08:50.889+08:00";
    private String latestDateFromData = "2016-02-10T02:08:50.889+08:00";

    @Before
    public void setUp() throws Exception {
        RenamingDelegatingContext renamingDelegatingContext
                = new RenamingDelegatingContext(InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext(), "test_");

        db = new HealthDroidDatabaseHelper(renamingDelegatingContext, null);
        userContract = new UserContract(db);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testNewUser() throws Exception {
        userContract.newUser(email, subscriptionStatus, lastUpdated);
        assertEquals(1, countUsers(email, subscriptionStatus, lastUpdated));

        userContract.newUser(anotherEmail, null, null);
        assertEquals(1, countUsers(anotherEmail, UserContract.UserEntry.UNSUBSCRIBED, UserContract.UserEntry.ZERO_DATE));
    }

    @Test
    public void testInsertUser() throws Exception {
        //TODO: idk if i try to make it to complicated here
        //Insert user for the 1st time, create new row
        userContract.insertUser(email, subscriptionStatus, lastUpdated);
        assertEquals(1,
                countUsers(email, subscriptionStatus, lastUpdated));

        //Insert again, overwrite existing entry with all parameters
        userContract.insertUser(email, UserContract.UserEntry.SUBSCRIBED, latestDateFromData);
        assertEquals(1,
                countUsers(email, UserContract.UserEntry.SUBSCRIBED, latestDateFromData));

        //insert again but update partially
        userContract.insertUser(email, null, lastUpdated);
        assertEquals(1,
                countUsers(email, UserContract.UserEntry.SUBSCRIBED, lastUpdated));

        //insert a another user
        userContract.insertUser(anotherEmail, null, null);
        assertEquals(1,
                countUsers(anotherEmail, UserContract.UserEntry.UNSUBSCRIBED, UserContract.UserEntry.ZERO_DATE));
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
        assertEquals(date, lastUpdatedFromDb);
        cursor.close();
    }

    @Test
    public void testUpdateSubscriptionStatus() throws Exception {
        userContract.newUser(email);
        userContract.updateSubscriptionStatus(email, UserContract.UserEntry.PENDING);
        assertTrue(countUsers(email, UserContract.UserEntry.PENDING, UserContract.UserEntry.ZERO_DATE) >= 1);
        userContract.updateSubscriptionStatus(email, UserContract.UserEntry.SUBSCRIBED);
        assertTrue(countUsers(email, UserContract.UserEntry.SUBSCRIBED, UserContract.UserEntry.ZERO_DATE) >= 1);
    }

    @Test
    public void testDeleteUser() throws Exception {
        userContract.newUser(email);
        userContract.newUser(anotherEmail);
        userContract.deleteUser(new String[]{email});
        assertEquals(0, countUsers(email));
        assertEquals(1, countUsers(anotherEmail));
    }

    @Test
    public void testUpdateLastUpdated() throws Exception {
        userContract.newUser(email);
        Date lastUpdate = new Date();
        userContract.updateLastUpdated(email, lastUpdate);
        assertEquals(1, countUsers(email, subscriptionStatus, DateHelper.formatAsRfc3992(lastUpdate)));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        userContract.newUser(email);
        String anotherEmail = "another@email.com";
        userContract.newUser(anotherEmail);
        List<UserContract.UserEntry> allUsers = userContract.getAllUsers();
        assertEquals(2, allUsers.size());
    }

    @Test
    public void testGetSubscribedUsers() throws Exception {
        userContract.newUser(email);
        String anotherEmail = "another@email.com";
        userContract.insertUser(anotherEmail, UserContract.UserEntry.SUBSCRIBED, lastUpdated);
        List<UserContract.UserEntry> subscribedUsers = userContract.getSubscribedUsers();
        assertNotNull(subscribedUsers);
        assertEquals(1, subscribedUsers.size());

        List<UserContract.UserEntry> subscribedUsersAndLocal = userContract.getSubscribedUsers(email);
        assertNotNull(subscribedUsersAndLocal);
        assertEquals(2, subscribedUsersAndLocal.size());
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