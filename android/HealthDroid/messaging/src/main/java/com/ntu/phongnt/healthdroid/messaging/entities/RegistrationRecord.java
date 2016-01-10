package com.ntu.phongnt.healthdroid.messaging.entities;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class RegistrationRecord {

    @Id
    Long id;

    @Index
    private String regId;

    @Parent
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Ref<HealthDroidUser> user;

    public RegistrationRecord() {
    }

    public void setUser(Ref<HealthDroidUser> user) {
        this.user = user;
    }

    public HealthDroidUser getUser() {
        return user.get();
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }
}