package com.ntu.phongnt.healthdroid.graph.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ntu.phongnt.healthdroid.graph.util.bloodpressure.BloodPressureFragment;
import com.ntu.phongnt.healthdroid.graph.util.simple.SimpleDataFragment;

public class GraphFragmentPagerAdapter extends FragmentPagerAdapter {
    public static final int GRAPH_COUNT = 3;
    GraphFragment[] graphFragments = new GraphFragment[GRAPH_COUNT];

    public GraphFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        //TODO: do this according to graph subclass later
        graphFragments[0] = new SimpleDataFragment();
        graphFragments[1] = new BloodPressureFragment();
        graphFragments[2] = new SimpleDataFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return graphFragments[position].getType();
    }

    @Override
    public Fragment getItem(int position) {
        return graphFragments[position];
    }

    @Override
    public int getCount() {
        return GRAPH_COUNT;
    }
}
