package edu.wisc.ece.uiapp;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

public class Bar {

    String name;
    String ssid;
    LatLng location;
    int id;
    int patrons;
    WeekInformation week;





    public Bar(String name) {
        this.name = name;

    }

    public String getName() {
        return this.name;
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
    public WeekDay fillDay(JSONObject day){
        WeekDay tmpday;
        try {
            tmpday = new WeekDay(day.getString("open"), day.getString("close"), day.getString("special"));
            return tmpday;
        }
        catch(Exception e){
        }
        return null;
    }
    public void setWeekly(JSONObject weekly_data){
        WeekInformation week = null;
        try {
            JSONObject monday = weekly_data.getJSONObject("monday");
            JSONObject tuesday = weekly_data.getJSONObject("tuesday");
            JSONObject wednesday = weekly_data.getJSONObject("wednesday");
            JSONObject thursday = weekly_data.getJSONObject("thursday");
            JSONObject friday = weekly_data.getJSONObject("friday");
            JSONObject saturday = weekly_data.getJSONObject("saturday");
            JSONObject sunday = weekly_data.getJSONObject("sunday");

            week = new WeekInformation(fillDay(sunday), fillDay(monday),
                    fillDay(tuesday), fillDay(wednesday), fillDay(thursday), fillDay(friday), fillDay(saturday));



        }
        catch (Exception e){}
        this.week = week;
    }

}


