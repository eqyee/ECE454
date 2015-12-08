package edu.wisc.ece.uiapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import edu.wisc.ece.uiapp.BarTabs.EventFeed;
import edu.wisc.ece.uiapp.BarTabs.WeeklySpecials;

public class BarTabsPagerAdaper extends FragmentPagerAdapter {

    private int numTabs = 3;
    public BarTabsPagerAdaper(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Profile fragment activity
                return new EventFeed();
            case 1:
                // News Feed fragment activity
                return new WeeklySpecials();
            case 2:
                // Heat Map fragment activity
                return new EventFeed();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return numTabs;
    }

    @Override
    public int getItemPosition(Object obj) {
        /*Need to check if we are on NewsFeed tab*/
        //NewsFeed.adapter.notifyDataSetChanged();
        return super.getItemPosition(obj);
    }
}