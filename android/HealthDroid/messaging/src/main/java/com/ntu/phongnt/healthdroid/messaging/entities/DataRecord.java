package com.ntu.phongnt.healthdroid.messaging.entities;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;

@Entity
public class DataRecord {

    @Id
    public Long id;

    @Parent
    private Ref<HealthDroidUser> user;
    private Date date;
    @Index
    private Date createdAt;

    private int value;
    private String identifier;

    public DataRecord() {
    }

    public HealthDroidUser getUser() {
        return user.get();
    }

    public void setUser(HealthDroidUser user) {
        this.user = Ref.create(user);
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
