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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


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
    public static GeofenceManager mGeofenceManager;
    public static Context mContext;
    private boolean created;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        created = false;
        mGeofenceManager = new GeofenceManager(this);
        try{
            Log.e("Restore", "Restoring from bundle!");
            created = savedInstanceState.getBoolean("created");
            CurrentLocation.latitude = (double)savedInstanceState.getSerializable("latitude");
            CurrentLocation.longitude = (double)savedInstanceState.getSerializable("longitude");
            GeofenceIntentService.currentGeofenceId = savedInstanceState.getInt("geofence");
            APICalls.barMap = (HashMap<Integer, Bar>) savedInstanceState.getSerializable("barMap");
            APICalls.eventMap = (HashMap<Integer, ArrayList<Event>>) savedInstanceState.getSerializable("eventMap");
            MainActivity.events = (ArrayList<Event>) savedInstanceState.getSerializable("events");
            Log.e("Restore", "Restored!");

        }
        catch(Exception e){
            Log.e("Restore", e.toString());
        }
        if (created == false) {
            Collection<Bar> barList = APICalls.barMap.values();
            mGeofenceManager.onStart();
            mGeofenceManager.addGeofences(barList);
            mGeofenceManager.enableGeofence();
            created = true;
        }
//        if (APICalls.barMap.values() == null){
//            APICalls.getBars(Double.toString(CurrentLocation.longitude), Double.toString(CurrentLocation.latitude));
//            Log.e("WARNING", "EVENTS MIGHT BE MISSING");
//        }
//        else{
//            Collection<Bar> barList = APICalls.barMap.values();
//            mGeofenceManager.onStart();
//            mGeofenceManager.addGeofences(barList);
//            mGeofenceManager.enableGeofence();
//        }
        setContentView(R.layout.activity_main);



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

    @Override
    public void onDestroy(){
        if (GeofenceIntentService.currentGeofenceId != -1)
            APICalls.updatePopulation(1,0,GeofenceIntentService.currentGeofenceId);
        super.onDestroy();

    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("barMap", APICalls.barMap);
        outState.putSerializable("eventMap", APICalls.eventMap);
        outState.putBoolean("created", created);
        outState.putInt("geofence", GeofenceIntentService.currentGeofenceId);
        outState.putSerializable("events", MainActivity.events);
        outState.putSerializable("latitude", CurrentLocation.latitude);
        outState.putSerializable("longitude", CurrentLocation.longitude);

        Log.e("Parceable", "Put Parceables!");
    }

    public static Context getContext(){
        return mContext;
    }

}