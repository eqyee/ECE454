package edu.wisc.ece.uiapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Evan Yee on 11/2/2015.
 */
public class InsideOutside extends Service implements LocationListener{

    protected LocationManager locationManager;
    private double currentUncertainty;

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();

    IBinder mBinder;

    @Override
    public void onCreate(){
        locationManager = (LocationManager)getApplication().getSystemService(Context.LOCATION_SERVICE);
        initializeTimer();
        timer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        getLocationUpdates();
        Log.e("Inside/Outside", "Starting inside/outside detection");
        return 1;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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
            timer.purge();
            currentUncertainty = location.getAccuracy();
            timer = new Timer();
            checkInsideProbability();
            initializeTimer();
            timer.schedule(timerTask, 180000);
        }
        catch(NullPointerException e){
            Log.e("Inside/Outside", e.getMessage());
        }
        catch(IllegalStateException e){
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
                        stopLocationUpdates();
                        checkInsideProbability();
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

        }
        else if(currentUncertainty > 300){
            MainActivity.inside = true;
            try {
                locationManager.removeUpdates(this);
            } catch(SecurityException se){}
      }
        else{
            MainActivity.inside = true;
        }
        Log.w("Inside/Outside", "" + MainActivity.inside + " : " + currentUncertainty);
    }

    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onDestroy(){
        Log.e("Inside/Outside", "Stopping inside/outside detection");
        stopLocationUpdates();
        super.onDestroy();
    }

}
