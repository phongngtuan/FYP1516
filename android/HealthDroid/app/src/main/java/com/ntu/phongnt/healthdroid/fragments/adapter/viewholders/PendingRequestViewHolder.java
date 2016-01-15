package com.ntu.phongnt.healthdroid.fragments.adapter.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ntu.phongnt.healthdroid.R;

public class PendingRequestViewHolder extends RecyclerView.ViewHolder {
    public View itemView = null;
    public TextView userView = null;
    public Button acceptButton = null;

    public PendingRequestViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.userView = (TextView) itemView.findViewById(R.id.user);
        this.acceptButton = (Button) itemView.findViewById(R.id.accept_btn);
    }
}
