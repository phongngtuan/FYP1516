package com.ntu.phongnt.healthdroid.graph;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntu.phongnt.healthdroid.R;
import com.ntu.phongnt.healthdroid.graph.util.TitleUtil;

public class GraphTabsFragment extends Fragment {
    private static final String TAG = "GraphTabsFragment";
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TitleUtil.setSupportActionBarTitle(getActivity(), "Tabbed Graphs");
        View view = inflater.inflate(R.layout.graphs, container, false);
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(new GraphFragmentPagerAdapter(getChildFragmentManager()));
        Log.d(TAG, "onCreateView()");

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        tabLayout.invalidate();

        return view;
    }
}
