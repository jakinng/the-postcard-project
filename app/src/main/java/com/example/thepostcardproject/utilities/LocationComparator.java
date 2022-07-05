package com.example.thepostcardproject.utilities;

import android.util.Log;

import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;

import java.util.Comparator;

public class LocationComparator implements Comparator<Postcard> {
    private static final String TAG = "LocationComparator";
    public Location targetLocation;

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public LocationComparator(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    @Override
    public int compare(Postcard postcard1, Postcard postcard2) {
        double distance1 = Location.getDistanceBetweenLocations(postcard1.getLocationFrom(), targetLocation);
        double distance2 = Location.getDistanceBetweenLocations(postcard2.getLocationFrom(), targetLocation);
//        if (distance1 > distance2) {
//            return -1;
//        } else if (distance1 == distance2) {
//            return 0;
//        } return 1;
        return (int) (distance1 - distance2);
    }
}
