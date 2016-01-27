package com.ntu.phongnt.healthdroid.subscription;

public interface SubscriptionChangePublisher {
    void registerSubscriptionListener(SubscriptionChangeListener listener);

    void unregisterSubscriptionListener(SubscriptionChangeListener listener);

    void notifySubscriptionChange();
}
