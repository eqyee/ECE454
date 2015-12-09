package edu.wisc.ece.uiapp;

import org.json.JSONArray;

import java.util.ArrayList;

public class Event {
    String name;
    String message;
    String start_time;
    String end_time;
    int favorites;
    int barId;
    String [] tags;
    //users_present
    //spec_tag
    String subject;

    ArrayList<String> newsFeedItems;

    Bar parentBar;

    public Event(String name) {
        this.name = name;
    }

//    public void setBar(Bar parent) {
//        this.parentBar = parent;
//    }
//
//    public Bar getBar() {
//        return this.parentBar;
//    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBarId(int id){this.barId = id;}
    public int getBarId(){return this.barId;}

    public String getSubject() {
        return this.subject;
    }

    public void setStartTime(String sTime) {
        this.start_time = sTime;
    }

    public String getStartTime() {
        return this.start_time;
    }

    public void setFavorites(int favorites){ this.favorites = favorites;}

    public int getFavorites(){ return this.favorites;}
    public String [] getTags(){
        return this.tags;
    }

    public void setTags(JSONArray tags) {

        String[] tagarray = new String[tags.length()];
        for (int i = 0; i < tags.length(); i++) {
            try {
                tagarray[i] = tags.getString(i);
            } catch (Exception e) {
            }
        }
        this.tags = tagarray;
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
            newsFeedItems.add(this.message);
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
