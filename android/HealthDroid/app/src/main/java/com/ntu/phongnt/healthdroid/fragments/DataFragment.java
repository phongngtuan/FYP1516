package com.ntu.phongnt.healthdroid.fragments;

import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.DatabaseHelper;

public class DataFragment extends ListFragment {
    DatabaseHelper db = null;
    private Cursor current = null;
    private AsyncTask task=null;

    public DataFragment() {
        super();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(getActivity(), R.layout.row,
                        current, new String[]{
                        DatabaseHelper.VALUE},
                        new int[]{R.id.value},
                        0);
        setListAdapter(adapter);

        if (db == null) {
            db = DatabaseHelper.getInstance(getActivity());
            task = new LoadCursorTask().execute();
        }
    }

    abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {
        @Override
        public void onPostExecute(Cursor result) {
            ((CursorAdapter) getListAdapter()).changeCursor(result);
            task = null;
        }

        protected Cursor doQuery() {
            Cursor result =
                    db
                            .getReadableDatabase()
                            .query(DatabaseHelper.TABLE,
                                    new String[]{"ROWID AS _id",
                                            DatabaseHelper.VALUE},
                                    null, null, null, null, DatabaseHelper.VALUE);

            Log.i("DataFragment", String.valueOf(result.getCount()));

            return (result);
        }
    }

    private class LoadCursorTask extends BaseTask<Void> {
        @Override
        protected Cursor doInBackground(Void... params) {
            return (doQuery());
        }
    }

}