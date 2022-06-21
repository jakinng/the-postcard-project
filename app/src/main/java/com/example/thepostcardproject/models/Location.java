package com.example.thepostcardproject.models;

import static com.example.thepostcardproject.utilities.Keys.KEY_LOCATION_NAME;
import static com.example.thepostcardproject.utilities.Keys.KEY_NAME;
import static com.example.thepostcardproject.utilities.Keys.KEY_COORDINATES;
import static com.example.thepostcardproject.utilities.Keys.KEY_ADDRESS;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Location")
public class Location extends ParseObject {
    public Location() { super(); }

    public Location(String locationName, String address, ParseGeoPoint coordinates) {
        super();
        setLocationName(locationName);
        setAddress(address);
        setCoordinates(coordinates);
    }

    public String getLocationName() throws ParseException {
        return fetchIfNeeded().getString(KEY_LOCATION_NAME);
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

    public ParseGeoPoint getCoordinates() {
        return getParseGeoPoint(KEY_COORDINATES);
    }

    public void setCoordinates(ParseGeoPoint coordinates) {
        put(KEY_COORDINATES, coordinates);
    }

    public String locationFromAddressComponents() {
        return "hi";
    }
}
