package edu.wisc.ece.uiapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.MapView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/*My thinking with this is to get the API call going right away. It will display a splash screen
* until the Events are initially loaded the first time. This is essential since the news feed wants
* events to be there in order to display the list. The bar call also gets started here, but is not
* essential for the app to start.*/

public class SplashScreen extends Activity implements LocationListener {
    protected LocationManager lm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                }catch (Exception ignored){

                }
            }
        }).start();
        try {

            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null || location.getLatitude() == 0.0 || location.getLongitude() == 0.0){
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location != null && location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                CurrentLocation.latitude = location.getLatitude();
                CurrentLocation.longitude = location.getLongitude();
                Log.e("GotLastKnown", "Got last known!");
                loadCalls();
            } else {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                Log.e("DidnttGetLast", "Nope last known!");
            }
            MediaPlayer mPlayer = new MediaPlayer();
                AssetFileDescriptor afd = getAssets().openFd("epic_meal_time.mp3");
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(afd.getFileDescriptor());
                mPlayer.prepare();
                mPlayer.start();
        }
        catch (SecurityException e){
            Log.e("LocationManager", "Location Manager Error in Splash");
        }catch (Exception e) {
        e.printStackTrace();
    }

        //TODO - NOT SURE WHATS GOING ON HERE------------------------------------------------------



    }
    public void loadCalls(){
        String lat = Double.toString(CurrentLocation.latitude);
        String lon = Double.toString(CurrentLocation.longitude);
        //------------------------------------------------------------------------------------------
        String strs[ ] = new String [3];
        Log.d("LAT", lat);
        Log.d("LON", lon);
        strs[0] = lat;
        strs[1] = lon;
        new loadInitialEvents().execute(strs);
    }

    protected class loadInitialEvents extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls
        }

        @Override
        protected JSONArray doInBackground(String... strs) {
            // Your code here
            String temp1 = "";
            String latitude = strs[0];
            String longitude = strs[1];


            //Construct an HTTP POST for getting bars
            HttpClient httpclient = new DefaultHttpClient();
            String command = (MainActivity.API_URL + "api/bars/?" +
                    "location=POINT(" + longitude + "%20" + latitude + ")&radius=" + MainActivity.RADIUS);
            Log.d("Bar_Command", command);
            HttpGet getVal = new HttpGet(command);
            try {
                HttpResponse response = httpclient.execute(getVal);
                temp1 = EntityUtils.toString(response.getEntity());
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("HTTP IO Exception.");
                e.printStackTrace();
            }
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(temp1);
            } catch (JSONException e) {
                System.out.println("Error in JSON decoding");
                e.printStackTrace();
                jsonArray = new JSONArray();
            }
            Log.d("Bars found", jsonArray.toString());
            APICalls.fillBars(jsonArray);

            //Construct an HTTP POST for getting events
            command = (MainActivity.API_URL + "api/eventloc/?" +
                    "location=POINT(" + longitude + "%20" + latitude + ")&radius=" + MainActivity.RADIUS);
            Log.d("Command", command);
            getVal = new HttpGet(command);
            try {
                HttpResponse response = httpclient.execute(getVal);
                temp1 = EntityUtils.toString(response.getEntity());
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("HTTP IO Exception.");
                e.printStackTrace();
            }
            try {
                jsonArray = new JSONArray(temp1);
                Log.d("JSONOUTPUT", jsonArray.toString(0));
            } catch (JSONException e) {
                System.out.println("Error in JSON decoding");
                e.printStackTrace();
                jsonArray = new JSONArray();
            }
            return jsonArray;
        }
        @Override
        protected void onPostExecute(JSONArray res) {
            APICalls.fillEvents(res);
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);
            finish();

        }
    }
        @Override
        public void onLocationChanged(Location location) {
            CurrentLocation.longitude = location.getLongitude();
            CurrentLocation.latitude = location.getLatitude();
            loadCalls();

        }
    @Override
    public void onProviderEnabled(String provider){}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    protected void onPause() {
        super.onPause();
        try {
            lm.removeUpdates(this);
        }
        catch(SecurityException e){
            Log.e("SecurityExceptionPauseSplash", "Yeah");
        }

        Log.d("onPause", "onPause, done");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
           // mPlayer.stop();
            lm.removeUpdates(this);
        }
        catch(SecurityException e){
            Log.e("SecurityExceptionPauseSplash", "Yeah");
        }

        Log.d("onDestroy", "onDestroy, done");
    }
}