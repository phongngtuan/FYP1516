package com.ntu.phongnt.healthdroid.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
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
import com.ntu.phongnt.healthdroid.LoginActivity;
import com.ntu.phongnt.healthdroid.MainActivity;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.data.data.Data;
import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.graph.util.TitleUtil;
import com.ntu.phongnt.healthdroid.services.DataFactory;
import com.ntu.phongnt.healthdroid.services.data.GetDataRecordsFromEndpointTask;
import com.ntu.phongnt.healthdroid.services.subscription.SubscriptionService;

import java.io.IOException;
import java.util.Date;

public class HomeFragment extends Fragment {
    public static String TAG = "HOME_FRAG";
    public static String TITLE = "Home";

    private static HomeFragment instance;

    HealthDroidDatabaseHelper db = null;
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
        db = HealthDroidDatabaseHelper.getInstance(getActivity());
        button = (Button) view.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetDataRecordsFromEndpointTask(getActivity()).execute();
            }
        });
        button = (Button) view.findViewById(R.id.button2);
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
        String signedInAccount = getActivity().getSharedPreferences(LoginActivity.USER_PREFERENCE, Context.MODE_PRIVATE)
                .getString(LoginActivity.USER_ACCOUNT, "N/A");
        userField.setText(signedInAccount);

        //Set title
        TitleUtil.setSupportActionBarTitle(getActivity(), TITLE);

        //Playing around
        Button btn3 = (Button) view.findViewById(R.id.button3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification notification = new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("My Tittle")
                        .setContentText(
                                "My notification"
                        )
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setVibrate(new long[0])
                        .build();
                NotificationManager notificationManager =
                        (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
            }
        });

        Button btn4 = (Button) view.findViewById(R.id.button4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubscriptionService.startUpdateUserList(getActivity());
            }
        });

        return view;
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
