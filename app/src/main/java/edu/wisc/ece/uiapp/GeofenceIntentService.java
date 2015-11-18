package edu.wisc.ece.uiapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Evan Yee on 11/2/2015.
 */
public class GeofenceIntentService extends IntentService{
    protected static final String TAG = "GeofenceIntentService";
    private static Timer putTimer;
    private static TimerTask putTimerTask;
    private Handler handler = new Handler();

    public GeofenceIntentService(){
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        putTimerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //TODO: Put the API call here
                    }
                });
            }
        };
    }
    protected void onHandleIntent(Intent intent){
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = event.getGeofenceTransition();
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            if(putTimer == null){
                putTimer = new Timer();
                putTimer.schedule(putTimerTask, 120000);
            }
        }
        else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            if(putTimer != null){
                putTimer.cancel();
                putTimer = null;
            }
        }
        return;
    }
}
