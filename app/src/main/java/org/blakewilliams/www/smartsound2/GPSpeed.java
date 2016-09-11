package org.blakewilliams.www.smartsound2;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Blake on 9/11/2016.
 */
public class GPSpeed implements LocationListener {
    Location prevLocation;
    private long prevTime;
    private double currMetricSpeed;

    GPSpeed(){
        prevLocation= new Location("GPSpeed");
        prevTime= System.currentTimeMillis();

    }

    public double getSpeed(){
        return currMetricSpeed;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        float deltaMeters = prevLocation.distanceTo(location);
        float deltaMillis = System.currentTimeMillis()-prevTime;

        currMetricSpeed = deltaMeters/(deltaMillis/1000);

        //Log.i("Geo_Location", "Latitude: " + latitude + ", Longitude: " + longitude);

        prevLocation.setLatitude(latitude);
        prevLocation.setLongitude(longitude);
        prevTime= System.currentTimeMillis();

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}
