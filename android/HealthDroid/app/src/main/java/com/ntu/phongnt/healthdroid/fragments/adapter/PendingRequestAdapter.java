package com.ntu.phongnt.healthdroid.fragments.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.fragments.adapter.viewholders.PendingRequestViewHolder;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestViewHolder> {
    @Override
    public PendingRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_request_row, parent, false);
        return new PendingRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PendingRequestViewHolder holder, int position) {
        holder.userView.setText("My funny thing " + position);
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
