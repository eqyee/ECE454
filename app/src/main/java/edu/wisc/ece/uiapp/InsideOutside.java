package edu.wisc.ece.uiapp;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Evan Yee on 11/2/2015.
 */
public class InsideOutside extends Fragment  implements LocationListener {

    protected static final String TAG = "MainActivity";
    protected LocationManager locationManager;
    private GeofenceManager mGeofenceManager;
    private double currentLatitude;
    private double currentLongitude;
    private double currentUncertainty;
    private TextView latitude;
    private TextView longitude;
    private TextView uncertainty;
    private TextView insideorout;

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();
    private Timer locTimer;
    private TimerTask locTimerTask;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLocationUpdates();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_insideoutside, container, false);

        latitude = (TextView)rootView.findViewById(R.id.latitude);
        longitude = (TextView)rootView.findViewById(R.id.longitude);
        uncertainty = (TextView)rootView.findViewById(R.id.Uncertainty);
        insideorout = (TextView)rootView.findViewById(R.id.InsideOrOut);

        Button enableGeofence = (Button)rootView.findViewById((R.id.geofenceButton));
        enableGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableGeofences();
            }
        });
        Button disableGeofence = (Button)rootView.findViewById((R.id.disableGeofenceButton));
        disableGeofence.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                disableGeofences();
            }
        });
        mGeofenceManager = new GeofenceManager(getActivity());

        timer = new Timer();
        initializeTimer();
        timer.schedule(timerTask, 180000);

        locTimer = new Timer();
        initializeLocTimer();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGeofenceManager.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGeofenceManager.onStop();
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        timer.cancel();
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        currentUncertainty = location.getAccuracy();


        latitude.setText("Latitude: " + currentLatitude);
        longitude.setText("Longitude: " + currentLongitude);
        uncertainty.setText("Uncertainty: " + currentUncertainty);
        checkInsideProbability();
        timer.schedule(timerTask, 180000);
    }
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderDisabled(String provider) {}

    public void enableGeofences(){
        HashMap tmp = APICalls.barMap;
        Collection<Bar> barList = tmp.values();
        mGeofenceManager.addGeofences(barList);
        mGeofenceManager.enableGeofence();
    }
    public void disableGeofences(){
        mGeofenceManager.disableGeofence();
    }

    public void checkInsideProbability(){
        if(currentUncertainty < 30.0){
            insideorout.setText("You are probably outside (or by a window). Will use" +
                    "a function of distance from center as heuristic.");
        }
        else if(currentUncertainty < 100){
            insideorout.setText("You may be inside");
        }
        else if(currentUncertainty > 9999){
            insideorout.setText("Can't get GPS. Assuming you are inside");
            try {
                locationManager.removeUpdates(this);
            } catch(SecurityException se){}
            locTimer.schedule(locTimerTask, 180000);
        }
        else if(currentUncertainty > 300){
            try {
                locationManager.removeUpdates(this);
            } catch(SecurityException se){}
            locTimer.schedule(locTimerTask, 180000);
        }
        else{
            insideorout.setText("You are most likely inside");
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

    public void getLocationUpdates(){
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, this);
        }catch (SecurityException se){}
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
}
