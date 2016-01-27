package com.ntu.phongnt.healthdroid.graph.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.ntu.phongnt.healthdroid.graph.GraphFragment;

public class TimeRangeDialogFragment extends DialogFragment {
    public static final String DAY = "Day";
    public static final String WEEK = "Week";
    public static final String MONTH = "Month";
    public static final String[] choices = {DAY, WEEK, MONTH};
    public TimeRangePickerListener listener = null;

    public TimeRangeDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Log.d(GraphFragment.TAG, "Creating dialog");
        builder
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onTimeRangePicked(which);
                    }
                })
                .setPositiveButton("Wai?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    public static interface TimeRangePickerListener {
        void onTimeRangePicked(int which);
    }
}
