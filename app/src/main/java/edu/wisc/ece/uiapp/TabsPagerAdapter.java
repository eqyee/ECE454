package edu.wisc.ece.uiapp;

/**
 * Created by CCS on 10/28/2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private int numTabs = 3;
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Profile fragment activity
                return new Profile();
            case 1:
                // News Feed fragment activity
                return new NewsFeed();
            case 2:
                // Heat Map fragment activity
                return new HeatMap();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return numTabs;
    }

}
