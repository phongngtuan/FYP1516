package com.ntu.phongnt.healthdroid.messaging.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class DataRecord {

    @Id
    Long id;

    private float value;

    public DataRecord(float value) {
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
