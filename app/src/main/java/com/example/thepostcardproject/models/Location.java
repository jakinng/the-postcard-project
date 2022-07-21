package com.example.thepostcardproject.models;

import static com.example.thepostcardproject.utilities.Keys.KEY_ID;
import static com.example.thepostcardproject.utilities.Keys.KEY_LOCATION_NAME;
import static com.example.thepostcardproject.utilities.Keys.KEY_NAME;
import static com.example.thepostcardproject.utilities.Keys.KEY_COORDINATES;
import static com.example.thepostcardproject.utilities.Keys.KEY_ADDRESS;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Location")
public class Location extends ParseObject {
    public static final String TAG = "Location";

    public Location() { super(); }

    public Location(String locationName, String address, ParseGeoPoint coordinates, String id) {
        super();
        setLocationName(locationName);
        setAddress(address);
        setCoordinates(coordinates);
        setId(id);
    }

    public String getLocationName() {
        try {
            return fetchIfNeeded().getString(KEY_LOCATION_NAME);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setLocationName(String locationName) {
        put(KEY_LOCATION_NAME, locationName);
    }

    public String getAddress() {
        return getString(KEY_ADDRESS);
    }

    public void setAddress(String address) {
        put(KEY_ADDRESS, address);
    }

    public ParseGeoPoint getCoordinates() throws ParseException {
        return fetchIfNeeded().getParseGeoPoint(KEY_COORDINATES);
    }

    public void setCoordinates(ParseGeoPoint coordinates) {
        put(KEY_COORDINATES, coordinates);
    }

    public String getId() {
        return getString(KEY_ID);
    }

    public void setId(String id) {
        put(KEY_ID, id);
    }

    // ##########################################
    // ##   DISTANCE TO TARGET LOCATION        ##
    // ##########################################

    /**
     * Calculate the great circle (straight line along the surface of the Earth) distance between two locations
     * @param location1 The first location to calculate distance from
     * @param location2 The second location to calculate distance to
     * @return The distance between the two points
     */
    public static double getDistanceBetweenLocations(Location location1, Location location2) throws ParseException {
            ParseGeoPoint coordinates1 = location1.getCoordinates();
            ParseGeoPoint coordinates2 = location2.getCoordinates();
            return distance(coordinates1.getLatitude(), coordinates1.getLongitude(),
                    coordinates2.getLatitude(), coordinates2.getLongitude());
    }

    /**
     * Calculate the great circle (straight line along the surface of the Earth) distance between two points,
     * specified by latitude and longitude, assuming the Earth is a perfect sphere
     * @param lat1 The latitude of the first point
     * @param lon1 The longitude of the first point
     * @param lat2 The latitude of the second point
     * @param lon2 The longitude of the second point
     * @return The distance between the two points, specified by latitude and longitude
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double p = Math.PI / 180;
        double a = 0.5 - Math.cos((lat2 - lat1) * p)/2 +
                Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                        (1 - Math.cos((lon2 - lon1) * p))/2;
        double radius_of_earth = 3958.8;
        return 2 * radius_of_earth * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
    }
}
