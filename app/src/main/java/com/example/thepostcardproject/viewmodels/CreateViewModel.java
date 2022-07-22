package com.example.thepostcardproject.viewmodels;

import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thepostcardproject.models.Filter;
import com.example.thepostcardproject.models.FilteredPhoto;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.User;
import com.google.android.libraries.places.api.model.Place;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateViewModel extends ViewModel {
    public static final String TAG = "CreateViewModel";

    public File photoFile = null;
    public MutableLiveData<Drawable> drawablePhoto =  new MutableLiveData<Drawable>();
    public MutableLiveData<FilteredPhoto> filteredPhoto =  new MutableLiveData<FilteredPhoto>();
    public ArrayList<String> usernames = new ArrayList<>();
    public MutableLiveData<Location> currentLocation = new MutableLiveData<Location>(((User) ParseUser.getCurrentUser()).getCurrentLocation());

    public void setupUsernameAutocomplete(FindCallback<ParseUser> findCallback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(findCallback);
    }

    public void addUsersToList(ArrayList<ParseUser> users) {
        for (ParseUser parseUser : users) {
            User user = (User) parseUser;
            usernames.add(user.getUsername());
        }
    }

    public void saveNewLocation(Place place) {
        ParseGeoPoint coordinates = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
        Location newLocation = new Location(place.getName(), place.getAddress(), coordinates, place.getId());
        currentLocation.setValue(newLocation);
    }
}
