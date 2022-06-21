package com.example.thepostcardproject.models;

import static com.example.thepostcardproject.utilities.Keys.KEY_CURRENT_LOCATION;
import static com.example.thepostcardproject.utilities.Keys.KEY_NAME;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {

    public User() { super(); }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public Location getCurrentLocation() { return (Location) getParseObject(KEY_CURRENT_LOCATION); }

    public void setCurrentLocation(ParseObject currentLocation) { put(KEY_CURRENT_LOCATION, currentLocation); }
}
