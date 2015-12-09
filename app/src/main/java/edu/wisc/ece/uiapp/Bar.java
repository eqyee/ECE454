package edu.wisc.ece.uiapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

public class Bar implements Parcelable {

    String name;
    String ssid;
    LatLng location;
    int id;
    int patrons;
    WeekInformation week;



    String address;
    String state;
    int zipcode;
    String phone;
    String city;

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

    public int getZipcode(){return this.zipcode;}
    public void setZipcode(int zipcode){ this.zipcode = zipcode ;}

    public String getAddress(){return this.address;}
    public void setAddress(String address){ this.address = address ;}

    public String getState(){return this.state;}
    public void setState(String state){ this.state = state ;}

    public String getCity(){return this.city;}
    public void setCity(String city){ this.city = city ;}

    public String getPhone(){return this.phone;}
    public void setPhone(String phone){ this.phone = phone ;}

    public void setLocation(LatLng location){this.location = location;}
    public LatLng getLocation(){ return this.location;}
    public String getFullAddress(){
        String fullAddress = "";
        fullAddress = getAddress() + ", " + getCity() + ", " + getState() + ", " + getZipcode();
        return fullAddress;
    };
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
    public WeekInformation getWeekInfo(){return this.week;}


    protected Bar(Parcel in) {
        name = in.readString();
        ssid = in.readString();
        location = (LatLng) in.readValue(LatLng.class.getClassLoader());
        id = in.readInt();
        patrons = in.readInt();
        week = (WeekInformation) in.readValue(WeekInformation.class.getClassLoader());
        address = in.readString();
        state = in.readString();
        zipcode = in.readInt();
        phone = in.readString();
        city = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(ssid);
        dest.writeValue(location);
        dest.writeInt(id);
        dest.writeInt(patrons);
        dest.writeValue(week);
        dest.writeString(address);
        dest.writeString(state);
        dest.writeInt(zipcode);
        dest.writeString(phone);
        dest.writeString(city);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Bar> CREATOR = new Parcelable.Creator<Bar>() {
        @Override
        public Bar createFromParcel(Parcel in) {
            return new Bar(in);
        }

        @Override
        public Bar[] newArray(int size) {
            return new Bar[size];
        }
    };
}