package edu.wisc.ece.uiapp;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by Evan Yee on 11/2/2015.
 */
public class GeofenceIntentService extends IntentService{
    protected static final String TAG = "GeofenceIntentService";

    public GeofenceIntentService(){
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    protected void onHandleIntent(Intent intent){
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = event.getGeofenceTransition();
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            Toast.makeText(GeofenceIntentService.this, "Entered Geofence", Toast.LENGTH_SHORT).show();
        }
        else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            Toast.makeText(GeofenceIntentService.this, "Exited Geofence", Toast.LENGTH_SHORT).show();
        }
        return;
    }
}
