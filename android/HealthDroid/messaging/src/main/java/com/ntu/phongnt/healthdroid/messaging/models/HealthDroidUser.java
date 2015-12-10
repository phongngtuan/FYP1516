package com.ntu.phongnt.healthdroid.messaging.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class HealthDroidUser {
    @Id
    private String id;

    public HealthDroidUser(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
