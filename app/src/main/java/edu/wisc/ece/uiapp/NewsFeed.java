package edu.wisc.ece.uiapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class NewsFeed extends Fragment {

    public static final String EVENT_POSITION = "EventId";
    public static final String BAR_ID = "BarId";

    static ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);

        final ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.listView);
        adapter = new ArrayAdapter(getActivity(), MainActivity.events);
        listView.setAdapter(adapter);

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            // Keep track of previous expanded parent
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                // Collapse previous parent if expanded.
                if ((previousGroup != -1) && (groupPosition != previousGroup)) {
                    listView.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });

        return rootView;
    }

    public static void eventsListDataHasChanged() {
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public static void launchBarInfo(Context context, int barId, int eventPosition) {
        Log.d("NewsFeed", "Child Item Clicked");
        Activity activity = (Activity) context;
        Intent i = new Intent(activity, BarInfoActivity.class);

        i.putExtra(BAR_ID, Integer.toString(barId));
        i.putExtra(EVENT_POSITION, Integer.toString(eventPosition));

        activity.startActivity(i);

        //ViewPager viewPager = (ViewPager) activity.findViewById(R.id.pager);
        //viewPager.setCurrentItem(1);
    }
}
