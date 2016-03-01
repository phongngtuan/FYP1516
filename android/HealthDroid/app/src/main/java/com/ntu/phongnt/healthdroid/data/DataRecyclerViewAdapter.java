package com.ntu.phongnt.healthdroid.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.data.DataContract;

import java.util.List;

public class DataRecyclerViewAdapter extends RecyclerView.Adapter<DataViewHolder> {

    List<DataContract.DataEntry> dataEntryList;

    public DataRecyclerViewAdapter(List<DataContract.DataEntry> dataEntryList) {
        this.dataEntryList = dataEntryList;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_row, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        holder.dateView.setText(dataEntryList.get(position).date);
        holder.valueView.setText(dataEntryList.get(position).value);
    }

    @Override
    public int getItemCount() {
        return dataEntryList.size();
    }
}
