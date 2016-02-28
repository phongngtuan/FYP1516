package com.ntu.phongnt.healthdroid.subscription;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.user.UserContract;
import com.ntu.phongnt.healthdroid.graph.util.TitleUtil;
import com.ntu.phongnt.healthdroid.services.subscription.SubscriptionService;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment implements SubscriptionChangeListener {
    public static final String TAG = "USER_FRAG";
    public static final String TITLE = "Users";
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static HealthDroidDatabaseHelper db = null;
    // TODO: Customize parameters
    private RecyclerView recyclerView = null;
    private int mColumnCount = 1;

    private SubscriptionChangePublisher subscriptionChangePublisher = null;
    private List<UserWrapper> listUser = new ArrayList<>();

    public UserFragment() {
    }

    public static UserFragment newInstance(SubscriptionChangePublisher subscriptionChangePublisher) {
        UserFragment fragment = new UserFragment();
        fragment.subscriptionChangePublisher = subscriptionChangePublisher;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        if (db == null) {
            db = HealthDroidDatabaseHelper.getInstance(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptionChangePublisher.unregisterSubscriptionListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        subscriptionChangePublisher.registerSubscriptionListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_list, container, false);

        TitleUtil.setSupportActionBarTitle(getActivity(), TITLE);
        HealthDroidUserViewInteractionListener listener = new HealthDroidUserViewInteractionListener() {
            @Override
            public void onItemClick(UserWrapper user) {
                //TODO: implement
                Toast.makeText(getActivity(), user.email, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSubscribeClick(UserWrapper user) {
                switch (user.subscriptionState) {
                    case UserContract.UserEntry.UNSUBSCRIBED:
                        handleSubscribe(user.email);
                        break;
                    case UserContract.UserEntry.PENDING:
                        handleUnsubscribe(user.email);
                        break;
                    case UserContract.UserEntry.SUBSCRIBED:
                        handleUnsubscribe(user.email);
                        break;
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
            UserRecyclerViewAdapter viewAdapter = new UserRecyclerViewAdapter(listUser, listener);
            recyclerView.setAdapter(viewAdapter);

            new LoadCursorUserDbTask().execute();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
    }

    @Override
    public void subscriptionChanged() {
        Log.d(TAG, "Handling subscription changed");
        new LoadCursorUserDbTask().execute();
    }

    private void handleSubscribe(String user) {
        SubscriptionService.startSubscribeUser(getActivity(), user);
    }

    private void handleUnsubscribe(String user) {
        SubscriptionService.startUnsubscribeUser(getActivity(), user);
    }

    public interface HealthDroidUserViewInteractionListener {

        void onItemClick(UserWrapper user);

        void onSubscribeClick(UserWrapper user);
    }

    public static class UserWrapper {

        public String email = null;
        public int subscriptionState = 0;
        public String lastUpdated = null;

        public UserWrapper(String user, int subscriptionState, String lastUpdated) {
            this.email = user;
            this.subscriptionState = subscriptionState;
            this.lastUpdated = lastUpdated;
        }
    }

    abstract private class BaseUserDbTask<T> extends AsyncTask<T, Void, Cursor> {
        protected Cursor doQuery() {
            Cursor result =
                    db
                            .getReadableDatabase()
                            .query(UserContract.UserEntry.TABLE_NAME,
                                    new String[]{
                                            UserContract.UserEntry._ID,
                                            UserContract.UserEntry.COLUMN_NAME_EMAIL,
                                            UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS,
                                            UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED
                                    },
                                    null,
                                    null,
                                    null,
                                    null,
                                    UserContract.UserEntry.COLUMN_NAME_EMAIL);

            Log.i(TAG, String.valueOf(result.getCount()));
            return (result);
        }
    }

    private class LoadCursorUserDbTask extends BaseUserDbTask<Void> {

        @Override
        protected Cursor doInBackground(Void... params) {
            return (doQuery());
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            listUser.clear();
            if (cursor.moveToFirst()) {
                do {
                    String email = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_EMAIL));
                    int subscription = cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS));
                    String lastUpdated = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_LAST_UPDATED));
                    UserWrapper userWrapper = new UserWrapper(email, subscription, lastUpdated);
                    listUser.add(userWrapper);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

}
