package com.ntu.phongnt.healthdroid.fragments.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.data.subscription.model.SubscriptionRecord;
import com.ntu.phongnt.healthdroid.fragments.adapter.viewholders.PendingRequestViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestViewHolder> {
    private List<SubscriptionRecord> pendingRequests = new ArrayList<>();

    public List<SubscriptionRecord> getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(List<SubscriptionRecord> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    @Override
    public PendingRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_request_row, parent, false);
        return new PendingRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PendingRequestViewHolder holder, int position) {
        String user = pendingRequests.get(position).getSubscriber().getEmail();
        holder.userView.setText(user);
    }

    @Override
    public int getItemCount() {
        return pendingRequests.size();
    }
}
