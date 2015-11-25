package android.csulb.edu.fitnessapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class MapActivity extends Activity
{
    // pointList contains the coordinates to be plotted with polyline
    private ArrayList<LatLng> pointList;
    private GoogleMap map;

    // Fields for flash functionality
    Camera camera;
    Camera.Parameters parameters;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_map);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);

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

            // Add markers at start and end of track
            map.addMarker(new MarkerOptions().position(pointList.get(0)).title("Start"));
            map.addMarker(new MarkerOptions().position(pointList.get(pointList.size()-1)).title("End"));

            // Move camera.  Zoom level arbitrarily set to 12.
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(pointList.get(0), 12);
            map.animateCamera(update);
        }

        if (isFlashSupported())
        {
            camera = Camera.open();
            parameters = camera.getParameters();
        }
        else
        {
            showNoFlashAlert();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fitness_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.led_on:
                // Turn on LED
                parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
                return true;
            case R.id.led_off:
                // Turn off LED
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                camera.stopPreview();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNoFlashAlert()
    {
        new AlertDialog.Builder(this)
                .setMessage("Your device hardware does not support flashlight!")
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Error")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

    private boolean isFlashSupported()
    {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // Release camera when activity paused
        if(camera != null)
        {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
