package android.csulb.edu.fitnessapp;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Tim on 11/9/2015.
 */
public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
    private static final long MIN_TIME_BW_UPDATES = 3000;
    protected LocationManager locationManager;
    public static ArrayList<LatLng> gpsCoordinates = new ArrayList<>();
    public static Polyline line;
    public static int gpsWait = 0;
    private GoogleMap mMap;
    private TextView mDistance;
    private TextView mCalories;
    private String mTransportSelection;

    public GPSTracker(Context context, GoogleMap map, TextView distance, TextView calories, String selection) {
        this.mContext = context;
        this.mMap = map;
        this.mDistance = distance;
        this.mCalories = calories;
        this.mTransportSelection = selection;
        line = mMap.addPolyline(new PolylineOptions());

    }

    public Location getLocation() {
        try {
            Criteria settings = new Criteria();
            settings.setAccuracy(Criteria.ACCURACY_FINE);
            settings.setPowerRequirement(Criteria.POWER_LOW);
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(settings, true);

            if(provider != null) {
                this.canGetLocation = true;
                locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                location = locationManager.getLastKnownLocation(provider);
                if(location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void stopUsingGPS() {
        locationManager.removeUpdates(GPSTracker.this);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS Settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
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

    @Override
    public void onLocationChanged(Location location) {
        //wait for 2 location points before recording
        if(gpsWait > 1) {
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            if (gpsCoordinates.size() > 1) {
                line.setColor(Color.GREEN);
                line.setPoints(gpsCoordinates);
                updateDistanceText(gpsCoordinates.get(gpsCoordinates.size() - 1), point);
                updateCaloriesText();
            } else if (gpsCoordinates.size() == 0) {
                mMap.addMarker(new MarkerOptions().position(point).title("Start"));
            }
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(point, 18);
            mMap.animateCamera(update);
            gpsCoordinates.add(point);
        }
        else {
            gpsWait++;
        }
    }

    private void updateDistanceText(LatLng from, LatLng to) {
        float[] results = new float[1];
        Location.distanceBetween(from.latitude, from.longitude, to.latitude, to.longitude, results);
        mDistance.setText(String.valueOf(Float.parseFloat(mDistance.getText().toString())
                + results[0]));
    }

    private void updateCaloriesText() {
        //convert meters to kilometer
        double distance = Float.parseFloat(mDistance.getText().toString()) / 1000;
        double calories = 0;

        if(mTransportSelection.compareTo("Walking") == 0) {
            //average calories burned walking 1 km at 2 mph
            calories = 57 * distance;
        }
        else if(mTransportSelection.compareTo("Running") == 0) {
            //average calories burned running 1 km at 8 mph
            calories = 76 * distance;
        }
        else {
            //average calories burned biking 1 km at 13 mph
            calories = 33 * distance;
        }
        DecimalFormat df = new DecimalFormat("###.###");
        String str = df.format(calories);
        mCalories.setText(df.format(calories));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
