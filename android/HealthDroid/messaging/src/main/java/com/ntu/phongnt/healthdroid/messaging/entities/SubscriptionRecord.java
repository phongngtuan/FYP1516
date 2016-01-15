package com.ntu.phongnt.healthdroid.messaging.entities;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class SubscriptionRecord {
    @Id
    Long id;
    @Index
    private Boolean isAccepted;
    @Index
    private transient Ref<HealthDroidUser> subscriber;
    @Parent
    private transient Ref<HealthDroidUser> target;

    public SubscriptionRecord() {
        isAccepted = false;
    }

    public SubscriptionRecord(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public Long getId() {
        return id;
    }

    public HealthDroidUser getSubscriber() {
        return subscriber.get();
    }

    public void setSubscriber(HealthDroidUser subscriber) {
        this.subscriber = Ref.create(subscriber);
    }

    public HealthDroidUser getTarget() {
        return target.get();
    }

    public void setTarget(HealthDroidUser target) {
        this.target = Ref.create(target);
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
}
