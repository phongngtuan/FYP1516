package com.ntu.phongnt.healthdroid.subscribers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.services.subscription.SubscriberRecord;

import java.util.ArrayList;
import java.util.List;

public class SubscriberRecordAdapter extends RecyclerView.Adapter<SubscriberRecordViewHolder> {
    private List<SubscriberRecord> subscriberRecords = new ArrayList<>();
    private SubscriberRecordInteractionListener listener;

    public List<SubscriberRecord> getSubscriberRecords() {
        return subscriberRecords;
    }

    public void setSubscriberRecords(List<SubscriberRecord> subscriberRecords) {
        this.subscriberRecords = subscriberRecords;
    }

    public SubscriberRecordInteractionListener getListener() {
        return listener;
    }

    public void setListener(SubscriberRecordInteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public SubscriberRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subscriber_record_row, parent, false);
        return new SubscriberRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SubscriberRecordViewHolder holder, int position) {
        String user = subscriberRecords.get(position).getSubscriber();
        holder.userView.setText(user);

        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int holderPosition = holder.getAdapterPosition();
                listener.onRequestAccepted(subscriberRecords.get(holderPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return subscriberRecords.size();
    }


    public interface SubscriberRecordInteractionListener {
        //TODO: should also handle remove
        void onRequestAccepted(SubscriberRecord subscriptionRecord);
    }
}
