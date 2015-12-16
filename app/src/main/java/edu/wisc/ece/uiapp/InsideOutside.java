package edu.wisc.ece.uiapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Evan Yee on 11/2/2015.
 */
public class InsideOutside extends NonStopIntentService implements LocationListener{

    protected LocationManager locationManager;
    public static double []uncertainty = new double [7];
    private int uncertaintyPosition = 0;

    final Handler handler = new Handler();

    IBinder mBinder;

    public InsideOutside(){
        super("InsideOutside");

    }
    @Override
    public void onCreate(){
        super.onCreate();
        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onHandleIntent(Intent intent){
        uncertaintyPosition = 0;
        for(int i = 0; i < 7; i++){
            uncertainty[i] = 0;
        }
        getLocationUpdates();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                        for(int i = 0; i < 7; i++) {
                            uncertainty[i] = 10000.0;
                        }
                        stopLocationUpdates();
                        checkInsideProbability();
            }
        }, 180000);
        Log.e("Inside/Outside", "Starting inside/outside detection");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        // Don't let this service restart automatically if it has been stopped by the OS.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void getLocationUpdates(){
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
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
            handler.removeCallbacksAndMessages(null);
            uncertainty[uncertaintyPosition]= (double) location.getAccuracy();
            uncertaintyPosition = (uncertaintyPosition + 1) % 7;
            checkInsideProbability();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < 7; i++) {
                        uncertainty[i] = 10000.0;
                    }
                    stopLocationUpdates();
                    checkInsideProbability();
                }
            }, 180000);
            String message = "";
            for(Double curr : uncertainty){
                message += curr + " , ";
            }
            sendNotification(message);
        }
        catch(NullPointerException e){
            Log.e("Inside/Outside", e.getMessage());
        }
        catch(IllegalStateException e){
            Log.e("Inside/Outside", e.getMessage());
        }
    }

    public void checkInsideProbability(){
        int inside = 0;
        int insideCount = 0;
        for(Double curr : uncertainty){
            if(curr > 40.0 && curr < 9999) {
                insideCount++;
            }
            else if(curr >= 9999){
                inside = 2;
                break;
            }
            if(insideCount > 4){
                inside = 1;
                break;
            }
        }
        if(inside == 1){
            MainActivity.inside = true;
        }
        else if(inside == 2){
            MainActivity.inside = true;
            try {
                locationManager.removeUpdates(this);
            } catch(SecurityException se){}

        }
        else{
            MainActivity.inside = false;
        }

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
                .setContentTitle("Uncertainties")
                .setContentText(notificationDetails)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationDetails))
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
