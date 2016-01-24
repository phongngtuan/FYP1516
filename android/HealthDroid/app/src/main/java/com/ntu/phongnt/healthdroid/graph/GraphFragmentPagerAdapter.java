package com.ntu.phongnt.healthdroid.graph;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ntu.phongnt.healthdroid.fragments.GraphFragment;

public class GraphFragmentPagerAdapter extends FragmentPagerAdapter {
    public GraphFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //TODO: change appropriately
        return "Graph " + position;
    }

    @Override
    public Fragment getItem(int position) {
        return new GraphFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
