package com.ntu.phongnt.healthdroid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntu.phongnt.healthdroid.R;

/**
 * Created by phongnt on 10/20/15.
 */
public class GraphFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.graph_main, container, false);
    }
}
