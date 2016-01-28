package com.ntu.phongnt.healthdroid.subscription;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.user.UserContract;

import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private final List<UserFragment.UserWrapper> healthDroidUsers;
    private final UserFragment.HealthDroidUserViewInteractionListener itemClickListener;

    public UserRecyclerViewAdapter(List<UserFragment.UserWrapper> items, UserFragment.HealthDroidUserViewInteractionListener listener) {
        healthDroidUsers = items;
        itemClickListener = listener;
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_row, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        holder.healthDroidUser = healthDroidUsers.get(position);
        holder.idView.setText(healthDroidUsers.get(position).getEmail());

        switch (holder.healthDroidUser.subscriptionState) {
            case UserContract.UserEntry.UNSUBSCRIBED:
                holder.subscribeButton.setText(R.string.subscribe_label);
                break;
            case UserContract.UserEntry.PENDING:
                holder.subscribeButton.setText(R.string.pending_label);
                break;
            case UserContract.UserEntry.SUBSCRIBED:
                holder.subscribeButton.setText(R.string.unsubscribe_label);
                break;
        }

//        listener for interaction on whole view
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != itemClickListener) {
                    itemClickListener.onItemClick(holder.healthDroidUser);
                }
            }
        });

        //listener for clicking on the button
        holder.subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != itemClickListener) {
                    itemClickListener.onSubscribeClick(holder.healthDroidUser);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return healthDroidUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView idView;
        public final Button subscribeButton;
        public UserFragment.UserWrapper healthDroidUser;

        public UserViewHolder(View view) {
            super(view);
            this.view = view;
            idView = (TextView) view.findViewById(R.id.id);
            subscribeButton = (Button) view.findViewById(R.id.subscribe_btn);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + idView.getText() + "'";
        }
    }
}
