package com.ntu.phongnt.healthdroid.services.subscription;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PendingRequestTest {
    Long id = 123L;
    String subscriber = "subscriber@test.com";
    String target = "target@test.com";
    PendingRequest pendingRequest = new PendingRequest(id, subscriber, target);

    @Test
    public void testUnmarshalling() throws Exception {
        Intent intent = new Intent();
        intent.putExtra("Test", pendingRequest);
        PendingRequest receivedPendingRequest = intent.getParcelableExtra("Test");
        assertEquals(id, receivedPendingRequest.getId());
        assertEquals(subscriber, receivedPendingRequest.getSubscriber());
        assertEquals(target, receivedPendingRequest.getTarget());
    }

    @Test
    public void testParcelableExtra() throws Exception {
        Intent intent = new Intent();
        ArrayList<PendingRequest> pendingRequests = new ArrayList<>();
        pendingRequests.add(pendingRequest);
        pendingRequests.add(pendingRequest);
        pendingRequests.add(pendingRequest);
        assertEquals(3, pendingRequests.size());
        intent.putParcelableArrayListExtra(SubscriptionService.EXTRA_PARAM_REQUESTS, pendingRequests);
        ArrayList<PendingRequest> receivedPendingRequests =
                intent.getParcelableArrayListExtra(SubscriptionService.EXTRA_PARAM_REQUESTS);
        assertEquals(3, receivedPendingRequests.size());
        PendingRequest firstPendingRequest = receivedPendingRequests.get(0);
        assertEquals(id, firstPendingRequest.getId());
        assertEquals(subscriber, firstPendingRequest.getSubscriber());
        assertEquals(target, firstPendingRequest.getTarget());
    }
}