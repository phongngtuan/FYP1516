package com.ntu.phongnt.healthdroid.graph;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class GraphFragmentPagerAdapter extends FragmentPagerAdapter {
    public static final int GRAPH_COUNT = 3;
    GraphFragment[] graphFragments = new GraphFragment[GRAPH_COUNT];

    public GraphFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        //TODO: do this according to graph subclass later
        graphFragments[0] = new GraphFragment("Type A");
        graphFragments[1] = new GraphFragment("Type B");
        graphFragments[2] = new GraphFragment("Type C");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //TODO: change appropriately
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
