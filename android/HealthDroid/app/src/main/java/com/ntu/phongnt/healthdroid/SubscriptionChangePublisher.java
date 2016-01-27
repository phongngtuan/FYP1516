package com.ntu.phongnt.healthdroid;

public interface SubscriptionChangePublisher {
    void registerSubscriptionListener(SubscriptionChangeListener listener);

    void unregisterSubscriptionListener(SubscriptionChangeListener listener);

    void notifySubscriptionChange();
}
