package edu.wisc.ece.uiapp;

import java.util.ArrayList;

public class Bar {

    String name;
    ArrayList<Event> events;
    String ssid;

    public Bar(String name) {
        this.name = name;
        events = new ArrayList<>();

    }

    public void setSSID(String ssid) {
        this.ssid = ssid;
    }

    public void addEvent(Event e){
        events.add(e);
    }
}
