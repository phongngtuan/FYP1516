package com.ntu.phongnt.healthdroid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.DatabaseHelper;

public class HomeFragment extends Fragment implements Button.OnClickListener, GoogleSignInListener {
    private static HomeFragment instance;
    DatabaseHelper db = null;
    private GoogleSignInAccount account;

    private Button button;
    private TextView userField = null;

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
        Toast toast = Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onSignedIn(GoogleSignInAccount account) {
        this.account = account;
        userField.setText(account.getId());
    }
}
