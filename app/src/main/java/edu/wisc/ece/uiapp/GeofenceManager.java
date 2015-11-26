package edu.wisc.ece.uiapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Evan Yee on 11/2/2015.
 */
public class GeofenceManager implements ResultCallback<Status>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    protected static final String TAG = "GeofenceManager";
    private static GoogleApiClient mGoogleApiClient;
    private List<Geofence> mGeofenceList;
    private Context applicationContext;
    private PendingIntent mGeofencePendingIntent;

    public GeofenceManager(Context context){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGeofencePendingIntent = null;
        mGoogleApiClient.connect();
        this.applicationContext = context;
    }

    public void addGeofences(Collection<Bar> bars){
        mGeofenceList = new ArrayList<>();
        for(Bar  b:  bars){
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId("" + b.name)
                            //set static radius to be 100
                    .setCircularRegion(b.getLocation().latitude, b.getLocation().longitude, 100.0f)
                    .setExpirationDuration(100000000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }
    public void enableGeofence(){
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(applicationContext,"GoogleAPIClient not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofenceRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            Log.e(TAG, "Invalid location permission. " +
                    "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
        }
    }
    public void disableGeofence(){
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(applicationContext,"GoogleAPIClient not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            Log.e(TAG, "Invalid location permission. " +
                    "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
        }
    }
    private GeofencingRequest getGeofenceRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        builder.addGeofences(mGeofenceList);
        return builder.build();
    }
    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(applicationContext, GeofenceIntentService.class);
        return PendingIntent.getService(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public void onResult(Status status){
        if(status.isSuccess()){
            Toast.makeText(applicationContext, "Geofence Added Or Maybe Removed. How Should I Know.", Toast.LENGTH_SHORT).show();
        }
        else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = "Error: Could not add geofence";
            Log.e(TAG, errorMessage);
        }
    }

    public void onStart(){
        if(mGoogleApiClient.isConnected()){
            Log.i(TAG, "Already Connected");
        } else {
            Log.i(TAG, "Connecting Client");
            mGoogleApiClient.connect();
        }
    }

    public void onStop() {
        Log.i(TAG, "Disconnecting Client");
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint){
        Log.i(TAG, "Connected to GoogleApiClient");
    }
    @Override
    public void onConnectionSuspended(int cause){
        Log.i(TAG, "Connection suspended");
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
}