package com.ntu.phongnt.healthdroid;

import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.ntu.phongnt.healthdroid.data.DataFragment;
import com.ntu.phongnt.healthdroid.data.user.User;
import com.ntu.phongnt.healthdroid.data.user.model.HealthDroidUser;
import com.ntu.phongnt.healthdroid.gcm.QuickstartPreferences;
import com.ntu.phongnt.healthdroid.gcm.RegistrationIntentService;
import com.ntu.phongnt.healthdroid.graph.view.GraphTabsFragment;
import com.ntu.phongnt.healthdroid.home.HomeFragment;
import com.ntu.phongnt.healthdroid.request.PendingRequestChangeListener;
import com.ntu.phongnt.healthdroid.request.PendingRequestChangePublisher;
import com.ntu.phongnt.healthdroid.request.PendingRequestFragment;
import com.ntu.phongnt.healthdroid.services.DataFactory;
import com.ntu.phongnt.healthdroid.services.RegistrationFactory;
import com.ntu.phongnt.healthdroid.services.SubscriptionFactory;
import com.ntu.phongnt.healthdroid.services.UserFactory;
import com.ntu.phongnt.healthdroid.services.subscription.PendingRequest;
import com.ntu.phongnt.healthdroid.services.subscription.SubscriptionService;
import com.ntu.phongnt.healthdroid.subscription.SubscriptionChangeListener;
import com.ntu.phongnt.healthdroid.subscription.SubscriptionChangePublisher;
import com.ntu.phongnt.healthdroid.subscription.UserFragment;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SubscriptionChangePublisher,
        PendingRequestChangePublisher {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    public static final String SHARED_PREFERENCE_NAME = "HealthDroid";

    private HomeFragment homeFragment = null;
    private DataFragment dataFragment = null;
    private UserFragment userFragment = null;
    private GraphTabsFragment graphTabsFragment = null;
    private PendingRequestFragment pendingRequestFragment = null;
    private List<SubscriptionChangeListener> subscriptionChangeListeners = new ArrayList<>();
    private List<PendingRequestChangeListener> pendingRequestChangeListeners = new ArrayList<>();

    private ImageView profileImage = null;

    private static final int REQUEST_ACCOUNT_PICKER = 2;
    public static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";
    GoogleAccountCredential credential = null;
    SharedPreferences settings = null;

    private BroadcastReceiver tokenBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Layout
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        profileImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);

        //Instance variables initializations
        //Broadcasting
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        //receiver
        tokenBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "broadcast received");
                Toast.makeText(context, "Sent token to server", Toast.LENGTH_SHORT).show();
            }
        };
        IntentFilter tokenIntentFilter = new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE);
        broadcastManager.registerReceiver(tokenBroadcastReceiver, tokenIntentFilter);

        //Subscription status receiver
        BroadcastReceiver subscriptionStatusBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "receive broadcast: Subscription status changed");
                notifySubscriptionChange();
            }
        };
        IntentFilter subscriptionStatusChangedFilter =
                new IntentFilter(QuickstartPreferences.SUBSCRIPTION_REQUEST_CHANGED);
        broadcastManager.registerReceiver(
                subscriptionStatusBroadcastReceiver, subscriptionStatusChangedFilter);

        //Pending request accepted
        BroadcastReceiver pendingRequestChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "received pending request accepted broadcast");
                long subscriptionId = intent.getLongExtra(SubscriptionService.EXTRA_PARAM_SUBSCRIPTION_ID, 0);
                notifyPendingRequestAccepted(subscriptionId);
            }
        };
        IntentFilter pendingRequestChangedFilter =
                new IntentFilter(QuickstartPreferences.PENDING_REQUEST_ACCEPTED);
        broadcastManager.registerReceiver(pendingRequestChangedReceiver, pendingRequestChangedFilter);

        //Pending request loaded
        BroadcastReceiver pendingRequestLoadedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "received pending request loaded broadcast");
                ArrayList<PendingRequest> pendingRequests = intent.getParcelableArrayListExtra(SubscriptionService.EXTRA_PARAM_REQUESTS);
                notifyPendingRequestLoaded(pendingRequests);
            }
        };
        IntentFilter pendingRequestLoadedFilter =
                new IntentFilter(QuickstartPreferences.PENDING_REQUESTS_LOADED);
        broadcastManager.registerReceiver(pendingRequestLoadedReceiver, pendingRequestLoadedFilter);

        settings = getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        credential = GoogleAccountCredential.usingAudience(
                this,
                "server:client_id:" + BuildConfig.WEB_CLIENT_ID);

        pickAccount();

        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, homeFragment);
            fragmentTransaction.commit();
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
                        handleAccountSelected(accountName);
                    }
                }
                break;
        }
    }

    private void handleAccountSelected(String accountName) {
        Log.d(TAG, "Authorized complete");
        Log.d(TAG, "Account name: " + accountName);
        setSelectedAccountName(accountName);
        constructServices(credential);

        // Start IntentService to register this application with GCM.
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            Log.i(TAG, "Here");
        }
        //Register this email to endpoint
        new RegisterUserToEndpoint().execute();
        //TODO: refactor to appropriate place
        //Load list user
        SubscriptionService.startUpdateUserList(this);

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

