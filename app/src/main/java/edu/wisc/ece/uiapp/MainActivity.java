package edu.wisc.ece.uiapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collection;


@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public static ArrayList<Event> events;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    // Tab titles
    private String[] tabs = { "News Feed",  "Heat Map", "Profile" };
    public static CurrentLocation myLocation;

    public static boolean inside = true;
    public static final String RADIUS = "3000";
    public static final String API_URL = "http://flock-app-dev2.elasticbeanstalk.com/";
    public static InsideOutside insideOutsideManager;
    public static GeofenceManager mGeofenceManager;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insideOutsideManager = new InsideOutside(this);
        mGeofenceManager = new GeofenceManager(this);
        Collection<Bar> barList = APICalls.barMap.values();
        mGeofenceManager.onStart();
        mGeofenceManager.addGeofences(barList);
        mGeofenceManager.enableGeofence();
        mContext = getApplicationContext();
        // Initilization
        mAdapter = new TabsPagerAdapter(getFragmentManager());
        setUpPager();
        setUpActionbar();

        //events = new ArrayList<Event>();

        //fillEvents();
        //no clue whats going on here?
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    private void setUpActionbar() {
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }
    }

    private void setUpPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mAdapter);

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                mAdapter.notifyDataSetChanged();
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                //here we will want to do the refresh events
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.
                getActionView(menu.findItem(R.id.search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    // Will need to do some sort of database call to fill the bars later.
   /* private void fillEvents() {
        String lat = Double.toString(CurrentLocation.latitude);
        String lon = Double.toString(CurrentLocation.longitude);
        String rad = "10000000";
        APICalls.getEvents(this, lat, lon, rad);
        APICalls.getBars(lat, lon, rad);
    }*/

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