package com.ntu.phongnt.healthdroid.data;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DataFragmentPagerAdapter extends FragmentPagerAdapter {

    DataListFragment[] fragments = new DataListFragment[3];

    public DataFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments[0] = new AllDataFragment();
        fragments[1] = new BloodPressureDataFragment();
        fragments[2] = new AllDataFragment();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments[position].getLabel();
    }
}




