package edu.wisc.ece.uiapp.BarTabs;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import edu.wisc.ece.uiapp.BarInfoActivity;
import edu.wisc.ece.uiapp.R;
import edu.wisc.ece.uiapp.WeekAdapter;

/**
 * Created by Sam on 12/8/2015.
 */
public class AboutTab extends Fragment {
        private ExpandableListView listView;
        private WeekAdapter Week_adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //TODO MAKE THE BAR DESCRIPTION HERE
            View rootView = inflater.inflate(R.layout.fragment_weekly_specials, container, false);

            listView = (ExpandableListView) rootView.findViewById(R.id.WeekView);
            Log.d("Week Specials", BarInfoActivity.currBar.toString());
            Week_adapter = new WeekAdapter(getActivity(), BarInfoActivity.currBar.week);

            listView.setAdapter(Week_adapter);
            return rootView;
        }
    }
