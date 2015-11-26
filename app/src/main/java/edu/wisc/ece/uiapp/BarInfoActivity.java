package edu.wisc.ece.uiapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class BarInfoActivity extends Activity {
    int barId;
    int eventInfo;
    private TextView barName_tv;
    Bar currBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_info);
        Bundle b = this.getIntent().getExtras();
            barId = Integer.parseInt(b.getString(NewsFeed.BAR_ID));
            eventInfo = Integer.parseInt(b.getString(NewsFeed.EVENT_POSITION));
            barName_tv = (TextView) findViewById(R.id.bar_name_text);
            currBar = APICalls.barMap.get(barId);
            if(currBar != null) {
                barName_tv.setText(currBar.getName());
            }
            else{
                Log.d("Bar id should be", Integer.toString(barId));
                for(Bar bar : APICalls.barMap.values()) {

                    Log.d(Integer.toString(bar.getId()), bar.getName());
                }
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bar_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
