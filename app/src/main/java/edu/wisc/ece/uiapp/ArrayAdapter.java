package edu.wisc.ece.uiapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.util.ArrayList;

public class ArrayAdapter extends BaseExpandableListAdapter {

    private final ArrayList<Bar> bars;
    public LayoutInflater inflater;
    public Activity activity;

    public ArrayAdapter(Activity activity, ArrayList<Bar> bars) {
        this.activity = activity;
        this.bars = bars;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public Object getChild(int position, int childPosition) {
        return bars.get(position).events.get(childPosition);
    }

    @Override
    public long getChildId(int position, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int position, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Event event = (Event) getChild(position, childPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }
        text = (TextView) convertView.findViewById(R.id.bar_details_tv);
        text.setText(event.name);
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO From here we can launch a "view event info" tab if we want
                Toast.makeText(activity, event.name, Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int position) {
        return bars.get(position).events.size();
    }

    @Override
    public Object getGroup(int position) {
        return bars.get(position);
    }

    @Override
    public int getGroupCount() {
        return bars.size();
    }

    @Override
    public void onGroupCollapsed(int position) {
        super.onGroupCollapsed(position);
    }

    @Override
    public void onGroupExpanded(int position) {
        super.onGroupExpanded(position);
    }

    @Override
    public long getGroupId(int position) {
        return 0;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_bar, null);
        }
        Bar bar = (Bar) getGroup(position);
        ((CheckedTextView) convertView).setText(bar.name);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}