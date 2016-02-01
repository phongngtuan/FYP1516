package com.ntu.phongnt.healthdroid.data;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.View;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.data.DataContract;

public class DataFragment extends ListFragment {
    HealthDroidDatabaseHelper db = null;
    private Cursor current = null;
    private AsyncTask task = null;

    public DataFragment() {
        super();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        android.support.v4.widget.CursorAdapter adapter =
                new android.support.v4.widget.SimpleCursorAdapter(
                        getActivity(),
                        R.layout.data_row,
                        current,
                        new String[]{
                                DataContract.DataEntry.COLUMN_NAME_VALUE,
                                DataContract.DataEntry.COLUMN_NAME_DATE
                        },
                        new int[]{
                                R.id.value,
                                R.id.date},
                        0);
        setListAdapter(adapter);

        if (db == null) {
            db = HealthDroidDatabaseHelper.getInstance(getActivity());
            task = new LoadCursorTaskLoadData(db).execute();
        }
    }

    private class LoadCursorTaskLoadData extends LoadDataCursorTask<Void> {
        public LoadCursorTaskLoadData(HealthDroidDatabaseHelper db) {
            super(db);
        }

        @Override
        public void onPostExecute(Cursor result) {
            ((CursorAdapter) getListAdapter()).changeCursor(result);
            task = null;
        }
    }

}
