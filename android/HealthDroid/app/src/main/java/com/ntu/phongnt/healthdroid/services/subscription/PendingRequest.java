package com.ntu.phongnt.healthdroid.services.subscription;

import android.os.Parcel;
import android.os.Parcelable;

public class PendingRequest implements Parcelable {
    private Long id;
    private String subscriber;
    private String target;

    public PendingRequest(Long id, String subscriber, String target) {
        this.id = id;
        this.subscriber = subscriber;
        this.target = target;
    }

    protected PendingRequest(Parcel in) {
        id = in.readLong();
        target = in.readString();
        subscriber = in.readString();
    }

    public static final Creator<PendingRequest> CREATOR = new Creator<PendingRequest>() {
        @Override
        public PendingRequest createFromParcel(Parcel in) {
            return new PendingRequest(in);
        }

        @Override
        public PendingRequest[] newArray(int size) {
            return new PendingRequest[size];
        }
    };

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
