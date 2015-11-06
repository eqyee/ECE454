package edu.wisc.ece.uiapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * Created by CCS on 10/28/2015.
 */
public class NewsFeed extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);

        ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.listView);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), MainActivity.bars);
        listView.setAdapter(adapter);

        return rootView;
    }
}
