package com.projects.shrungbhatt.filetransfer.adpaters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fragments.Fragment_Received;
import fragments.Fragment_Sent;

public class Adapter_HistoryViewPager extends FragmentStatePagerAdapter {

    private int mTabCount;


    public Adapter_HistoryViewPager(FragmentManager fm, int tabCount) {
        super(fm);
        mTabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return new Fragment_Sent();
            case 1:
                return new Fragment_Received();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mTabCount;
    }
}
