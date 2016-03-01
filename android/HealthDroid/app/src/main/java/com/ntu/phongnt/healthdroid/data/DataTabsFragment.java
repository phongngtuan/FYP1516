package com.ntu.phongnt.healthdroid.data;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.graph.util.TitleUtil;

public class DataTabsFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TitleUtil.setSupportActionBarTitle(getActivity(), "Data");
        View view = inflater.inflate(R.layout.data_view, container, false);
        //ViewPager setup
        viewPager = (ViewPager) view.findViewById(R.id.dataViewPager);
        viewPager.setAdapter(new DataFragmentPagerAdapter(getChildFragmentManager()));
        //Tab Layout
        tabLayout = (TabLayout) view.findViewById(R.id.dataTabLayout);
        tabLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        tabLayout.setupWithViewPager(viewPager);
                        tabLayout.invalidate();
                    }
                }
        );
        return view;
    }
}
