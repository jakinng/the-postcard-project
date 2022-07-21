package com.example.thepostcardproject.viewmodels;

import static com.example.thepostcardproject.utilities.Keys.KEY_USER_FROM;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thepostcardproject.adapters.ProfilePostcardAdapter;
import com.example.thepostcardproject.databinding.FragmentProfileBinding;
import com.example.thepostcardproject.fragments.ProfileFragment;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.models.User;
import com.example.thepostcardproject.utilities.EndlessRecyclerViewScrollListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {
    private static final String TAG = "ProfileViewModel";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final int LOAD_AT_ONCE = 5;

    public User currentUser;
    public MutableLiveData<Integer> selectedPostcardPosition;
    private MutableLiveData<String> locationName;
    public MutableLiveData<List<Postcard>> sentPostcards;

    // ********************************************************
    // **    HELPER METHODS DEALING WITH THE CURRENT USER    **
    // ********************************************************

    public void setCurrentUser() {
        currentUser = (User) ParseUser.getCurrentUser();
        getLocationName().setValue(currentUser.getCurrentLocation().getLocationName());
    }

    public String getUsername() {
        return currentUser.getUsername();
    }

    // ********************************************************
    // **            HELPER METHODS FOR LOCATION             **
    // ********************************************************

    public void saveNewLocation(Place place) {
        ParseGeoPoint coordinates = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
        Location newLocation = new Location(place.getName(), place.getAddress(), coordinates, place.getId());
        currentUser.setCurrentLocation(newLocation);
        currentUser.saveInBackground();
        locationName.setValue(place.getName());
    }

    public MutableLiveData<String> getLocationName() {
        if (locationName == null) {
            locationName = new MutableLiveData<String>();
        }
        return locationName;
    }

    // ********************************************************
    // **            QUERY FOR LIST OF POSTCARDS             **
    // ********************************************************

    public MutableLiveData<List<Postcard>> getSentPostcards() {
        if (sentPostcards == null) {
            sentPostcards = new MutableLiveData<List<Postcard>>(new ArrayList<Postcard>());
        }
        return sentPostcards;
    }

    public void addPostcards(List<Postcard> postcards) {
        getSentPostcards().getValue().addAll(postcards);
        sentPostcards.setValue(sentPostcards.getValue());
    }

    public void clearPostcards() {
        getSentPostcards().getValue().clear();
        sentPostcards.setValue(sentPostcards.getValue());
    }

    public void loadMorePostcards(int skip, FindCallback<Postcard> findCallback) {
        ParseQuery<Postcard> query = ParseQuery.getQuery(Postcard.class);
        query.whereEqualTo(KEY_USER_FROM, currentUser);
        query.setLimit(LOAD_AT_ONCE);
        query.addDescendingOrder(KEY_CREATED_AT);
        query.setSkip(skip);
        query.findInBackground(findCallback);
    }
}
