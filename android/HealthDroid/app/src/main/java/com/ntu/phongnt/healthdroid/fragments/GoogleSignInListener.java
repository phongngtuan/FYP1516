package com.ntu.phongnt.healthdroid.fragments;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface GoogleSignInListener {
    public void onSignedIn(GoogleSignInAccount account);
}
