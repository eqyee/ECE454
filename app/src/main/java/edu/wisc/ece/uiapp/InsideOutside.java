package edu.wisc.ece.uiapp;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Evan Yee on 11/2/2015.
 */
public class InsideOutside implements LocationListener {

    protected LocationManager locationManager;
    private double currentLatitude;
    private double currentLongitude;
    private double currentUncertainty;

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();
    private Timer locTimer;
    private TimerTask locTimerTask;

    public InsideOutside(Context context){
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        start();
        initializeLocTimer();
        initializeTimer();
    }

    public void getLocationUpdates(){
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, this);
        }catch (SecurityException se){}
    }

    public void stopLocationUpdates(){
        try{
            locationManager.removeUpdates(this);
        }catch (SecurityException se){}
    }

    public void start(){
        getLocationUpdates();
    }

    public void stop(){
        stopLocationUpdates();
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        //TODO: Timer error
        CurrentLocation.latitude = location.getLatitude();
        CurrentLocation.longitude = location.getLongitude();
        try {
            timer.cancel();
            timer.purge();
            timer = new Timer();
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            currentUncertainty = location.getAccuracy();

            checkInsideProbability();
            timer.schedule(timerTask, 180000);
        }
        catch(NullPointerException e){
            Log.e("Inside/Outside", e.getMessage());
        }
    }

    private void initializeTimer(){
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        currentUncertainty = 10000;
                        checkInsideProbability();
                    }
                });
            }
        };
    }

    private void initializeLocTimer(){
        locTimerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        getLocationUpdates();
                    }
                });
            }
        };
    }

    public void checkInsideProbability(){
        if(currentUncertainty < 60.0){
            MainActivity.inside = false;
        }
        else if(currentUncertainty > 9999){
            MainActivity.inside = true;
            try {
                locationManager.removeUpdates(this);
            } catch(SecurityException se){}
            locTimer.cancel();
            locTimer.purge();
            locTimer = new Timer();

            locTimer.schedule(locTimerTask, 180000);
        }
        else if(currentUncertainty > 300){
            MainActivity.inside = true;
            try {
                locationManager.removeUpdates(this);
            } catch(SecurityException se){}
            locTimer.cancel();
            locTimer.purge();
            locTimer = new Timer();
            locTimer.schedule(locTimerTask, 180000);        }
        else{
            MainActivity.inside = true;
        }
        Log.w("Inside/Outside", "" + MainActivity.inside);
    }

    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderDisabled(String provider) {}

}
