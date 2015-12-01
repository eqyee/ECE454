package edu.wisc.ece.uiapp;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
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

    HashMap<Marker, Integer> markers;
    ArrayList<Marker> markerArray;

    public static final String EVENT_POSITION = "EventId";
    public static final String BAR_ID = "BarId";

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message inputMessage){
            if(inputMessage.obj instanceof ArrayList){
                ArrayList<String> results = (ArrayList)inputMessage.obj;
                for (String place : results) {

                    SearchResult option = new SearchResult(place,
                            ContextCompat.getDrawable(view.getContext(), android.R.drawable.ic_menu_search));
                    search.addSearchable(option);
                }
                search.updateResults();
            }
        }
    };


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
        map.setMyLocationEnabled(true);
        updateHeatMap();
        LatLng center = new LatLng(CurrentLocation.latitude, CurrentLocation.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15.0f));
        placePins();

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent i = new Intent(view.getContext(), BarInfoActivity.class);

                i.putExtra(BAR_ID, Integer.toString(markers.get(marker)));
                i.putExtra(EVENT_POSITION, -1);
                startActivity(i);
            }
        });

    }
    private void updateHeatMap(){
        List<LatLng> heatData = getHeatData();
        if(heatData.size() > 0) {
            mProvider = new HeatmapTileProvider.Builder()
                    .data(heatData)
                    .build();
            mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
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
    private void placePins(){
        if(markerArray != null && markerArray.size() > 0) {
            for (Marker curr : markerArray) {
                curr.remove();
            }
        }
        markers = new HashMap<>();
        markerArray = new ArrayList<>();
        for(Bar bar: APICalls.barMap.values()){
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(bar.getLocation())
                    .title(bar.getName()));
            markers.put(marker, bar.getId());
            markerArray.add(marker);
        }
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
                        Runnable mRunnable = new AutocompleteSearch(searchTerm, handler);
                        mRunnable.run();
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

                    /*
                    map.addMarker(new MarkerOptions()
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .position(center));
                    */
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

                    /*
                    map.addMarker(new MarkerOptions()
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .position(center));
                    */
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

}

