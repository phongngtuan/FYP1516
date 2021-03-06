package com.ntu.phongnt.healthdroid.messaging.entities;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.List;

import static com.ntu.phongnt.healthdroid.messaging.OfyService.ofy;

@Entity
public class HealthDroidUser {
    @Id
    private String id;
    private String email;

    public HealthDroidUser() {
    }

    public HealthDroidUser(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static HealthDroidUser getUser(String userId) {
        if (userId == null)
            return null;
        Key<HealthDroidUser> key = Key.create(HealthDroidUser.class, userId);
        return ofy().load().key(key).now();
    }

    public static List<HealthDroidUser> getAllUsers() {
        return ofy().load().type(HealthDroidUser.class).list();
    }

}
