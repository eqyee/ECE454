package edu.wisc.ece.uiapp;

import java.util.ArrayList;

public class Event {
    String name;
    String message;
    String start_time;
    String end_time;
    int favorites;
    //users_present
    //spec_tag
    String subject;

    ArrayList<String> newsFeedItems;

    Bar parentBar;

    public Event(String name) {
        this.name = name;
    }

    public void setBar(Bar parent) {
        this.parentBar = parent;
    }

    public Bar getBar() {
        return this.parentBar;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setStartTime(String sTime) {
        this.start_time = sTime;
    }

    public String getStartTime() {
        return this.start_time;
    }

    // public Time getStartTimeAsTime() {}

    public void setEndTime(String eTime) {
        this.end_time = eTime;
    }

    public String getEndTime() {
        return this.end_time;
    }

    // public Time getStartTimeAsTime() {}

    public ArrayList<String> getNewsFeedItems() {
        newsFeedItems = new ArrayList<String>();
        if(this.message != null) {
            newsFeedItems.add(this.message + "\nStarts: " + this.start_time + "\nEnds: " + this.end_time);
        }
        /*if(this.start_time != null) {
            newsFeedItems.add("Starts: " + this.start_time);
        }
        if(this.end_time != null) {
            newsFeedItems.add("Ends: " + this.end_time);
        }*/

        return newsFeedItems;
    }
}
