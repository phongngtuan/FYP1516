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
    private int type;
    @Index
    private Date createdAt;

    private String value;
    private String identifier;

    public DataRecord() {
    }

    public DataRecord(Date date, String value, int type) {
        this.date = date;
        this.value = value;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public HealthDroidUser getUser() {
        return user.get();
    }

    public void setUser(HealthDroidUser user) {
        this.user = Ref.create(user);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
