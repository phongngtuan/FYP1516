package com.ntu.phongnt.healthdroid.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ntu.phongnt.healthdroid.R;

public class DataViewHolder extends RecyclerView.ViewHolder {

    public TextView valueView;
    public TextView dateView;

    public DataViewHolder(View itemView) {
        super(itemView);
        valueView = (TextView) itemView.findViewById(R.id.value);
        dateView = (TextView) itemView.findViewById(R.id.date);
    }
}
