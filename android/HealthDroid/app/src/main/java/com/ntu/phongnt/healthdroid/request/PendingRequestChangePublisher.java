package com.ntu.phongnt.healthdroid.request;

public interface PendingRequestChangePublisher {

    void registerPendingRequestListener(PendingRequestChangeListener listener);

    void unregisterPendingRequestListener(PendingRequestChangeListener listener);

    void notifyPendingRequestAccepted(Long subscriptionId);
}
