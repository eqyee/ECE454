package edu.wisc.ece.uiapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CCS on 11/7/2015.
 */
public class APICalls {
    private static JSONArray jsonBars;
    public static HashMap<Integer,Bar> barMap;
    public static HashMap<Integer,ArrayList<Event>> eventMap;

    public static void getEvents(Activity activity, String latitude, String longitude, String radius){
        eventQuery hi = new eventQuery(activity);
        String strs[ ] = new String [3];
        strs[0] = latitude;
        strs[1] = longitude;
        strs[2] = radius;
        hi.execute(strs);
    }
    public static void updatePopulation(int enter, int wait, int bid){
        barPopulation bp = new barPopulation();
        String strs[ ] = new String [3];
        strs[0] = "" + enter;
        strs[1] = "" + bid;
        strs[2] = "" + wait;
        bp.execute(strs);
    }
    public static void fillEvents(JSONArray jsonArray) {
        MainActivity.events = new ArrayList<Event>();
        eventMap = new HashMap<Integer, ArrayList<Event>>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject tmp = jsonArray.getJSONObject(i);
                    JSONObject own = tmp.getJSONObject("owner");
                    Event e = new Event(own.getString("bar_name"));
                    e.setBarId(own.getInt("id"));
                    e.setMessage(tmp.getString("message"));
                    e.setSubject(tmp.getString("subject"));
                    e.setStartTime(tmp.getString("start_time"));
                    e.setEndTime(tmp.getString("end_time"));
                    e.setFavorites(tmp.getInt("favorites"));
                    MainActivity.events.add(e);
                    ArrayList<Event> tmpp = eventMap.get(e.getBarId());
                    if(tmpp == null){
                        tmpp = new ArrayList<Event>();
                        tmpp.add(e);
                        eventMap.put(e.getBarId(), tmpp);
                    }
                    else{
                        tmpp.add(e);
                    }

                    Log.d("In LOOP", e.message);
                } catch (JSONException e) {

                }
            }

        }
    }
    protected static class eventQuery extends AsyncTask<String, Void, JSONArray> {
        Activity mActivity;
        public eventQuery(Activity activity){
            mActivity = activity;
        }
        @Override
        protected JSONArray doInBackground(String... strs) {
            // Your code here
            String temp1 = "";
            String latitude = strs[0];
            String longitude = strs[1];

            //Construct an HTTP POST
            HttpClient httpclient = new DefaultHttpClient();
            String command = (MainActivity.API_URL + "api/eventloc/?" +
                    "location=POINT(" + latitude + "%20" + longitude + ")&radius=" + MainActivity.RADIUS);
            Log.d("EventQueryCommand", command);
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
            NewsFeed.eventsListDataHasChanged();
            ExpandableListView lview = (ExpandableListView) mActivity.findViewById(R.id.listView);
            ((BaseExpandableListAdapter)lview.getExpandableListAdapter()).notifyDataSetChanged();
            NewsFeed.mSwipeRefreshLayout.setRefreshing(false);

        }
    }
    public static void getBars(String latitude, String longitude){

        barQuery hi = new barQuery();
        String strs[ ] = new String [3];
        strs[0] = latitude;
        strs[1] = longitude;
        hi.execute(strs);


    }
    public static void fillBars(JSONArray bars){
        barMap = new HashMap<Integer, Bar>();
        if (bars != null) {
            for (int i = 0; i < bars.length(); i++) {
                try {
                    JSONObject tmp = bars.getJSONObject(i);
                    Bar bar = new Bar(tmp.getString("bar_name"));
                    Log.d("Fill Bars", bar.name);
                    bar.setId(tmp.getInt("id"));
                    bar.setPatrons(tmp.getInt("patrons"));
                    String parse = tmp.getString("location");
                    Log.d("BREAKING", parse);
                    if (parse == null || parse.equals("null"))
                        continue;
                    Matcher m = Pattern.compile("\\((.*?)\\)").matcher(parse);
                    m.find();
                    parse = m.group(1);
                    String array[] = parse.split(" ");
                    LatLng loc = new LatLng(Double.parseDouble(array[1]), Double.parseDouble(array[0]));
                    bar.setLocation(loc);
                    barMap.put(bar.getId(), bar);

                } catch (JSONException e) {

                }
            }

        }
    }
    protected static class barQuery extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strs) {
            // Your code here
            String temp1 = "";
            String latitude = strs[0];
            String longitude = strs[1];

            //Construct an HTTP POST
            HttpClient httpclient = new DefaultHttpClient();
            String command = (MainActivity.API_URL + "api/bars/?" +
                    "location=POINT(" + latitude + "%20" + longitude + ")&radius=" + MainActivity.RADIUS);
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
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray res) {
            APICalls.fillBars(res);
            Collection<Bar> barList = barMap.values();
            if(barMap.size() != 0) {
                MainActivity.mGeofenceManager.onStart();
                MainActivity.mGeofenceManager.addGeofences(barList);
                MainActivity.mGeofenceManager.enableGeofence();
            }
        }
    }
    protected static class barPopulation extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strs) {
            // Your code here
            String temp1 = "";
            int enter = Integer.parseInt(strs[0]);
            int bid = Integer.parseInt(strs[1]);
            int wait = Integer.parseInt(strs[2]);

            //Construct an HTTP POST
            String command = "";
            HttpClient httpclient = new DefaultHttpClient();
            if (enter==0) {
                command = (MainActivity.API_URL + "api/enter_location/" + bid);
            }
            else{
                command = (MainActivity.API_URL + "api/leave_location/" + bid);
            }
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
            return 1;
        }

        @Override
        protected void onPostExecute(Integer res) {
            super.onPostExecute(res);
        }
    }

}
