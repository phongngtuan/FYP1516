package com.ntu.phongnt.healthdroid.graph;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.util.TitleUtil;

public class GraphTabsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TitleUtil.setSupportActionBarTitle(getActivity(), "Tabbed Graphs");
        View view = inflater.inflate(R.layout.graphs, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(new GraphFragmentPagerAdapter(getActivity().getSupportFragmentManager()));
        return view;
    }
}
