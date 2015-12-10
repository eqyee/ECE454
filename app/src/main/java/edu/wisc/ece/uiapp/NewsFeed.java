package edu.wisc.ece.uiapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;

public class NewsFeed extends Fragment {

    public static final String EVENT_POSITION = "EventId";
    public static final String BAR_ID = "BarId";

    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static ArrayAdapter adapter;

    private ExpandableListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);

        listView = (ExpandableListView) rootView.findViewById(R.id.listView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.newsfeed_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        adapter = new ArrayAdapter(getActivity(), MainActivity.events);
        listView.setAdapter(adapter);
        final View rV = rootView;
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            // Keep track of previous expanded parent
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                // Collapse previous parent if expanded.
                if ((previousGroup != -1) && (groupPosition != previousGroup)) {
                    ExpandableListView listView = (ExpandableListView) rV.findViewById(R.id.listView);
                    listView.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });



        return rootView;
    }

    @Override
    public void onPause(){
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    public static void eventsListDataHasChanged(ArrayList<Event> changedList) {
        if(adapter != null) {
            adapter.notifyDataSetChanged();
            adapter.dataChanged(changedList);
        }
    }

    public static void launchBarInfo(Context context, int barId, int eventPosition) {
            if(APICalls.barMap.get(barId) == null){
                return;
            }
        Activity activity = (Activity) context;
        Intent i = new Intent(activity, BarInfoActivity.class);

        i.putExtra(BAR_ID, Integer.toString(barId));
        i.putExtra(EVENT_POSITION, Integer.toString(eventPosition));

        activity.startActivity(i);

        //ViewPager viewPager = (ViewPager) activity.findViewById(R.id.pager);
        //viewPager.setCurrentItem(1);
    }

    private void refreshContent() {
        MainActivity.events = new ArrayList<Event>();
        MainActivity.events.clear();
        adapter.notifyDataSetInvalidated();
        APICalls.getEvents(getActivity(), Double.toString(CurrentLocation.longitude), Double.toString(CurrentLocation.latitude), MainActivity.RADIUS);
        APICalls.getBars(Double.toString(CurrentLocation.longitude), Double.toString(CurrentLocation.latitude));
        adapter.notifyDataSetChanged();
        Log.d("LAT", Double.toString(CurrentLocation.latitude));
        Log.d("LON", Double.toString(CurrentLocation.longitude));
        //adapter.dataChanged(MainActivity.events);
        // APICalls.getEvents(getActivity(), Double.toString(MainActivity.myLocation.latitude), Double.toString(MainActivity.myLocation.longitude), radius);
    }
}
