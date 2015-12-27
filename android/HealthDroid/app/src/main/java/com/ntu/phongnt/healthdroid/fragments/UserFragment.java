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

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.data.user.User;
import com.ntu.phongnt.healthdroid.data.user.model.HealthDroidUser;
import com.ntu.phongnt.healthdroid.fragments.adapter.MyUserRecyclerViewAdapter;
import com.ntu.phongnt.healthdroid.util.UserUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "USER_FRAGMENT";
    // TODO: Customize parameters
    private RecyclerView recyclerView = null;
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private List<HealthDroidUser> listUser = new ArrayList<HealthDroidUser>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserFragment newInstance(int columnCount) {
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

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

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
            recyclerView.setAdapter(new MyUserRecyclerViewAdapter(listUser, mListener));
        }
        return view;
    }

    private void notifyChange() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(HealthDroidUser item);
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
