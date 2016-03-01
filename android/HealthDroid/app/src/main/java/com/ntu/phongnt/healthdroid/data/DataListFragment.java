package com.ntu.phongnt.healthdroid.data;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.data.DataContract;

import java.util.List;

public class DataListFragment extends Fragment {
    RecyclerView recyclerView;
    DataRecyclerViewAdapter dataRecyclerViewAdapter;

    List<DataContract.DataEntry> dataEntryList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HealthDroidDatabaseHelper db =
                HealthDroidDatabaseHelper.getInstance(getActivity().getApplicationContext());
        DataContract dataContract = new DataContract(db);
        dataEntryList = dataContract.getAllData();
        dataRecyclerViewAdapter = new DataRecyclerViewAdapter(dataEntryList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.dataRecyclerView);
        recyclerView.setAdapter(dataRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    public String getType() {
        return "Data";
    }
}
