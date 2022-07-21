package com.example.thepostcardproject.models;

import static com.example.thepostcardproject.utilities.Keys.KEY_CURRENT_LOCATION;
import static com.example.thepostcardproject.utilities.Keys.KEY_NAME;

import com.example.thepostcardproject.utilities.Keys;
import com.parse.ParseClassName;
import com.parse.ParseException;
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

    @Override
    public String getUsername() {
        try {
            return fetchIfNeeded().getString(Keys.KEY_USERNAME);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setUsername(String username) {
        put(Keys.KEY_USERNAME, username);
    }

    public Location getCurrentLocation() { return (Location) getParseObject(KEY_CURRENT_LOCATION); }

    public void setCurrentLocation(ParseObject currentLocation) { put(KEY_CURRENT_LOCATION, currentLocation); }
}
