package edu.wisc.ece.uiapp;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by CCS on 10/28/2015.
 */
public class HeatMap extends Fragment implements OnMapReadyCallback {
    private static View view;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private com.quinny898.library.persistentsearch.SearchBox search;
    private Geocoder geocoder;
    private GoogleMap map;

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String API_KEY = "AIzaSyDWi6VXrx2hVMdzeLhLCK8lPxUEAymLJGA";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            ViewGroup parent = (ViewGroup)view.getParent();
            if(parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_heatmap, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        try {
            mapFragment.getMapAsync(this);
        } catch(Exception e){
            Log.e("HeatMap", "Get a newer phone");
        }
        geocoder = new Geocoder(view.getContext(), Locale.US);

        search = (com.quinny898.library.persistentsearch.SearchBox)view.findViewById(R.id.searchbox);
        setupSearchBar();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        if(this.map != null)
            updateHeatMap();
    }
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        updateHeatMap();
        LatLng center = new LatLng(CurrentLocation.latitude, CurrentLocation.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15.0f));
    }
    private void updateHeatMap(){
        List<LatLng> heatData = getHeatData();
        mProvider = new HeatmapTileProvider.Builder()
                .data(heatData)
                .build();
        mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
    private List<LatLng> getHeatData(){
        ArrayList<LatLng> heatData = new ArrayList<>();
        if(APICalls.barMap == null)
            return heatData;
        for(Bar bar: APICalls.barMap.values()){
            for(int i = 0; i < bar.getPatrons(); i++) {
                heatData.add(bar.getLocation());
            }
        }
        return heatData;
    }
    private void setupSearchBar(){
        search.setLogoText("Search Bars");
        search.setLogoTextColor(Color.LTGRAY);
        search.setSearchListener(new SearchBox.SearchListener() {

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
            }

            @Override
            public void onSearchTermChanged(String searchTerm) {

                if (searchTerm != null) {

                    try {
                        search.clearSearchable();
                        List<String> results = autocomplete(searchTerm);
                        for (String place : results) {

                            SearchResult option = new SearchResult(place,
                                    ContextCompat.getDrawable(view.getContext(), android.R.drawable.ic_menu_search));
                            search.addSearchable(option);
                        }
                        search.updateResults();
                    } catch (Exception e) {
                        Log.e("", "Something went wrong: ", e);
                    }
                }

            }

            @Override
            public void onSearch(String searchTerm) {
                try {
                    List<Address> results = geocoder.getFromLocationName(searchTerm, 1);
                    if (results.size() == 0)
                        return;
                    Address address = results.get(0);
                    LatLng center = new LatLng(address.getLatitude(), address.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12.0f));

                    map.addMarker(new MarkerOptions()
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .position(center));
                    updateHeatMap();
                } catch (Exception e) {
                    Log.e("", "Something went wrong: ", e);
                }
            }

            @Override
            public void onResultClick(SearchResult result) {
                try {
                    List<Address> results = geocoder.getFromLocationName(result.title, 1);
                    if (results.size() == 0)
                        return;
                    Address address = results.get(0);
                    LatLng center = new LatLng(address.getLatitude(), address.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 17));

                    map.addMarker(new MarkerOptions()
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .position(center));
                    updateHeatMap();
                } catch (Exception e) {
                    Log.e("", "Something went wrong: ", e);
                }
            }


            @Override
            public void onSearchCleared() {

            }

        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isAdded() && requestCode == SearchBox.VOICE_RECOGNITION_CODE && resultCode == getActivity().RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            search.populateEditText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:US");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("Autocomplete", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("Autocomplete", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e("Autocomplete", "Cannot process JSON results", e);
        }

        return resultList;
    }

}

