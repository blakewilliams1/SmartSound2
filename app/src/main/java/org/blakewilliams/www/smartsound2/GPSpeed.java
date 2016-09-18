package org.blakewilliams.www.smartsound2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
//import android.util.Log;

/**
 * Created by Blake on 9/11/2016.
 */
public class GPSpeed implements LocationListener {
    private int PERSONAL_LOCATION_REQUEST_CODE = 123;
    private Location prevLocation;
    private long prevTime;
    private double currMetricSpeed;
    private MainActivity active;

    GPSpeed(Context c){
        active = (MainActivity)c;
        prevLocation= new Location("GPSpeed");
        prevTime= System.currentTimeMillis();
        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(c,Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionCheck==-1){
                ActivityCompat.requestPermissions((Activity)c,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERSONAL_LOCATION_REQUEST_CODE);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 3, this);
        }catch(SecurityException e) {
            //Log.i("DEBUG","Permission not granted");
            e.printStackTrace();
        }
    }

    public double getMetricSpeed(){
        return currMetricSpeed;
    }

    public double getImperialSpeed(){
        return currMetricSpeed*2.23694;
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        float deltaMeters = prevLocation.distanceTo(location);
        float deltaMillis = System.currentTimeMillis()-prevTime;

        currMetricSpeed = deltaMeters/(deltaMillis/1000);
        currMetricSpeed = currMetricSpeed>53.644?0:currMetricSpeed;

        //Log.i("Geo_Location", "Latitude: " + latitude + ", Longitude: " + longitude);

        prevLocation.setLatitude(latitude);
        prevLocation.setLongitude(longitude);
        prevTime= System.currentTimeMillis();

        //((Button)active.findViewById(R.id.threadButton)).setText(Double.toString(getImperialSpeed()));
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
}
