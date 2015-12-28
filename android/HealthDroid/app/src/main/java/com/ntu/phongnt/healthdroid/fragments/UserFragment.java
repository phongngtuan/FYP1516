package com.ntu.phongnt.healthdroid.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.data.subscription.Subscription;
import com.ntu.phongnt.healthdroid.data.subscription.model.SubscriptionRecord;
import com.ntu.phongnt.healthdroid.data.user.User;
import com.ntu.phongnt.healthdroid.data.user.model.HealthDroidUser;
import com.ntu.phongnt.healthdroid.fragments.adapter.MyUserRecyclerViewAdapter;
import com.ntu.phongnt.healthdroid.util.SubscriptionUtil;
import com.ntu.phongnt.healthdroid.util.UserUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "USER_FRAGMENT";
    // TODO: Customize parameters
    private RecyclerView recyclerView = null;
    private int mColumnCount = 1;

    private List<HealthDroidUser> listUser = new ArrayList<HealthDroidUser>();
    private GoogleAccountCredential credential = null;
    private List<SubscriptionRecord> subscriptionRecords = null;

    public UserFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserFragment newInstance(int columnCount, GoogleAccountCredential credential) {
        //TODO: clean this up
        UserFragment fragment = new UserFragment();
        fragment.credential = credential;
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
            public void onItemClick(HealthDroidUser user) {
                Toast.makeText(getActivity(), user.getEmail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSubscribeClick(HealthDroidUser user) {
                subscribe(user);
                Toast.makeText(getActivity(), user.getEmail() + "subscribed", Toast.LENGTH_SHORT).show();
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

    private void notifyChange() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof HealthDroidUserViewInteractionListener) {
//            mListener = (HealthDroidUserViewInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement HealthDroidUserViewInteractionListener");
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        if (activity instanceof HealthDroidUserViewInteractionListener) {
//            mListener = (HealthDroidUserViewInteractionListener) activity;
//        } else {
//            throw new RuntimeException(activity.toString()
//                    + " must implement HealthDroidUserViewInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    public interface HealthDroidUserViewInteractionListener {
        // TODO: Update argument type and name
        void onItemClick(HealthDroidUser user);

        void onSubscribeClick(HealthDroidUser user);
    }

    private class SubscribeTask extends AsyncTask<HealthDroidUser, Void, SubscriptionRecord> {
        @Override
        protected SubscriptionRecord doInBackground(HealthDroidUser... params) {
            Subscription subscriptionService = SubscriptionUtil.getSubscriptionService(credential);
            SubscriptionRecord subscriptionRecord = null;
            HealthDroidUser targetUser = params[0];
            try {
                subscriptionRecord = subscriptionService.subscribe(targetUser.getEmail()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return subscriptionRecord;
        }
    }

    private class GetSubscriptionsTask extends AsyncTask<Void, Void, List<SubscriptionRecord>> {
        @Override
        protected List<SubscriptionRecord> doInBackground(Void... params) {
            Subscription subscriptionService = SubscriptionUtil.getSubscriptionService(credential);
            List<SubscriptionRecord> subscriptionRecords = null;
            try {
                //TODO: uncomment this to use get insteaad of list
//                subscriptionRecords = subscriptionService.get().execute().getItems();
                subscriptionRecords = subscriptionService.list().execute().getItems();
                Log.d(TAG, "Get subscription records count: " + subscriptionRecords.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return subscriptionRecords;
        }

        @Override
        protected void onPostExecute(List<SubscriptionRecord> resultedSubscriptionRecords) {
            super.onPostExecute(resultedSubscriptionRecords);
            subscriptionRecords = resultedSubscriptionRecords;
            Log.d(TAG, subscriptionRecords.toString());
        }
    }

    private class ListUserTask extends AsyncTask<GoogleAccountCredential, Void, List<HealthDroidUser>> {
        @Override
        protected List<HealthDroidUser> doInBackground(GoogleAccountCredential... params) {
            GoogleAccountCredential credential = params[0];
            User userService = UserUtil.getUserService(credential);
            try {
                List<HealthDroidUser> healthDroidUsers = userService.get().execute().getItems();
                Log.d(TAG, "Received " + healthDroidUsers.size() + " users");
                return healthDroidUsers;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<HealthDroidUser> healthDroidUsers) {
            super.onPostExecute(healthDroidUsers);
            if (healthDroidUsers != null)
                for (HealthDroidUser user : healthDroidUsers) {
                    listUser.add(user);
                }
            notifyChange();
        }
    }
}
