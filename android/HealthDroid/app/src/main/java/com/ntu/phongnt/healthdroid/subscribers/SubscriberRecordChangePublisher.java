package com.ntu.phongnt.healthdroid.subscribers;

public interface SubscriberRecordChangePublisher {

    void registerSubscriberRecordListener(SubscriberRecordChangeListener listener);

    void unregisterSubscriberRecordListener(SubscriberRecordChangeListener listener);

    void notifyPendingRequestAccepted(Long subscriptionId);
}
