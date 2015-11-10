package edu.wisc.ece.uiapp;

/**
 * Created by CCS on 11/9/2015.
 */
public class CurrentLocation {
    public static double latitude;
    public static double longitude;

    public CurrentLocation(double latitude, double longitude){
        CurrentLocation.latitude = latitude;
        CurrentLocation.longitude = longitude;
    }
}
