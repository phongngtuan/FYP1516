package com.ntu.phongnt.healthdroid.messaging;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.ntu.phongnt.healthdroid.messaging.entities.DataRecord;
import com.ntu.phongnt.healthdroid.messaging.entities.HealthDroidUser;
import com.ntu.phongnt.healthdroid.messaging.entities.RegistrationRecord;
import com.ntu.phongnt.healthdroid.messaging.entities.Subscription;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 */
public class OfyService {

    static {
        ObjectifyService.register(HealthDroidUser.class);
        ObjectifyService.register(RegistrationRecord.class);
        ObjectifyService.register(DataRecord.class);
        ObjectifyService.register(Subscription.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
