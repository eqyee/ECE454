package edu.wisc.ece.uiapp.BarTabs;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import edu.wisc.ece.uiapp.APICalls;
import edu.wisc.ece.uiapp.ArrayAdapter;
import edu.wisc.ece.uiapp.BarInfoActivity;
import edu.wisc.ece.uiapp.R;

public class EventFeed extends Fragment {
    private ExpandableListView listView;
    private ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_event_feed, container, false);
        listView = (ExpandableListView) rootView.findViewById(R.id.listView);
        adapter = new ArrayAdapter(getActivity(), false, APICalls.eventMap.get(BarInfoActivity.barId));
        listView.setAdapter(adapter);

        return rootView;
    }
}
