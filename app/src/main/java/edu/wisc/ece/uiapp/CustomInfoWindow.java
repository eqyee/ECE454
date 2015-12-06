package edu.wisc.ece.uiapp;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by CCS on 12/6/2015.
 */
class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater=null;

    CustomInfoWindow(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        View popup=inflater.inflate(R.layout.infowindow, null);

        TextView tv=(TextView)popup.findViewById(R.id.title);
        String tempString = marker.getTitle();
        SpannableString spanString = new SpannableString(tempString);
        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        tv.setText(spanString);
        tv=(TextView)popup.findViewById(R.id.snippet);
        tv.setText(marker.getSnippet());

        return(popup);
    }
}
