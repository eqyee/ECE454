package edu.wisc.ece.uiapp;

import com.google.android.gms.maps.model.LatLng;
public class Bar {

    String name;
    String ssid;
    LatLng location;
    int id;
    int patrons;


    public Bar(String name) {
        this.name = name;

    }
    public int getId(){return this.id;}
    public void setId(int id){ this.id = id ;}
    public int getPatrons(){return this.patrons;}
    public void setPatrons(int patrons){ this.patrons = patrons ;}
    public void setLocation(LatLng location){this.location = location;}
    public LatLng getLocation(){ return this.location;}
    public void setSSID(String ssid) {
        this.ssid = ssid;
    }


}
