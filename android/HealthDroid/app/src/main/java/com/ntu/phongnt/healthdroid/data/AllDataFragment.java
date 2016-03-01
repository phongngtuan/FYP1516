package com.ntu.phongnt.healthdroid.data;

import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;
import com.ntu.phongnt.healthdroid.db.data.DataContract;

import java.util.List;

public class AllDataFragment extends DataListFragment {
    @Override
    List<DataContract.DataEntry> getDataList() {
        HealthDroidDatabaseHelper db =
                HealthDroidDatabaseHelper.getInstance(getActivity().getApplicationContext());
        DataContract dataContract = new DataContract(db);
        return dataContract.getAllData();
    }

    @Override
    String getLabel() {
        return "All";
    }
}
