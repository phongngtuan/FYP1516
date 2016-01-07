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
    public DataSetPickerListener listener = null;
    private String[] choices;
    private boolean[] checkedItems;

    public static DataSetPickerFragment getInstance(String[] choices) {
        DataSetPickerFragment fragment = new DataSetPickerFragment();
        fragment.choices = choices;
        fragment.checkedItems = new boolean[choices.length];
        return fragment;
    }

    public DataSetPickerFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Log.d(GraphFragment.TAG, "Creating dialog");
        builder
                .setMultiChoiceItems(choices, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                })
                .setPositiveButton("Wai?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: Fill in the blank
                        for (int i = 0; i < choices.length; i++) {
                            if (checkedItems[i]) {
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    public interface DataSetPickerListener {
        void onDataSetPicked(int item);
    }
}
