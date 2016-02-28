package com.ntu.phongnt.healthdroid.subscribers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.ntu.phongnt.healthdroid.MainActivity;
import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.graph.util.TitleUtil;
import com.ntu.phongnt.healthdroid.services.subscription.SubscriberRecord;
import com.ntu.phongnt.healthdroid.services.subscription.SubscriptionService;

import java.util.ArrayList;
import java.util.List;

public class SubscriberRecordFragment extends Fragment implements
        SubscriberRecordAdapter.SubscriberRecordInteractionListener,
        SubscriberRecordChangeListener {
    public static final String TAG = "PENDING_REQUEST_FRAG";
    public static final String TITLE = "Pending requests";

    private SubscriberRecordChangePublisher changePublisher = null;
    private RecyclerView recyclerView = null;
    private SubscriberRecordAdapter subscriberRecordAdapter = null;
    private List<SubscriberRecord> subscriberRecords = new ArrayList<>();

    public static SubscriberRecordFragment getInstance(SubscriberRecordChangePublisher publisher) {
        SubscriberRecordFragment pendingRequestFragment = new SubscriberRecordFragment();
        pendingRequestFragment.setChangePublisher(publisher);
        return pendingRequestFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_request_list, container, false);

        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
            ((RecyclerView) view).setLayoutManager(new LinearLayoutManager(view.getContext()));
            subscriberRecordAdapter = new SubscriberRecordAdapter();
            subscriberRecordAdapter.setListener(this);
            subscriberRecordAdapter.setSubscriberRecords(subscriberRecords);
            recyclerView.setAdapter(subscriberRecordAdapter);
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
        SubscriptionService.startLoadSubscriberRecords(getActivity(), credential.getSelectedAccountName());
    }

    @Override
    public void onResume() {
        super.onResume();
        getChangePublisher().registerSubscriberRecordListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getChangePublisher().unregisterSubscriberRecordListener(this);
    }

    @Override
    public void pendingRequestAccepted(Long subscriptionId) {
        removePendingRequest(subscriptionId);
    }

    @Override
    public void subscriberRecordLoaded(List<SubscriberRecord> loadedSubscriberRecords) {
        subscriberRecords.clear();
        subscriberRecords.addAll(loadedSubscriberRecords);
        subscriberRecordAdapter.notifyDataSetChanged();
    }

    private void removePendingRequest(Long subscriptionId) {
        for (SubscriberRecord request : subscriberRecords) {
            if (request.getId().equals(subscriptionId))
                subscriberRecords.remove(request);
        }
        subscriberRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestAccepted(SubscriberRecord subscriptionRecord) {
        Toast.makeText(getActivity(), "Accepted req by " + subscriptionRecord.getSubscriber(), Toast.LENGTH_SHORT).show();
        SubscriptionService.startAcceptRequest(getActivity(), subscriptionRecord.getSubscriber(), subscriptionRecord.getId());
    }

    public SubscriberRecordChangePublisher getChangePublisher() {
        return changePublisher;
    }

    public void setChangePublisher(SubscriberRecordChangePublisher changePublisher) {
        this.changePublisher = changePublisher;
    }

}
