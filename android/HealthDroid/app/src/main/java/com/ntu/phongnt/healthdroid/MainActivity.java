package com.ntu.phongnt.healthdroid;

import android.accounts.AccountManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.ntu.phongnt.healthdroid.data.user.User;
import com.ntu.phongnt.healthdroid.fragments.DataFragment;
import com.ntu.phongnt.healthdroid.fragments.GraphFragment;
import com.ntu.phongnt.healthdroid.fragments.HomeFragment;
import com.ntu.phongnt.healthdroid.fragments.UserFragment;
import com.ntu.phongnt.healthdroid.gcm.RegistrationIntentService;
import com.ntu.phongnt.healthdroid.util.UserUtil;

import java.io.IOException;

public class MainActivity extends SignInActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private HomeFragment homeFragment = null;
    private GraphFragment graphFragment = null;
    private DataFragment dataFragment = null;
    private UserFragment userFragment = null;

    private static final int REQUEST_ACCOUNT_PICKER = 2;
    private static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";
    GoogleAccountCredential credential = null;
    SharedPreferences settings = null;
    private String accountName = null;

    public GoogleSignInAccount getAccount() {
        return account;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            Log.i(TAG, "Here");
        }

        //Instance variables initializations
        settings = getSharedPreferences("HealthDroid", 0);
        credential = GoogleAccountCredential.usingAudience(
                this,
                "server:client_id:" + BuildConfig.WEB_CLIENT_ID);

        pickAccount();

        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, homeFragment);
            fragmentTransaction.commit();
        }

        Log.d(TAG, "Is connected = " + googleApiClient.isConnected());
        if (!googleApiClient.isConnected()) {
            signIn();
        }
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
                        Log.d(TAG, "Account name: " + accountName);
                        setSelectedAccountName(accountName);
                        // User is authorized.
                        new RegisterUserToEndpoint().execute();
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "in handle sign in result");
        account = result.getSignInAccount();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        Log.i(TAG, "onOptionsItemSelected()");
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            homeFragment = (HomeFragment) getFragmentManager().findFragmentByTag("HOME_FRAGMENT");
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
            }
            if (!homeFragment.isVisible())
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, homeFragment, "HOME_FRAGMENT").commit();
        } else if (id == R.id.nav_graph) {
            graphFragment = (GraphFragment) getFragmentManager().findFragmentByTag("GRAPH_FRAGMENT");
            if (graphFragment == null) {
                graphFragment = new GraphFragment();
            }
            if (!graphFragment.isVisible())
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, graphFragment, "GRAPH_FRAGMENT").commit();
        } else if (id == R.id.nav_data) {
            dataFragment = (DataFragment) getFragmentManager().findFragmentByTag("DATA_FRAGMENT");
            if (dataFragment == null) {
                dataFragment = new DataFragment();
            }
            if (!dataFragment.isVisible())
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, dataFragment, "DATA_FRAGMENT").commit();
        } else if (id == R.id.nav_user) {
            userFragment = (UserFragment) getFragmentManager().findFragmentByTag("USER_FRAGMENT");
            if (userFragment == null)
                userFragment = UserFragment.newInstance(1);
            if (!userFragment.isVisible())
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, userFragment, "USER_FRAGMENT").commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void pickAccount() {
        if (credential.getSelectedAccountName() == null) {
            startActivityForResult(credential.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER);
        }
    }

    public GoogleAccountCredential getCredential() {
        return credential;
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.apply();
        credential.setSelectedAccountName(accountName);
        this.accountName = accountName;
    }

    private class RegisterUserToEndpoint extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            User userService = UserUtil.getUserService(getCredential());
            try {
                userService.add().execute();
                Log.d(TAG, "Registered user");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
