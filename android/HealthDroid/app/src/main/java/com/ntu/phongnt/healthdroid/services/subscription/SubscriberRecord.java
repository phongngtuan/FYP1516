package com.ntu.phongnt.healthdroid.services.subscription;

import android.os.Parcel;
import android.os.Parcelable;

public class SubscriberRecord implements Parcelable {
    public static final int ACCEPTED = 1;
    public static final int NOT_ACCEPTED = 0;
    public static final Creator<SubscriberRecord> CREATOR = new Creator<SubscriberRecord>() {
        @Override
        public SubscriberRecord createFromParcel(Parcel in) {
            return new SubscriberRecord(in);
        }

        @Override
        public SubscriberRecord[] newArray(int size) {
            return new SubscriberRecord[size];
        }
    };
    private Long id;
    private String subscriber;
    private String target;
    private Integer status;

    public SubscriberRecord(Long id, String subscriber, String target, Integer status) {
        this.id = id;
        this.subscriber = subscriber;
        this.target = target;
        this.status = status;
    }

    public SubscriberRecord(Long id, String subscriber, String target) {
        this(id, subscriber, target, 0);
    }

    protected SubscriberRecord(Parcel in) {
        id = in.readLong();
        target = in.readString();
        subscriber = in.readString();
        status = in.readInt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isAccepted() {
        return status == ACCEPTED;
    }

    public void setAccepted(boolean accepted) {
        if (accepted)
            status = ACCEPTED;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(subscriber);
        dest.writeString(target);
    }
}
