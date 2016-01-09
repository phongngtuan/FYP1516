package com.ntu.phongnt.healthdroid.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.ntu.phongnt.healthdroid.MainActivity;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.data.data.Data;
import com.ntu.phongnt.healthdroid.db.data.DataHelper;
import com.ntu.phongnt.healthdroid.services.DataFactory;

import java.io.IOException;
import java.util.Date;

public class HomeFragment extends Fragment implements Button.OnClickListener {
    private static String TAG = "HomeFragment";
    private static HomeFragment instance;

    DataHelper db = null;
    String accountName = null;

    private GoogleSignInAccount account;
    private Button button = null;
    private TextView userField = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.content_home, container, false);
        setRetainInstance(true);
        db = DataHelper.getInstance(getActivity());
        button = (Button) view.findViewById(R.id.send_button);
        button.setOnClickListener(this);
        button = (Button) view.findViewById(R.id.submit_btn);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText valueField = (EditText) view.findViewById(R.id.edit_field);
                Integer value = Integer.valueOf(valueField.getText().toString());
                new PostDataRecordsToEndpoint().execute(value);
                Toast.makeText(getActivity(), accountName, Toast.LENGTH_SHORT).show();
            }
        });
        userField = (TextView) view.findViewById(R.id.user_id);


        return view;
    }

    @Override
    public void onClick(View v) {
    }

    private class PostDataRecordsToEndpoint extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            //TODO: move credential check to the calling method
            GoogleAccountCredential credential =
                    ((MainActivity) getActivity()).getCredential();
            if (credential.getSelectedAccountName() != null) {
                Data dataService = DataFactory.getInstance();
                for (Integer value : params) {
                    try {
                        dataService.add(new DateTime(new Date()), value).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
