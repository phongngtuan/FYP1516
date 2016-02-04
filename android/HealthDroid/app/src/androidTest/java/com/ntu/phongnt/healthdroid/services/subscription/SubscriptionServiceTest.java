package com.ntu.phongnt.healthdroid.services.subscription;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SubscriptionServiceTest {
    @Rule
    public final ServiceTestRule subscriptionServiceTestRule = new ServiceTestRule();

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testStartAcceptRequest() throws Exception {
        subscriptionServiceTestRule.startService(
                new Intent(InstrumentationRegistry.getTargetContext(), SubscriptionService.class)
        );

    }
}