package com.ntu.phongnt.healthdroid.db.user;

import android.provider.BaseColumns;

public final class UserContract {

    public UserContract() {
    }

    public static abstract class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_SUBSCRIPTION_STATUS = "subscription_status";
        public static final String COLUMN_NAME_LAST_UPDATED = "last_updated";
        public static final int UNSUBSCRIBED = 0;
        public static final int PENDING = 1;
        public static final int SUBSCRIBED = 2;
    }
}
