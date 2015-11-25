package android.csulb.edu.fitnessapp;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;


public class FitnessMainActivity extends Activity implements ActionBar.TabListener, FitnessTrackerFragment.OnFitnessTrackerListener,
    FitnessChartFragment.OnFitnessChartListener, FitnessFilesFragment.OnFitnessFileListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private GoogleMap map;
    GPSTracker gps;

    // Fields for flash functionality
    Camera camera;
    Camera.Parameters parameters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_main);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onFitnessChartInteraction(String text) {

    }

    @Override
    public void onFitnessFileInteraction(String text) {

    }

    @Override
    public void onFitnessTrackerInteraction(String text) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0) {
                return FitnessTrackerFragment.newInstance("test", "test");
            }
            else if(position == 1) {
                return FitnessChartFragment.newInstance("test", "test", "test");
            }
            else {
                return FitnessFilesFragment.newInstance("test", "test");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.fitnesstracker);
                case 1:
                    return getString(R.string.fitnesschart);
                case 2:
                    return getString(R.string.fitnessfiles);
            }
            return null;
        }
    }

    public void onButtonStartStopClick(View v) throws IOException {
        Button btn = (Button) findViewById(R.id.btnStartStop);
        // should get coordinates that was saved every minute
        ArrayList<LatLng> coordinates = new ArrayList<>();
        coordinates.add(new LatLng(33.788542, -118.124377));
        coordinates.add(new LatLng(33.788666, -118.099318));
        coordinates.add(new LatLng(33.774382, -118.103150));
        coordinates.add(new LatLng(33.775328, -118.121233));

        if(btn.getText().toString().compareTo("Start") == 0) {
            transportationDialog();
            // Initiate file to write track
            try {
                FileOutputStream fOut = openFileOutput("track.txt", MODE_WORLD_READABLE);
                File path = getFileStreamPath("track.txt");

                byte[] bytesSize = ByteBuffer.allocate(4)
                        .putInt(coordinates.size()).array();
                fOut.write(bytesSize, 0, bytesSize.length);

                for(int i = 0; i< coordinates.size(); i++) {

                    byte[] bytesLat = ByteBuffer.allocate(8)
                            .putDouble(coordinates.get(i).latitude).array();
                    fOut.write(bytesLat, 0, bytesLat.length);

                    byte[] bytesLng = ByteBuffer.allocate(8)
                            .putDouble(coordinates.get(i).longitude).array();
                    fOut.write(bytesLng, 0, bytesLng.length);
                }
                System.out.println("file writing");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            saveTrackDialog();
        }
    }

    private LatLng getGPSLocation() {
        gps.getLocation();
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            LatLng location = new LatLng(latitude, longitude);

            return location;
        }
        else {
            gps.showSettingsAlert();
            return null;
        }
    }

    private void setCurrentLocation() {
        LatLng current = getGPSLocation();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.addMarker(new MarkerOptions().position(current).title("Start"));
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(current, 18);
        map.animateCamera(update);
    }

    private void transportationDialog() {
        String[] str = {"Walking", "Running", "Biking"};
        final ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, str);
        final Spinner sp = new Spinner(this);

        sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        sp.setAdapter(adp);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Method of Transportation");
        alertDialog.setMessage("Choose method of transportation: ");
        alertDialog.setView(sp);
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView transportation = (TextView) findViewById(R.id.transportationSel);
                String selection = sp.getSelectedItem().toString();
                TextView distance = (TextView) findViewById(R.id.distanceVal);
                TextView calories = (TextView) findViewById(R.id.caloriesVal);
                Button btn = (Button) findViewById(R.id.btnStartStop);
                map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                gps = new GPSTracker(getApplicationContext(), map, distance, calories, selection);

                mapCleanUp();
                transportation.setText(selection);
                btn.setText("Stop");
                setCurrentLocation();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void saveTrackDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Save track data?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO Save data/stop timer
                map.addMarker(new MarkerOptions().position(getGPSLocation()).title("Stop"));
                Button btn = (Button) findViewById(R.id.btnStartStop);
                btn.setText("Start");
                gps.stopUsingGPS();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO Stop timer
                map.addMarker(new MarkerOptions().position(getGPSLocation()).title("Stop"));
                Button btn = (Button) findViewById(R.id.btnStartStop);
                btn.setText("Start");
                gps.stopUsingGPS();
            }
        });

        alertDialog.setNeutralButton("Resume", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void mapCleanUp() {
        map.clear();
        GPSTracker.gpsCoordinates = new ArrayList<>();
        GPSTracker.line = map.addPolyline(new PolylineOptions());
    }
}
