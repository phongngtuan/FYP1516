package com.ntu.phongnt.healthdroid.data;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.db.data.DataHelper;

class LoadDataCursorTask<T> extends AsyncTask<T, Void, Cursor> {
    private DataHelper db;

    public LoadDataCursorTask(DataHelper db) {
        this.db = db;
    }

    @Override
    protected Cursor doInBackground(T... params) {
        return doQuery();
    }

    protected Cursor doQuery() {
        Cursor result =
                db
                        .getReadableDatabase()
                        .query(DataContract.DataEntry.TABLE_NAME,
                                new String[]{
                                        DataContract.DataEntry.COLUMN_NAME_VALUE,
                                        DataContract.DataEntry.COLUMN_NAME_DATE
                                },
                                null,
                                null,
                                null,
                                null,
                                DataContract.DataEntry.COLUMN_NAME_DATE);

        Log.i("DataFragment", String.valueOf(result.getCount()));

        return (result);
    }
}
