package com.ntu.phongnt.healthdroid.messaging.entities;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class DataRecord {

    @Id
    public Long id;

    @Parent
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<HealthDroidUser> user;

    private int value;
    private String identifier;

    public DataRecord() {
    }

    public float getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Key<HealthDroidUser> getUser() {
        return user;
    }

    public void setUser(Key<HealthDroidUser> user) {
        this.user = user;
    }
}
