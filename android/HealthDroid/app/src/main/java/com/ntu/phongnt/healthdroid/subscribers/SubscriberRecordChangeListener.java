package com.ntu.phongnt.healthdroid.subscribers;

import com.ntu.phongnt.healthdroid.services.subscription.SubscriberRecord;

import java.util.List;

public interface SubscriberRecordChangeListener {
    void pendingRequestAccepted(Long subscriptionId);

    void subscriberRecordLoaded(List<SubscriberRecord> subscriberRecordList);
}
