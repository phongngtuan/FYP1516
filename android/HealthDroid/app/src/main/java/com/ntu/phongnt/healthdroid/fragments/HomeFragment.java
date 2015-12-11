package com.ntu.phongnt.healthdroid.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.data.data.Data;
import com.ntu.phongnt.healthdroid.data.data.model.DataRecord;
import com.ntu.phongnt.healthdroid.db.DatabaseHelper;

import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment implements Button.OnClickListener, GoogleSignInListener {
    private static String TAG = "HomeFragment";
    private static HomeFragment instance;
    DatabaseHelper db = null;
    private GoogleSignInAccount account;

    private Button button;
    private TextView userField = null;

    private String message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_home, container, false);
        setRetainInstance(true);
        button = (Button) view.findViewById(R.id.send_button);
        button.setOnClickListener(this);
        userField = (TextView) view.findViewById(R.id.user_id);
        return view;
    }

    @Override
    public void onClick(View v) {
        new GetDataTask().execute();
    }

    @Override
    public void onSignedIn(GoogleSignInAccount account) {
        this.account = account;
        userField.setText(account.getId());
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {
        //TODO: Complete the features
        @Override
        protected Void doInBackground(Void... params) {
            Data dataService = null;
            message = "Hello World!";
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
