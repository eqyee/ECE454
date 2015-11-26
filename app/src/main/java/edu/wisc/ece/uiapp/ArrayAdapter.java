package edu.wisc.ece.uiapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ArrayAdapter extends BaseExpandableListAdapter {

    private final String LIKEABLE_STRING = "Cheers!";

    private final ArrayList<Event> events;
    public LayoutInflater inflater;
    public Activity activity;

    public ArrayAdapter(Activity activity, ArrayList<Event> events) {
        this.activity = activity;
        this.events = events;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public Object getChild(int position, int childPosition) {
        return events.get(position).getNewsFeedItems().get(childPosition);
    }

    @Override
    public long getChildId(int position, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int position, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final int eventPosition = position;
        final String eventItem = (String) getChild(position, childPosition);

        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_child, null);
        }
        text = (TextView) convertView.findViewById(R.id.event_details_tv);
        text.setText(eventItem);

        final View view = convertView;
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) view.getContext();
                Event currEvent = events.get(eventPosition);
                int barId = currEvent.getBarId();
                NewsFeed.launchBarInfo(view.getContext(), barId, eventPosition);

                //ViewPager viewPager = (ViewPager) activity.findViewById(R.id.pager);
                //viewPager.setCurrentItem(1);
            }
        });
        setLikeableListeners(convertView);
        return convertView;
    }

    private void setLikeableListeners(View convertView) {
        ImageView likeImage = (ImageView) convertView.findViewById(R.id.likeable_image_child);
        final TextView likeText = (TextView) convertView.findViewById(R.id.likeable_tv_child);

        OnClickListener myListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(likeText.getText() == LIKEABLE_STRING) {
                    likeText.setText("1");
                }
                else {
                    likeText.setText(LIKEABLE_STRING);
                }
            }
        };

        likeImage.setOnClickListener(myListener);
        likeText.setOnClickListener(myListener);
    }



    @Override
    public int getChildrenCount(int position) {
        return events.get(position).getNewsFeedItems().size();
    }

    @Override
    public Object getGroup(int position) {
        return events.get(position);
    }

    @Override
    public int getGroupCount() {
        return events.size();
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
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }
        Event event = (Event) getGroup(position);
        TextView itemTextView = (TextView)convertView.findViewById(R.id.groupItem_tv);
        itemTextView.setText(event.getSubject());
        /*itemTextView.setOnClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

            }
        });*/
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