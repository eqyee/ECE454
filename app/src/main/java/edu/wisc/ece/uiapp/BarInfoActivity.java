package edu.wisc.ece.uiapp;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class BarInfoActivity extends FragmentActivity implements ActionBar.TabListener {
    public static int barId;
    int eventInfo;

    private TextView barName_tv;
    private TextView barLocation_tv;

    Bar currBar;

    private ViewPager viewPager;
    private BarTabsPagerAdaper mAdapter;
    private ActionBar actionBar;

    // Tab titles
    private String[] tabs = { "Event Feed",  "Weekly Specials", "XXXXXX" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_info);

        /*Grab the bundle from the intent that started this activity. Used to get barID and EventID*/
        Bundle b = this.getIntent().getExtras();
        barId = Integer.parseInt(b.getString(NewsFeed.BAR_ID));
        if(b.getString(NewsFeed.EVENT_POSITION) != null){
            eventInfo = Integer.parseInt(b.getString(NewsFeed.EVENT_POSITION));
        }

        getViews();
        currBar = APICalls.barMap.get(barId);
        setViews();

        // Initilization
        mAdapter = new BarTabsPagerAdaper(getFragmentManager());
        setUpPager();
        setUpActionbar();
        actionBar.hide();
    }


    /*Gets a reference to each view on the page*/
    private void getViews() {
        barName_tv = (TextView) findViewById(R.id.bar_name_text);
        barLocation_tv = (TextView) findViewById(R.id.bar_location_tv);

    }

    /*Used to populate the views*/
    private void setViews() {
        barName_tv.setText(currBar.getName());
        //Probably need to do something with the getLocation
        barLocation_tv.setText(currBar.getLocation().toString());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

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

    private void setUpActionbar() {
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }
    }
}
