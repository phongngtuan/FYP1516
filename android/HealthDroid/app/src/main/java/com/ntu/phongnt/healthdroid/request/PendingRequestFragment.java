package com.ntu.phongnt.healthdroid.request;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.ntu.phongnt.healthdroid.graph.util.TitleUtil;
import com.ntu.phongnt.healthdroid.services.SubscriptionFactory;
import com.ntu.phongnt.healthdroid.services.subscription.SubscriptionService;

import java.io.IOException;
import java.util.List;

public class PendingRequestFragment extends Fragment implements PendingRequestAdapter.PendingRequestInteractionListener {
    public static final String TAG = "PENDING_REQUEST_FRAG";
    public static final String TITLE = "Pending requests";
    private RecyclerView recyclerView = null;
    private PendingRequestAdapter pendingRequestAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_request_list, container, false);

        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
            ((RecyclerView) view).setLayoutManager(new LinearLayoutManager(view.getContext()));
            pendingRequestAdapter = new PendingRequestAdapter();
            pendingRequestAdapter.setListener(this);
            recyclerView.setAdapter(pendingRequestAdapter);
        }

        TitleUtil.setSupportActionBarTitle(getActivity(), TITLE);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleAccountCredential credential =
                ((MainActivity) getActivity()).getCredential();
        new GetPendingRequestTask(credential.getSelectedAccountName()).execute();
    }

    @Override
    public void onRequestAccepted(SubscriptionRecord subscriptionRecord) {
        Toast.makeText(getActivity(), "Accepted req by " + subscriptionRecord.getSubscriber().getEmail(), Toast.LENGTH_SHORT).show();
        SubscriptionService.startAcceptRequest(getActivity(), subscriptionRecord.getSubscriber().getEmail(), subscriptionRecord.getId());
    }

    private class GetPendingRequestTask extends AsyncTask<Void, Void, List<SubscriptionRecord>> {
        private String accountName;

        public GetPendingRequestTask(String accountName) {
            this.accountName = accountName;
        }

        @Override
        protected List<SubscriptionRecord> doInBackground(Void... params) {
            Subscription subscriptionService = SubscriptionFactory.getInstance();
            List<SubscriptionRecord> subscriptionRecords = null;
            try {
                subscriptionRecords = subscriptionService.pending(accountName).execute().getItems();
                Log.d(TAG, "Get subscription records count: " + subscriptionRecords.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return subscriptionRecords;
        }

        @Override
        protected void onPostExecute(List<SubscriptionRecord> subscriptionRecords) {
            pendingRequestAdapter.setPendingRequests(subscriptionRecords);
            pendingRequestAdapter.notifyDataSetChanged();
        }
    }
}
