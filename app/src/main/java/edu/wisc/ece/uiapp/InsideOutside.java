package edu.wisc.ece.uiapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan Yee on 11/2/2015.
 */
public class InsideOutside extends Fragment  implements LocationListener {

    protected static final String TAG = "MainActivity";
    protected LocationManager locationManager;
    private GeofenceManager mGeofenceManager;
    private double currentLatitude;
    private double currentLongitude;
    private TextView latitude;
    private TextView longitude;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
        }catch (SecurityException se){}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_insideoutside, container, false);

        latitude = (TextView)rootView.findViewById(R.id.latitude);
        longitude = (TextView)rootView.findViewById(R.id.longitude);
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
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        latitude.setText("Latitude: " + currentLatitude);
        longitude.setText("longitude: " + currentLongitude);
    }
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderDisabled(String provider) {}

    public void enableGeofences(){
        List<List<Double>> fences = new ArrayList<>();
        List<Double> currentLocation = new ArrayList<>();
        currentLocation.add(currentLatitude);
        currentLocation.add(currentLongitude);
        currentLocation.add(5.0);
        currentLocation.add(1.0);
        fences.add(currentLocation);
        mGeofenceManager.addGeofences(fences);
        mGeofenceManager.enableGeofence();
    }
    public void disableGeofences(){
        mGeofenceManager.disableGeofence();
    }

}
