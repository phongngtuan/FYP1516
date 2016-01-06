package com.ntu.phongnt.healthdroid.db.data;

import android.provider.BaseColumns;

public final class DataContract {
    public DataContract() {
    }

    public static abstract class DataEntry implements BaseColumns {
        public static final String TABLE_NAME = "data";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_USER = "user";
    }
}
