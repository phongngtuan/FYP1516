package com.ntu.phongnt.healthdroid.db;

import android.provider.BaseColumns;

public final class UserContract {
    public UserContract() {
    }

    public static abstract class User implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_EMAIL = "email";

    }
}
