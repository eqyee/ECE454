package edu.wisc.ece.uiapp.BarTabs;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import edu.wisc.ece.uiapp.APICalls;
import edu.wisc.ece.uiapp.ArrayAdapter;
import edu.wisc.ece.uiapp.Bar;
import edu.wisc.ece.uiapp.BarInfoActivity;
import edu.wisc.ece.uiapp.MainActivity;
import edu.wisc.ece.uiapp.R;
import edu.wisc.ece.uiapp.WeekAdapter;


public class WeeklySpecials extends Fragment{
    private ExpandableListView listView;
    private WeekAdapter week_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_weekly_specials, container, false);

        listView = (ExpandableListView) rootView.findViewById(R.id.WeekView);
        Log.d("Week Specials", BarInfoActivity.currBar.toString());
        week_adapter = new WeekAdapter(getActivity(), BarInfoActivity.currBar.getWeekInfo());

        listView.setAdapter(week_adapter);
        return rootView;
    }
}
