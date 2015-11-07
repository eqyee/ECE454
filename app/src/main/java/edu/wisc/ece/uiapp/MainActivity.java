package edu.wisc.ece.uiapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;


@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public static ArrayList<Event> events;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "News Feed",  "Profile", "Heat Map", "Inside/Outside" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        events = new ArrayList<Event>();

        fillEvents();
    }

    // Will need to do some sort of database call to fill the bars later.
    private void fillEvents() {
        String name = "Bar1";
        String name2 = "Bar2";
        Bar bar1 = new Bar(name);
        //bar1.addEvent(new Event("event1"));
        //bar1.addEvent(new Event("event2"));
        Bar bar2 = new Bar(name2);
        //bar2.addEvent(new Event("event3"));

        Event e1 = new Event("name1");
        e1.setSubject("My first event!");
        e1.setMessage("Here is some more info about the event.");
        e1.setStartTime("Starts now.");
        e1.setEndTime("Ends tomorrow.");
        e1.setBar(bar1);
        bar1.addEvent(e1);

        Event e2 = new Event("event number 2.");
        e2.setSubject("Just another event.");
        e2.setMessage("Some more info.");
        e2.setStartTime("Starts later today.");
        e2.setEndTime("Ends tonight");
        e2.setBar(bar2);
        bar2.addEvent(e2);





        events.add(e1);
        events.add(e2);
        events.add(e1);
        events.add(e2);
        events.add(e1);
        events.add(e2);
        events.add(e1);
        events.add(e2);
        events.add(e1);
        events.add(e2);
        events.add(e1);
        events.add(e2);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

}