package edu.wisc.ece.uiapp;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by Sam on 12/8/2015.
 */
public class WeekAdapter extends BaseExpandableListAdapter {
    private WeekInformation week;
    public Activity activity;
    public LayoutInflater inflater;
    private String[] DaysOfTheWeek = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};


    public WeekAdapter(Activity activity, WeekInformation week) {
        this.activity = activity;
        this.week = week;
        inflater = activity.getLayoutInflater();

    }

    @Override
    public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.week_listrow_group, null);
        }
        WeekDay day =  getWeekDayFrom(position, week);
        TextView Day = null;
        Day = (TextView) convertView.findViewById(R.id.Day);
        Day.setText(DaysOfTheWeek[position]);
        return convertView;
    }

    @Override
    public Object getChild(int position, int childPosition) {
        return getWeekDayFrom(position,week);
    }

    @Override
    public long getChildId(int position, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int position, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.week_listrow_child, null);
        }
        WeekDay day =  getWeekDayFrom(position, week);
        TextView time = null;
        TextView special = null;
        time = (TextView) convertView.findViewById(R.id.OpenHours);
        special = (TextView) convertView.findViewById(R.id.Special);

        try {
            if(day.open.isEmpty())
            {
                time.setText("CLOSED");
            }else {
                time.setText("Hours Of Operation: " + day.open + "-" + day.close);
            }
        }catch (NullPointerException npe){
            time.setText("Bar Hours Not Found");
        }
        try {
            special.setText(day.special);
        }catch(NullPointerException npe){
            special.setText("No Daily Specials Found");
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int position) {
        return 1;
    }

    @Override
    public Object getGroup(int position) {
        return getWeekDayFrom(position,week);
    }

    @Override
    public int getGroupCount() {
        return 7;
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
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public WeekDay getWeekDayFrom(int position, WeekInformation week){
        WeekDay day = null;
        Log.d("WEEK", ""+position);
            switch (position){
                case 0:
                    day = week.monday;
                    break;
                case 1:
                    day =  week.tuesday;
                    break;
                case 2:
                    day =  week.wednesday;
                    break;
                case 3:
                    day = week.thursday;
                    break;
                case 4:
                    day =  week.friday;
                    break;
                case 5:
                    day =  week.saturday;
                    break;
                case 6:
                    day =  week.sunday;
                    break;
            }
        return day;
    }
}
