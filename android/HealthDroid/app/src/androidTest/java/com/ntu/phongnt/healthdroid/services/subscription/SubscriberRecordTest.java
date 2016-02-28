package com.ntu.phongnt.healthdroid.services.subscription;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SubscriberRecordTest {
    Long id = 123L;
    String subscriber = "subscriber@test.com";
    String target = "target@test.com";
    SubscriberRecord subscriberRecord = new SubscriberRecord(id, subscriber, target);

    @Test
    public void testUnmarshalling() throws Exception {
        Intent intent = new Intent();
        intent.putExtra("Test", subscriberRecord);
        SubscriberRecord receivedSubscriberRecord = intent.getParcelableExtra("Test");
        assertEquals(id, receivedSubscriberRecord.getId());
        assertEquals(subscriber, receivedSubscriberRecord.getSubscriber());
        assertEquals(target, receivedSubscriberRecord.getTarget());
    }

    @Test
    public void testParcelableExtra() throws Exception {
        Intent intent = new Intent();
        ArrayList<SubscriberRecord> subscriberRecords = new ArrayList<>();
        subscriberRecords.add(subscriberRecord);
        subscriberRecords.add(subscriberRecord);
        subscriberRecords.add(subscriberRecord);
        assertEquals(3, subscriberRecords.size());
        intent.putParcelableArrayListExtra(SubscriptionService.EXTRA_PARAM_REQUESTS, subscriberRecords);
        ArrayList<SubscriberRecord> receivedSubscriberRecords =
                intent.getParcelableArrayListExtra(SubscriptionService.EXTRA_PARAM_REQUESTS);
        assertEquals(3, receivedSubscriberRecords.size());
        SubscriberRecord firstSubscriberRecord = receivedSubscriberRecords.get(0);
        assertEquals(id, firstSubscriberRecord.getId());
        assertEquals(subscriber, firstSubscriberRecord.getSubscriber());
        assertEquals(target, firstSubscriberRecord.getTarget());
    }
}