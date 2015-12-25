package com.ntu.phongnt.healthdroid.fragments;

import android.accounts.AccountManager;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.util.DateTime;
import com.ntu.phongnt.healthdroid.BuildConfig;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.data.data.Data;
import com.ntu.phongnt.healthdroid.data.data.model.DataRecord;
import com.ntu.phongnt.healthdroid.db.DataHelper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements Button.OnClickListener, GoogleSignInListener {
    private static final int REQUEST_ACCOUNT_PICKER = 2;
    private static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";
    private static String TAG = "HomeFragment";
    private static HomeFragment instance;

    DataHelper db = null;
    String accountName = null;
    GoogleAccountCredential credential = null;
    SharedPreferences settings = null;

    private GoogleSignInAccount account;
    private Button button = null;
    private TextView userField = null;
    private Button submit_btn = null;

    private String message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        //Instance variables initializations
        settings = getActivity().getSharedPreferences("HealthDroid", 0);
        credential = GoogleAccountCredential.usingAudience(
                getActivity(),
                "server:client_id:" + BuildConfig.WEB_CLIENT_ID);
        return view;
    }

    @Override
    public void onClick(View v) {
        new GetDataRecordsFromEndpoint().execute();
    }

    @Override
    public void onSignedIn(GoogleSignInAccount account) {
        this.account = account;
        userField.setText(account.getId());
    }

    // setSelectedAccountName definition
    private void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.apply();
        credential.setSelectedAccountName(accountName);
        this.accountName = accountName;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    String accountName =
                            data.getExtras().getString(
                                    AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        Log.d(TAG, "Authorized complete");
                        setSelectedAccountName(accountName);
                        // User is authorized.

                    }
                }
                break;
        }
    }

    private class PostDataRecordsToEndpoint extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            Data.Builder builder = new Data.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),
                    credential)
                    .setRootUrl("http://192.168.1.28:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });

            if (credential.getSelectedAccountName() != null) {
                // Already signed in, begin app!
                Data dataService = builder.build();
                for (Integer value : params) {
                    try {
                        dataService.add(new DateTime(new Date()), value).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "already signed in");
            } else {
                // Not signed in, show login window or request an account.
                Log.d(TAG, "Not signed in");
                startActivityForResult(credential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }

            return null;
        }
    }

    private class GetDataRecordsFromEndpoint extends AsyncTask<Void, Void, Void> {
        //TODO: Complete the features
        @Override
        protected Void doInBackground(Void... params) {
            Data dataService = null;
            SharedPreferences dataPreferences = getActivity().
                    getSharedPreferences("DATA_PREFERENCES", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = dataPreferences.edit();

            String lastUpdatedPreference = dataPreferences.getString(DataHelper.LAST_UPDATED, DataHelper.ZERO_TIME);
            Date lastUpdate = DataHelper.getDate(lastUpdatedPreference);
            Date latestDateFromData = new Date();
            latestDateFromData.setTime(lastUpdate.getTime());

            if (dataService == null) {
                Data.Builder builder = new Data.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null)
                        .setRootUrl("http://192.168.1.28:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                dataService = builder.build();
            }

            try {
                List<DataRecord> dataRecordList = dataService.get().execute().getItems();

                Log.i(TAG, "dataRecord size = " + dataRecordList.size());
                int count = 0;
                SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
                for (DataRecord d : dataRecordList) {
                    Date dateFromData = DataHelper.getDate(d.getDate().toStringRfc3339());
                    if (dateFromData.after(lastUpdate)) {
                        count++;
                        latestDateFromData = dateFromData;
                        ContentValues values = new ContentValues();
                        values.put(DataHelper.VALUE, d.getValue());
                        values.put(DataHelper.CREATED_AT, d.getDate().toStringRfc3339());
                        sqLiteDatabase.insert(DataHelper.TABLE,
                                DataHelper.VALUE,
                                values);
                    }
                }
                editor.putString(DataHelper.LAST_UPDATED, DataHelper.toString(latestDateFromData));
                editor.apply();
                Log.d(TAG, "last Updated = " + lastUpdate);
                Log.d(TAG, "Updated count= " + count);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast toast = Toast.makeText(getActivity(), accountName, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
