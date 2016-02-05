package com.ntu.phongnt.healthdroid.request;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.services.subscription.PendingRequest;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestViewHolder> {
    private List<PendingRequest> pendingRequests = new ArrayList<>();
    private PendingRequestInteractionListener listener;

    public List<PendingRequest> getPendingRequests() {
        return pendingRequests;
    }

    public PendingRequestInteractionListener getListener() {
        return listener;
    }

    public void setListener(PendingRequestInteractionListener listener) {
        this.listener = listener;
    }

    public void setPendingRequests(List<PendingRequest> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    @Override
    public PendingRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_request_row, parent, false);
        return new PendingRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PendingRequestViewHolder holder, final int position) {
        String user = pendingRequests.get(position).getSubscriber();
        holder.userView.setText(user);

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRequestAccepted(pendingRequests.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingRequests.size();
    }


    public interface PendingRequestInteractionListener {
        public void onRequestAccepted(PendingRequest subscriptionRecord);
    }
}
