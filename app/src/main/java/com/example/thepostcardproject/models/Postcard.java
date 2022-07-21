package com.example.thepostcardproject.models;

import static com.example.thepostcardproject.utilities.Keys.KEY_COVER_PHOTO;
import static com.example.thepostcardproject.utilities.Keys.KEY_COVER_PHOTO_FILTERED;
import static com.example.thepostcardproject.utilities.Keys.KEY_LOCATION_FROM;
import static com.example.thepostcardproject.utilities.Keys.KEY_LOCATION_TO;
import static com.example.thepostcardproject.utilities.Keys.KEY_MESSAGE;
import static com.example.thepostcardproject.utilities.Keys.KEY_USER_FROM;
import static com.example.thepostcardproject.utilities.Keys.KEY_USER_TO;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Comparator;

@ParseClassName("Postcard")
public class Postcard extends ParseObject {

    public Postcard() { super(); }

    public Postcard(ParseUser userFrom, ParseUser userTo, ParseObject locationFrom, ParseObject locationTo, String message, FilteredPhoto filteredPhoto) {
        super();
        setUserFrom(userFrom);
        setUserTo(userTo);
        setLocationFrom(locationFrom);
        setLocationTo(locationTo);
        setMessage(message);
        setCoverPhotoFiltered(filteredPhoto);
    }

    public FilteredPhoto getCoverPhotoFiltered() throws ParseException {
        return (FilteredPhoto) fetchIfNeeded().getParseObject(KEY_COVER_PHOTO_FILTERED);
    }

    public void setCoverPhotoFiltered(FilteredPhoto filteredPhoto) {
        put(KEY_COVER_PHOTO_FILTERED, filteredPhoto);
    }

    public User getUserFrom() throws ParseException {
        return (User) fetchIfNeeded().getParseUser(KEY_USER_FROM);
    }

    public void setUserFrom(ParseUser userFrom) {
        put(KEY_USER_FROM, userFrom);
    }

    public User getUserTo() throws ParseException {
        return (User) fetchIfNeeded().getParseUser(KEY_USER_TO);
    }

    public void setUserTo(ParseUser userTo) {
        put(KEY_USER_TO, userTo);
    }

    public Location getLocationFrom() {
        return (Location) getParseObject(KEY_LOCATION_FROM);
    }

    public void setLocationFrom(ParseObject locationFrom) {
        put(KEY_LOCATION_FROM, locationFrom);
    }

    public Location getLocationTo() {
        return (Location) getParseObject(KEY_LOCATION_TO);
    }

    public void setLocationTo(ParseObject locationTo) {
        put(KEY_LOCATION_TO, locationTo);
    }

    public String getMessage() {
        return getString(KEY_MESSAGE);
    }

    public void setMessage(String message) {
        put(KEY_MESSAGE, message);
    }
}
