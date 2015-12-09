package edu.wisc.ece.uiapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;
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
    public static int currentGeofenceId = -1;
    Handler delayHandler = new Handler();
    public static int waitTime;
    private final int delay = 5000;
    private static final int BUFFSIZE = 15;
    private static int [] lineTime = new int [BUFFSIZE];
    private static int pointer = 0;

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
        final List<Geofence> triggeringGeofences = event.getTriggeringGeofences();
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER||
                geofenceTransition==Geofence.GEOFENCE_TRANSITION_EXIT ) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.

            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            sendNotification(geofenceTransitionDetails);
        }

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            if(putTimer == null){
                putTimer = new Timer();
                Log.d("Entering", triggeringGeofences.get(0).getRequestId());
                Log.d("Entering1GeofenceID", Integer.toString(GeofenceIntentService.currentGeofenceId));
                putTimerTask = new TimerTask() {
                    public void run() {
                        //use a handler to run a toast that shows the current timestamp
                        if(GeofenceIntentService.currentGeofenceId == -1) {
                            //TODO: Ask about this.
                            GeofenceIntentService.currentGeofenceId = Integer.parseInt(triggeringGeofences.get(0).getRequestId());
                            Log.d("Entering3GeofenceID", Integer.toString(GeofenceIntentService.currentGeofenceId));
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    Log.d("Entering2GeofenceID", Integer.toString(GeofenceIntentService.currentGeofenceId));
                                    //TODO: DECIDE WHEN INSIDE USING INSIDE/OUTSIDE
                                    if (MainActivity.inside) {
                                        APICalls.updatePopulation(0, getWaitTime(), Integer.parseInt(triggeringGeofences.get(0).getRequestId()));
                                        Log.d("Sending server call", Integer.toString(getWaitTime()));
                                    }
                                    else {
                                        Log.d("Wait time incrementing", Integer.toString(getWaitTime()));
                                        handler.postDelayed(this, 5000);
                                    }
                                }
                            }, 30000);
                        }
                    }
                };
                putTimer.schedule(putTimerTask, 10000);
            }
            AlgorithmsDet.LINESTORAGE = new AlgorithmsDet.LineObject[AlgorithmsDet.ARRAY_SIZE];
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlgorithmsDet.createLineObject();
                    waitTime = AlgorithmsDet.inLine();
                    pointer = pointer % BUFFSIZE;
                    lineTime[pointer] = waitTime;
                    pointer++;
                    delayHandler.postDelayed(this, delay);
                }
            }, delay);
           //TODO: FIX THIS
           startService(new Intent(getBaseContext(), InsideOutside.class));
        }
        else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            Log.d("Leaving!", Integer.toString(GeofenceIntentService.currentGeofenceId));
            if(putTimer != null){
                putTimer.cancel();
                putTimer = null;
            }
            delayHandler.removeCallbacksAndMessages(null);
            Log.d("LeavingGeofenceID", Integer.toString(GeofenceIntentService.currentGeofenceId));
            if(GeofenceIntentService.currentGeofenceId != -1) {
                APICalls.updatePopulation(1, 0, currentGeofenceId);
                GeofenceIntentService.currentGeofenceId = -1;
            }
            stopService(new Intent(getBaseContext(), InsideOutside.class));
        }
        return;
    }
    public int getWaitTime(){
        double sum = 0;
        double sumsq = 0;
        for (int i = 0; i< BUFFSIZE; i++){
            sum+=lineTime[i];
            sumsq+=lineTime[i]*lineTime[i];
        }
        double mean = sum/BUFFSIZE;
        double variance = sumsq/BUFFSIZE - mean*mean;
        double stdDeviation = Math.sqrt(variance);
        int total = 0;
        int value = 0;
        for (int i =0; i<BUFFSIZE; i++){
            //1 std deviation
            if(lineTime[i]>mean-1*stdDeviation && lineTime[i] < mean+1*stdDeviation){
                total++;
                value+=lineTime[i];
            }
        }
        if(total > 0) return (value/total * delay/1000);
        else return 0;

    }
    //The following was modified from google geofence sample
    // https://github.com/googlesamples/android-play-location/blob/master/Geofencing/app/src/main/java/com/google/android/gms/location/sample/geofencing/GeofenceTransitionsIntentService.java
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_enter);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exit);
            default:
                return getString(R.string.geofence_transition_unknown);
        }
    }
    //Just the copied notification
    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_action_likeable)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_action_likeable))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
}
