package com.example.thepostcardproject.utilities;

import android.util.Log;

import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;
import com.parse.ParseException;

import java.util.Comparator;

public class LocationComparator implements Comparator<Postcard> {
    private static final String TAG = "LocationComparator";
    public Location targetLocation;

    public LocationComparator(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    /**
     * Compares two postcards based on the distance between the target location and
     * the location the postcard was sent from
     * @param postcard1 The first postcard to compare
     * @param postcard2 The second postcard to compare
     * @return Returns -1 if postcard1 is closer than postcard2,
     *         1 if postcard1 is further than postcard2, and
     *         0 if they are the same distance, up to half a mile
     */
    @Override
    public int compare(Postcard postcard1, Postcard postcard2) {
        double distance1 = 0;
        double distance2 = 0;
        try {
            distance1 = Location.getDistanceBetweenLocations(postcard1.getLocationFrom(), targetLocation);
            distance2 = Location.getDistanceBetweenLocations(postcard2.getLocationFrom(), targetLocation);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (distance1 < distance2) {
            return -1;
        } else if (distance1 > distance2) {
            return 1;
        } else {
            return 0;
        }
    }
}