//    @Override
//    protected void handleSignInResult(GoogleSignInResult result) {
//        Log.d(TAG, "in handle sign in result");
//        account = result.getSignInAccount();
//        if (account != null) {
//            Uri personPhoto = account.getPhotoUrl();
//            new LoadProfileImageTask(profileImage).execute(personPhoto.toString());
//        }
//    }

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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HOME_FRAGMENT");
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
            }
            if (!homeFragment.isVisible())
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, homeFragment, "HOME_FRAGMENT").commit();
        } else if (id == R.id.nav_graph) {
            graphTabsFragment = (GraphTabsFragment) getSupportFragmentManager().findFragmentByTag("GRAPH_TABS_FRAGMENT");
            if (graphTabsFragment == null) {
                graphTabsFragment = new GraphTabsFragment();
            }
            if (!graphTabsFragment.isVisible())
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, graphTabsFragment, "GRAPH_TABS_FRAGMENT").commit();

        } else if (id == R.id.nav_data) {
            dataFragment = (DataFragment) getSupportFragmentManager().findFragmentByTag("DATA_FRAGMENT");
            if (dataFragment == null) {
                dataFragment = new DataFragment();
            }
            if (!dataFragment.isVisible())
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, dataFragment, "DATA_FRAGMENT").commit();

        } else if (id == R.id.nav_user) {
            userFragment = (UserFragment) getSupportFragmentManager().findFragmentByTag("USER_FRAGMENT");
            if (userFragment == null) {
                userFragment = UserFragment.newInstance(this);
            }
            if (!userFragment.isVisible())
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, userFragment, "USER_FRAGMENT").commit();

        } else if (id == R.id.nav_pending_request) {
            pendingRequestFragment = (PendingRequestFragment) getSupportFragmentManager().findFragmentByTag("PENDING_REQUEST_FRAGMENT");
            if (pendingRequestFragment == null) {
                pendingRequestFragment = PendingRequestFragment.getInstance(this);
            }
            if (!pendingRequestFragment.isVisible())
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, pendingRequestFragment, "PENDING_REQUEST_FRAGMENT").commit();
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void pickAccount() {
        String savedAccountName = settings.getString(PREF_ACCOUNT_NAME, null);
        if (credential.getSelectedAccountName() == null)
            if (savedAccountName == null) {
                Intent newChooseAccountIntent = AccountPicker.newChooseAccountIntent(null,
                        null,
                        new String[]{GoogleAccountManager.ACCOUNT_TYPE},
                        false,
                        null,
                        null,
                        null,
                        null);
                startActivityForResult(newChooseAccountIntent, REQUEST_ACCOUNT_PICKER);
            }
            else {
                handleAccountSelected(savedAccountName);
            }
    }

    private void constructServices(GoogleAccountCredential credential) {
        String rootUrl = getResources().getString(R.string.rootUrl);
        Log.d("Root_url", rootUrl);
        SubscriptionFactory.build(credential, rootUrl);
        UserFactory.build(credential, rootUrl);
        RegistrationFactory.build(credential, rootUrl);
        DataFactory.build(credential, rootUrl);
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
    }

    @Override
    public void registerSubscriptionListener(SubscriptionChangeListener listener) {
        subscriptionChangeListeners.add(listener);
    }

    @Override
    public void unregisterSubscriptionListener(SubscriptionChangeListener listener) {
        subscriptionChangeListeners.remove(listener);
    }

    @Override
    public void notifySubscriptionChange() {
        for (SubscriptionChangeListener listener : subscriptionChangeListeners)
            listener.subscriptionChanged();
    }

    @Override
    public void registerPendingRequestListener(PendingRequestChangeListener listener) {
        pendingRequestChangeListeners.add(listener);
    }

    @Override
    public void unregisterPendingRequestListener(PendingRequestChangeListener listener) {
        pendingRequestChangeListeners.remove(listener);
    }

    @Override
    public void notifyPendingRequestAccepted(Long subscriptionId) {
        for (PendingRequestChangeListener listener : pendingRequestChangeListeners)
            listener.pendingRequestAccepted(subscriptionId);
    }

    private void notifyPendingRequestLoaded(ArrayList<PendingRequest> loadedPendingRequests) {
        for (PendingRequestChangeListener listener : pendingRequestChangeListeners) {
            listener.pendingRequestLoaded(loadedPendingRequests);
        }
    }

    private class LoadProfileImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return bmp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bmImage.setImageBitmap(bitmap);
        }
    }

    private class RegisterUserToEndpoint extends AsyncTask<Void, Void, HealthDroidUser> {
        @Override
        protected HealthDroidUser doInBackground(Void... params) {
            User userService = UserFactory.getInstance();
            HealthDroidUser healthDroidUser = null;
            try {
                List<HealthDroidUser> users = userService.get()
                        .set("userId", credential.getSelectedAccountName())
                        .execute().getItems();
                if (users.isEmpty() || users.get(0).isEmpty()) {
                    healthDroidUser = userService.add().execute();
                    Log.d(TAG, "Registered email");
                } else {
                    healthDroidUser = users.get(0);
                    Log.d(TAG, "Found registered email " + users.get(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return healthDroidUser;
        }

        @Override
        protected void onPostExecute(HealthDroidUser resultedHealthDroidUser) {
            super.onPostExecute(resultedHealthDroidUser);
        }
    }
}
