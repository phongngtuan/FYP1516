package com.ntu.phongnt.healthdroid.graph;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class GraphFragmentPagerAdapter extends FragmentPagerAdapter {
    public GraphFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new DemoFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
