package edu.wisc.ece.uiapp;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Parcelable {
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

    protected Event(Parcel in) {
        name = in.readString();
        message = in.readString();
        start_time = in.readString();
        end_time = in.readString();
        favorites = in.readInt();
        barId = in.readInt();
        subject = in.readString();
        tags = in.createStringArray();
        if (in.readByte() == 0x01) {
            newsFeedItems = new ArrayList<String>();
            in.readList(newsFeedItems, String.class.getClassLoader());
        } else {
            newsFeedItems = null;
        }
        parentBar = (Bar) in.readValue(Bar.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(message);
        dest.writeString(start_time);
        dest.writeString(end_time);
        dest.writeInt(favorites);
        dest.writeInt(barId);
        dest.writeString(subject);
        dest.writeStringArray(tags);
        if (newsFeedItems == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(newsFeedItems);
        }
        dest.writeValue(parentBar);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}