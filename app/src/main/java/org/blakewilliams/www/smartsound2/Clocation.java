package org.blakewilliams.www.smartsound2;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Blake on 8/27/2016.
 */
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;


public class Clocation extends Location {

    private boolean bUseMetricUnits = false;

    public Clocation(Location location)
    {
        this(location, true);
    }

    public Clocation(Location location, boolean bUseMetricUnits) {
        super(location);
        //TODO: Instantiate a notification that will close the thread when swiped away
        this.bUseMetricUnits = bUseMetricUnits;
    }


    public boolean getUseMetricUnits()
    {
        return this.bUseMetricUnits;
    }

    public void setUseMetricunits(boolean bUseMetricUntis)
    {
        this.bUseMetricUnits = bUseMetricUntis;
    }

    @Override
    public float distanceTo(Location dest) {
        float nDistance = super.distanceTo(dest);
        if(!this.getUseMetricUnits())
        {
            //Convert meters to feet
            nDistance = nDistance * 3.28083989501312f;
        }
        return nDistance;
    }

    @Override
    public float getAccuracy() {
        float nAccuracy = super.getAccuracy();
        if(!this.getUseMetricUnits())
        {
            //Convert meters to feet
            nAccuracy = nAccuracy * 3.28083989501312f;
        }
        return nAccuracy;
    }

    @Override
    public double getAltitude() {
        double nAltitude = super.getAltitude();
        if(!this.getUseMetricUnits())
        {
            //Convert meters to feet
            nAltitude = nAltitude * 3.28083989501312d;
        }
        return nAltitude;
    }

    @Override
    public float getSpeed() {
        float nSpeed = super.getSpeed() * 3.6f;
        if(!this.getUseMetricUnits())
        {
            //Convert meters/second to miles/hour
            nSpeed = nSpeed * 2.2369362920544f/3.6f;
        }
        return nSpeed;
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Clocation> CREATOR = new Parcelable.Creator<Clocation>() {
        @Override
        public Clocation createFromParcel(Parcel in) {
            return null;
        }

        @Override
        public Clocation[] newArray(int size) {
            return new Clocation[size];
        }
    };

}