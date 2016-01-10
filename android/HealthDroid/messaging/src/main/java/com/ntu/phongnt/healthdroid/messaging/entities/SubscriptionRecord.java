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
    public transient Ref<HealthDroidUser> subscriber;
    @Parent
    public transient Ref<HealthDroidUser> target;

    public SubscriptionRecord() {

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
//
//    public String getSubsciptions() {
//        return subscriber.getValue().getEmail()
//                + target.getValue().getEmail();
//    }
//
//    public static List<SubscriptionRecord> getAllSubscriptions() {
//        return ofy().load().type(SubscriptionRecord.class).list();
//    }
}
