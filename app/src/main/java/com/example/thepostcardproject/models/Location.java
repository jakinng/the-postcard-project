package com.example.thepostcardproject.models;

import static com.example.thepostcardproject.utilities.Keys.KEY_ABBREVIATED_NAME;
import static com.example.thepostcardproject.utilities.Keys.KEY_COORDINATES;
import static com.example.thepostcardproject.utilities.Keys.KEY_FORMATTED_NAME;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Location")
public class Location extends ParseObject {
    public Location() { super(); }

    public Location(String formattedName, String abbreviatedName, ParseGeoPoint coordinates) {
        super();
        setFormattedName(formattedName);
        setAbbreviatedName(abbreviatedName);
        setCoordinates(coordinates);
    }

    public String getFormattedName() {
        return getString(KEY_FORMATTED_NAME);
    }

    public void setFormattedName(String formattedName) {
        put(KEY_FORMATTED_NAME, formattedName);
    }

    public String getAbbreviatedName() {
        return getString(KEY_ABBREVIATED_NAME);
    }

    public void setAbbreviatedName(String abbreviatedName) {
        put(KEY_ABBREVIATED_NAME, abbreviatedName);
    }

    public ParseGeoPoint getCoordinates() {
        return getParseGeoPoint(KEY_COORDINATES);
    }

    public void setCoordinates(ParseGeoPoint coordinates) {
        put(KEY_COORDINATES, coordinates);
    }
}
