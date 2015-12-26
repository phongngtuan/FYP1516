package com.ntu.phongnt.healthdroid.messaging.entities;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.List;

import static com.ntu.phongnt.healthdroid.messaging.OfyService.ofy;

@Entity
public class Subscription {
    @Id
    private Long id;
    @Index
    private Ref<HealthDroidUser> subscriber;
    private Ref<HealthDroidUser> target;

    public Subscription() {

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

    public static List<Subscription> getAllSubscriptions() {
        return ofy().load().type(Subscription.class).list();
    }
}
