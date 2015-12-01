package edu.wisc.ece.uiapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
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

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //TODO - NOT SURE WHATS GOING ON HERE-------------------------------------------------------
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Log.e("TEST", "Longitude " + longitude + ": Latitude " + latitude );
            MainActivity.myLocation = new CurrentLocation(latitude, longitude);
        }catch (Exception e){
            Log.e("Location Manager", "Some issue with LocationManager");
        }

        String lat = Double.toString(CurrentLocation.latitude);
        String lon = Double.toString(CurrentLocation.longitude);
        String rad = "10000000";
        //------------------------------------------------------------------------------------------
        String strs[ ] = new String [3];
        strs[0] = lat;
        strs[1] = lon;
        strs[2] = rad;
        new loadInitialEvents().execute(strs);
        APICalls.getBars(lat, lon, rad);

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
            String radius;
            try{
                radius = strs[2];
            }
            catch (NullPointerException e){
                radius = "1000";
            }
            if (radius == null){
                radius = "1000";
            }

            //Construct an HTTP POST
            HttpClient httpclient = new DefaultHttpClient();
            String command = ("http://flock-app-dev2.elasticbeanstalk.com/api/eventloc/?" +
                    "location=POINT(" + latitude + "%20" + longitude + ")&radius=" + radius);
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

}