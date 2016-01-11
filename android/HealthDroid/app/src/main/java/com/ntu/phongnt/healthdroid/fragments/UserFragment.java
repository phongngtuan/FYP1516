package com.ntu.phongnt.healthdroid.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.ntu.phongnt.healthdroid.MainActivity;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.data.subscription.Subscription;
import com.ntu.phongnt.healthdroid.data.subscription.model.SubscriptionRecord;
import com.ntu.phongnt.healthdroid.data.user.User;
import com.ntu.phongnt.healthdroid.data.user.model.HealthDroidUser;
import com.ntu.phongnt.healthdroid.fragments.adapter.MyUserRecyclerViewAdapter;
import com.ntu.phongnt.healthdroid.services.GetDataRecordsFromEndpointTask;
import com.ntu.phongnt.healthdroid.services.SubscriptionFactory;
import com.ntu.phongnt.healthdroid.services.UserFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class UserFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "USER_FRAGMENT";
    // TODO: Customize parameters
    private RecyclerView recyclerView = null;
    private int mColumnCount = 1;

    private List<HealthDroidUserWrapper> listUser = new ArrayList<>();

    public UserFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserFragment newInstance(int columnCount, GoogleAccountCredential credential) {
        //TODO: clean this up
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new ListUserTask().execute((GoogleAccountCredential) null);
        new GetSubscriptionsTask().execute();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        HealthDroidUserViewInteractionListener listener = new HealthDroidUserViewInteractionListener() {
            //TODO: implement
            @Override
            public void onItemClick(HealthDroidUserWrapper user) {
                Toast.makeText(getActivity(), user.healthDroidUser.getEmail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSubscribeClick(HealthDroidUserWrapper user, boolean alreadySubscribed) {
                if (alreadySubscribed) {
                    subscribe(user.healthDroidUser);
                    Toast.makeText(getActivity(), user.healthDroidUser.getEmail() + " subscribed", Toast.LENGTH_SHORT).show();
                } else {
                    unsubscribe(user.healthDroidUser);
                    Toast.makeText(getActivity(), user.healthDroidUser.getEmail() + " unsubscribed", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Set the adapter
        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            MyUserRecyclerViewAdapter viewAdapter = new MyUserRecyclerViewAdapter(listUser, listener);
            recyclerView.setAdapter(viewAdapter);
        }
        return view;
    }

    private void subscribe(HealthDroidUser user) {
        new SubscribeTask().execute(user);
    }

    private void unsubscribe(HealthDroidUser user) {
        new UnsubscribeTask().execute(user);
    }

    private void notifyChange() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public interface HealthDroidUserViewInteractionListener {
        // TODO: Update argument type and name
        void onItemClick(HealthDroidUserWrapper user);

        void onSubscribeClick(HealthDroidUserWrapper user, boolean alreadySubscribed);
    }

    private class UnsubscribeTask extends AsyncTask<HealthDroidUser, Void, Void> {
        @Override
        protected Void doInBackground(HealthDroidUser... params) {
            Subscription subscriptionService = SubscriptionFactory.getInstance();
            HealthDroidUser targetUser = params[0];
            MainActivity mainActivity = (MainActivity) getActivity();
            String user = mainActivity.getCredential().getSelectedAccountName();
            try {
                subscriptionService.unsubscribe(user).setTarget(targetUser.getEmail()).execute();
                notifyUnsubscribed(targetUser.getEmail());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class SubscribeTask extends AsyncTask<HealthDroidUser, Void, SubscriptionRecord> {
        @Override
        protected SubscriptionRecord doInBackground(HealthDroidUser... params) {
            Subscription subscriptionService = SubscriptionFactory.getInstance();
            SubscriptionRecord subscriptionRecord = null;
            HealthDroidUser targetUser = params[0];
            try {
                subscriptionRecord = subscriptionService.subscribe(targetUser.getEmail()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return subscriptionRecord;
        }

        @Override
        protected void onPostExecute(SubscriptionRecord subscriptionRecord) {
            super.onPostExecute(subscriptionRecord);
            notifySubscribed(subscriptionRecord.getTarget());
            notifyChange();
        }
    }

    private void notifyUnsubscribed(String email) {
        for (HealthDroidUserWrapper healthDroidUserWrapper : listUser) {
            if (healthDroidUserWrapper.healthDroidUser.getEmail().equalsIgnoreCase(email)) {
                Log.d(TAG, "Disabling a subscribe button for user " + email);
                healthDroidUserWrapper.subscribed = false;
            }
        }

        //TODO: there is duplicated code, consider refactor
        SharedPreferences dataPreferences =
                getActivity().getSharedPreferences("DATA_PREFERENCES", Context.MODE_PRIVATE);
        Set<String> subscribedUsers = dataPreferences.getStringSet(
                GetDataRecordsFromEndpointTask.SUBSCRIBED_USERS_KEY,
                new TreeSet<String>()
        );
        subscribedUsers.remove(email);
        SharedPreferences.Editor editor = dataPreferences.edit();
        editor.putStringSet(
                GetDataRecordsFromEndpointTask.SUBSCRIBED_USERS_KEY,
                subscribedUsers
        );
        editor.apply();
        Log.d(TAG, "Total subscriptions: " + subscribedUsers.size());
    }

    private void notifyUnsubscribed(com.ntu.phongnt.healthdroid.data.subscription.model.HealthDroidUser user) {
        notifyUnsubscribed(user.getEmail());
    }

    private void notifySubscribed(com.ntu.phongnt.healthdroid.data.subscription.model.HealthDroidUser user) {
        String email = user.getEmail();
        Set<String> subscribedUsers = new TreeSet<>();
        for (HealthDroidUserWrapper healthDroidUserWrapper : listUser) {
            if (healthDroidUserWrapper.healthDroidUser.getEmail().equalsIgnoreCase(email)) {
                Log.d(TAG, "Disabling a subscribe button for user " + email);
                healthDroidUserWrapper.subscribed = true;
                subscribedUsers.add(healthDroidUserWrapper.healthDroidUser.getEmail());
            }
        }
        //TODO: there is duplicated code, consider refactor
        SharedPreferences dataPreferences =
                getActivity().getSharedPreferences("DATA_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = dataPreferences.edit();
        editor.putStringSet(
                GetDataRecordsFromEndpointTask.SUBSCRIBED_USERS_KEY,
                subscribedUsers
        );
        editor.apply();
        Log.d(TAG, "Total subscriptions: " + subscribedUsers.size());
    }

    private class GetSubscriptionsTask extends AsyncTask<Void, Void, List<com.ntu.phongnt.healthdroid.data.subscription.model.HealthDroidUser>> {
        @Override
        protected List<com.ntu.phongnt.healthdroid.data.subscription.model.HealthDroidUser> doInBackground(Void... params) {
            Subscription subscriptionService = SubscriptionFactory.getInstance();
            List<com.ntu.phongnt.healthdroid.data.subscription.model.HealthDroidUser> subscribedUsers = null;
            try {
                subscribedUsers = subscriptionService.subscribed().execute().getItems();
                Log.d(TAG, "Get subscription records count: " + subscribedUsers.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return subscribedUsers;
        }

        @Override
        protected void onPostExecute(List<com.ntu.phongnt.healthdroid.data.subscription.model.HealthDroidUser> healthDroidUsers) {
            super.onPostExecute(healthDroidUsers);
            for (com.ntu.phongnt.healthdroid.data.subscription.model.HealthDroidUser user : healthDroidUsers)
                notifySubscribed(user);
            notifyChange();

        }
    }

    public class HealthDroidUserWrapper {
        public HealthDroidUser healthDroidUser = null;
        public Boolean subscribed = false;

        public HealthDroidUserWrapper(HealthDroidUser healthDroidUser, Boolean subscribed) {
            this.healthDroidUser = healthDroidUser;
            this.subscribed = subscribed;
        }

        public HealthDroidUserWrapper(HealthDroidUser healthDroidUser) {
            this.healthDroidUser = healthDroidUser;
        }
    }

    private class ListUserTask extends AsyncTask<GoogleAccountCredential, Void, List<HealthDroidUserWrapper>> {
        @Override
        protected List<HealthDroidUserWrapper> doInBackground(GoogleAccountCredential... params) {
            User userService = UserFactory.getInstance();
            try {
                List<HealthDroidUser> healthDroidUsers = userService.get().execute().getItems();
                Log.d(TAG, "Received " + healthDroidUsers.size() + " users");
                List<HealthDroidUserWrapper> wrapperList = new ArrayList<HealthDroidUserWrapper>();
                for (HealthDroidUser user : healthDroidUsers) {
                    wrapperList.add(new HealthDroidUserWrapper(user));
                }
                return wrapperList;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<HealthDroidUserWrapper> resultedHealthDroidUsers) {
            super.onPostExecute(resultedHealthDroidUsers);
            if (resultedHealthDroidUsers != null)
                listUser.addAll(resultedHealthDroidUsers);
            notifyChange();
        }
    }
}
