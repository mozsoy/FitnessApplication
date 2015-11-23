package android.csulb.edu.fitnessapp;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapActivity extends Activity
{
    // pointList contains the coordinates to be plotted with polyline
    private ArrayList<LatLng> pointList;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_map);

        // When user taps an item in FitnessFilesFragment, ArrayList<LatLng> is sent as "selectedTrack"
        Bundle myInput = this.getIntent().getExtras();
        if (myInput == null)
            Log.d("debug", "argument was null");
        else
        {
            pointList = myInput.getParcelableArrayList("selectedTrack");
        }

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.separate_map)).getMap();

        // Add zoom buttons
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        if (pointList != null && !(pointList.isEmpty()))
        {
            // Draw polyline using pointList
            map.addPolyline((new PolylineOptions())
                    .addAll(pointList).width(5).color(Color.BLUE).geodesic(true));

            // Move camera.  Zoom level arbitrarily set to 12.
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(pointList.get(0), 12);
            map.animateCamera(update);
        }
    }
}
