package com.ntu.phongnt.healthdroid.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.ntu.phongnt.healthdroid.fragments.GraphFragment;

public class DataSetPickerFragment extends DialogFragment {
    public static final String[] choices = {"Nguyen", "Tuan", "Phong"};
    public DataSetPickerListener listener = null;

    public DataSetPickerFragment() {
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
                        listener.onDataSetPicked(which);
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

    public static interface DataSetPickerListener {
        void onDataSetPicked(int item);
    }
}
