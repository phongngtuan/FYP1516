package com.ntu.phongnt.healthdroid.messaging.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.List;

import static com.ntu.phongnt.healthdroid.messaging.OfyService.ofy;

@Entity
public class Subscription {
    @Id
    private Long id;
    private HealthDroidUser subscriber;
    private HealthDroidUser target;

    public Subscription() {

    }

    public HealthDroidUser getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(HealthDroidUser subscriber) {
        this.subscriber = subscriber;
    }

    public HealthDroidUser getTarget() {
        return target;
    }

    public void setTarget(HealthDroidUser target) {
        this.target = target;
    }

    public static List<Subscription> getAllSubscriptions() {
        return ofy().load().type(Subscription.class).list();
    }
}
