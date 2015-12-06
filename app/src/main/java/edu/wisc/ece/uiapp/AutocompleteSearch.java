package edu.wisc.ece.uiapp;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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

/**
 * Created by CCS on 11/18/2015.
 */
public class AutocompleteSearch implements Runnable {
    private String input;
    private Handler handler;
    private Context context;

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String API_KEY = "AIzaSyDWi6VXrx2hVMdzeLhLCK8lPxUEAymLJGA";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private com.quinny898.library.persistentsearch.SearchBox searchBox;

    public AutocompleteSearch(Context context, String search, Handler handler, com.quinny898.library.persistentsearch.SearchBox searchBox){
        this.context = context;
        this.input = search;
        this.handler = handler;
        this.searchBox = searchBox;
    }

    public void run() {
        ArrayList<SearchResult> resultList = null;

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
            Message mMessage = Message.obtain();
            mMessage.obj = resultList;
            handler.sendMessage(mMessage);
        } catch (IOException e) {
            Log.e("Autocomplete", "Error connecting to Places API", e);
            Message mMessage = Message.obtain();
            mMessage.obj = resultList;
            handler.sendMessage(mMessage);
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
                if(!input.equals(predsJsonArray.getJSONObject(i).getString("description"))) {
                    SearchResult option = new SearchResult(predsJsonArray.getJSONObject(i).getString("description"),
                            ContextCompat.getDrawable(context, android.R.drawable.ic_menu_search));
                    resultList.add(option);
                }
            }
        } catch (JSONException e) {
            Log.e("Autocomplete", "Cannot process JSON results", e);
        }

        Message mMessage = Message.obtain();
        mMessage.obj = resultList;
        handler.sendMessage(mMessage);
    }
}
