package com.ntu.phongnt.healthdroid.subscribers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ntu.phongnt.healthdroid.R;

public class SubscriberRecordViewHolder extends RecyclerView.ViewHolder {
    public View itemView = null;
    public TextView userView = null;
    public Button actionButton = null;

    public SubscriberRecordViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.userView = (TextView) itemView.findViewById(R.id.user);
        this.actionButton = (Button) itemView.findViewById(R.id.actionButton);
    }
}
