package com.ntu.phongnt.healthdroid.subscription;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.data.DataContract;
import com.ntu.phongnt.healthdroid.db.data.DataHelper;
import com.ntu.phongnt.healthdroid.db.user.UserContract;
import com.ntu.phongnt.healthdroid.db.user.UserHelper;
import com.ntu.phongnt.healthdroid.graph.util.TitleUtil;
import com.ntu.phongnt.healthdroid.services.data.GetDataRecordsFromEndpointTask;
import com.ntu.phongnt.healthdroid.services.subscription.SubscriptionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class UserFragment extends Fragment implements SubscriptionChangeListener {
    private static UserHelper db = null;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String TAG = "USER_FRAG";
    public static final String TITLE = "Users";
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

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        if (db == null) {
            db = UserHelper.getInstance(getActivity());
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
                Toast.makeText(getActivity(), user.getEmail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSubscribeClick(UserWrapper user) {
                switch (user.subscriptionState) {
                    case UserContract.UserEntry.UNSUBSCRIBED:
                        handleSubscribe(user.getEmail());
                        break;
                    case UserContract.UserEntry.PENDING:
                        handleUnsubscribe(user.getEmail());
                        break;
                    case UserContract.UserEntry.SUBSCRIBED:
                        handleUnsubscribe(user.getEmail());
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
    public void subscriptionChanged() {
        Log.d(TAG, "Handling subscription changed");
        new LoadCursorUserDbTask().execute();
    }

    private void handleSubscribe(String user) {
        SubscriptionService.startAddSubscribedUser(getActivity(), user);
    }

    private void handleUnsubscribe(String user) {
        SubscriptionService.startRemoveSubscribedUser(getActivity(), user);
    }

    private void notifyChange() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public interface HealthDroidUserViewInteractionListener {

        void onItemClick(UserWrapper user);

        void onSubscribeClick(UserWrapper user);
    }

    private void notifySubscriptionSent(String email) {
        for (UserWrapper userWrapper : listUser) {
            if (userWrapper.getEmail().equalsIgnoreCase(email)) {
                userWrapper.onSubscriptionSent();
            }
        }
    }

    private void notifyUnsubscribed(String email) {
        //TODO: refactor the flow to do broadcast intent
        for (UserWrapper userWrapper : listUser) {
            if (userWrapper.getEmail().equalsIgnoreCase(email)) {
                //Delete from database
                Log.d(TAG, "Getting data for email: " + email);
                SQLiteOpenHelper db = DataHelper.getInstance(getActivity());
                SQLiteDatabase writableDatabase = db.getWritableDatabase();

                writableDatabase.delete(DataContract.DataEntry.TABLE_NAME,
                        DataContract.DataEntry.COLUMN_NAME_USER + "=?",
                        new String[]{email});

                userWrapper.onSubscriptionCancelled();
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

    private void notifySubscriptionConfirmed(String email) {
        Log.d(TAG, "Notifying subscription confirm for email " + email);
        Set<String> subscribedUsers = new TreeSet<>();
        for (UserWrapper userWrapper : listUser) {
            if (userWrapper.getEmail().equalsIgnoreCase(email)) {
                Log.d(TAG, "Notifying subscribed email: " + userWrapper.getEmail());
                userWrapper.onSubscriptionConfirmed();
                subscribedUsers.add(userWrapper.getEmail());
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

    abstract private class BaseUserDbTask<T> extends AsyncTask<T, Void, Cursor> {
        protected Cursor doQuery() {
            Cursor result =
                    db
                            .getReadableDatabase()
                            .query(UserContract.UserEntry.TABLE_NAME,
                                    new String[]{
                                            UserContract.UserEntry._ID,
                                            UserContract.UserEntry.COLUMN_NAME_EMAIL,
                                            UserContract.UserEntry.COLUMN_NAME_SUBSCRIPTION_STATUS
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
                    UserWrapper userWrapper = new UserWrapper(email, subscription);
                    listUser.add(userWrapper);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public static class UserWrapper {

        public String email = null;
        public int subscriptionState = 0;

        public UserWrapper(String user, int subscriptionState) {
            this.email = user;
            this.subscriptionState = subscriptionState;
        }

        public UserWrapper(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getSubscriptionState() {
            return subscriptionState;
        }

        public void setSubscriptionState(int subscriptionState) {
            this.subscriptionState = subscriptionState;
        }

        public int onSubscriptionSent() {
            subscriptionState = UserContract.UserEntry.PENDING;
            return subscriptionState;
        }

        public int onSubscriptionConfirmed() {
            subscriptionState = UserContract.UserEntry.SUBSCRIBED;
            return subscriptionState;
        }

        public int onSubscriptionCancelled() {
            subscriptionState = UserContract.UserEntry.UNSUBSCRIBED;
            return subscriptionState;
        }
    }

}
