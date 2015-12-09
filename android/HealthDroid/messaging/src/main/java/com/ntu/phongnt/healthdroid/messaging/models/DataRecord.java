package com.ntu.phongnt.healthdroid.messaging.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class DataRecord {

    @Id
    public Long id;

    private int value;
    private String identifier;

    public DataRecord() {
    }

    public DataRecord(long id) {
        this.id = id;
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
}
