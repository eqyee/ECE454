package edu.wisc.ece.uiapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class BarInfoActivity extends Activity {
    int barId;
    int eventInfo;

    private TextView barName_tv;
    private TextView barLocation_tv;

    Bar currBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_info);
        /*Grab the bundle from the intent that started this activity. Used to get barID and EventID*/
        Bundle b = this.getIntent().getExtras();
        barId = Integer.parseInt(b.getString(NewsFeed.BAR_ID));
        if(b.getString(NewsFeed.EVENT_POSITION) != null){
            eventInfo = Integer.parseInt(b.getString(NewsFeed.EVENT_POSITION));
        }

        getViews();
        currBar = APICalls.barMap.get(barId);
        setViews();
    }


    /*Gets a reference to each view on the page*/
    private void getViews() {
        barName_tv = (TextView) findViewById(R.id.bar_name_text);
        barLocation_tv = (TextView) findViewById(R.id.bar_location_tv);

    }

    /*Used to populate the views*/
    private void setViews() {
        barName_tv.setText(currBar.getName());
        //Probably need to do something with the getLocation
        barLocation_tv.setText(currBar.getLocation().toString());
    }
}
