package com.ntu.phongnt.healthdroid.request;

import com.ntu.phongnt.healthdroid.services.subscription.PendingRequest;

import java.util.List;

public interface PendingRequestChangeListener {
    void pendingRequestAccepted(Long subscriptionId);

    void pendingRequestLoaded(List<PendingRequest> pendingRequestList);
}
