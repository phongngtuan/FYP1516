package com.ntu.phongnt.healthdroid.messaging.entities;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import java.util.List;

import static com.ntu.phongnt.healthdroid.messaging.OfyService.ofy;

@Entity
public class SubscriptionRecord {
    @Id
    private Long id;
    @Index
    private Ref<HealthDroidUser> subscriber;
    @Parent
    private Ref<HealthDroidUser> target;

    public SubscriptionRecord() {

    }

    public HealthDroidUser getSubscriber() {
        return ofy().load().ref(subscriber).now();
    }

    public void setSubscriber(Ref<HealthDroidUser> subscriber) {
        this.subscriber = subscriber;
    }

    public HealthDroidUser getTarget() {
        return ofy().load().ref(target).now();
    }

    public void setTarget(Ref<HealthDroidUser> target) {
        this.target = target;
    }

    public static List<SubscriptionRecord> getAllSubscriptions() {
        return ofy().load().type(SubscriptionRecord.class).list();
    }
}