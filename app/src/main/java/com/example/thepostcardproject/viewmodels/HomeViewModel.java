package com.example.thepostcardproject.viewmodels;

import static com.example.thepostcardproject.utilities.Keys.KEY_USER_FROM;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.models.User;
import com.example.thepostcardproject.utilities.LocationComparator;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    public static final String KEY_CREATED_AT = "createdAt";

    public MutableLiveData<List<Postcard>> receivedPostcards;

    public static final int LOAD_AT_ONCE = 5;
    public static final int SORT_MOST_RECENT = 0;
    public static final int SORT_EARLIEST = 1;
    public static final int SORT_LOCATION_TO = 2;
    public static final int SORT_LOCATION_FROM = 3;
    private int sortBy = 0;

    // Save the dates and location to filter by
    public MutableLiveData<Pair<Date, Date>> dateRange = new MutableLiveData<>(null);
    public MutableLiveData<Location> targetLocation = new MutableLiveData<>(((User) ParseUser.getCurrentUser()).getCurrentLocation());
    public MutableLiveData<User> targetUser = new MutableLiveData<>(null);

    public HomeViewModel() {
    }

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
    }

    public int getSortBy() {
        return sortBy;
    }

    // ********************************************************
    // **            QUERY FOR LIST OF POSTCARDS             **
    // ********************************************************

    public MutableLiveData<List<Postcard>> getReceivedPostcards() {
        if (receivedPostcards == null) {
            receivedPostcards = new MutableLiveData<List<Postcard>>(new ArrayList<Postcard>());
        }
        return receivedPostcards;
    }

    /**
     * Add postcards to the current list of postcards
     * @param postcards The list of postcards to add
     */
    public void addPostcards(List<Postcard> postcards) {
        getReceivedPostcards().getValue().addAll(postcards);
        receivedPostcards.setValue(receivedPostcards.getValue());
    }

    /**
     * Clears the current list of postcards
     */
    public void clearPostcards() {
        getReceivedPostcards().getValue().clear();
        receivedPostcards.setValue(receivedPostcards.getValue());
    }

    /**
     * Load more postcards
     * @param skip The skip in the query; used for infinite scroll
     * @param findCallback The action to take after the query has completed
     */
    public void loadMorePostcards(int skip, FindCallback<Postcard> findCallback) {
        ParseQuery<Postcard> query = ParseQuery.getQuery(Postcard.class);
        query.setSkip(skip);

        // Set date constraints
        if (dateRange.getValue() != null) {
            query.whereGreaterThan(KEY_CREATED_AT, dateRange.getValue().first);
            query.whereLessThan(KEY_CREATED_AT, dateRange.getValue().second);
        }

        if (targetUser.getValue() != null) {
            query.whereEqualTo(KEY_USER_FROM, targetUser.getValue());
        } else {
            query.whereEqualTo(KEY_USER_FROM, ParseUser.getCurrentUser());
        }

        // Sort by the most recent date first
        if (sortBy == SORT_MOST_RECENT) {
            query.setLimit(LOAD_AT_ONCE);
            query.addDescendingOrder(KEY_CREATED_AT);
            query.setSkip(skip);
        } else if (sortBy == SORT_EARLIEST) {
            // Sort by the least recent date first
            query.setLimit(LOAD_AT_ONCE);
            query.addAscendingOrder(KEY_CREATED_AT);
            query.setSkip(skip);
        } else if (!(sortBy == SORT_LOCATION_FROM) & !(sortBy == SORT_LOCATION_TO)) {
            Log.d(TAG, "The sort has not been clicked!");
        }
        query.findInBackground(findCallback);
    }

    /**
     * Adds the postcards to the list to display, sorted by location
     * @param postcards A list of all the postcards to display
     * @param sortBy An integer representing what to sort the postcards by
     */
    public void addPostcardsByLocation(List<Postcard> postcards, int sortBy) {
        LocationComparator comparator = null;
        if (sortBy == SORT_LOCATION_FROM) {
            comparator = new LocationComparator(targetLocation.getValue(), new LocationComparator.getPostcardLocation() {
                @Override
                public Location getLocation(Postcard postcard) {
                    return postcard.getLocationTo();
                }
            });
        } else if (sortBy == SORT_LOCATION_TO) {
            comparator = new LocationComparator(targetLocation.getValue(), new LocationComparator.getPostcardLocation() {
                @Override
                public Location getLocation(Postcard postcard) {
                    return postcard.getLocationFrom();
                }
            });
        }
        Collections.sort(postcards, comparator);
        clearPostcards();
        addPostcards(postcards);
    }

    // ######################################################
    // ##            FILTER BY DATE AND LOCATION           ##
    // ######################################################

    /**
     * Resets the date range to all possible dates
     */
    public void clearDateRange() {
        dateRange.setValue(null);
    }

    public void setDateRange(Date startDate, Date endDate) {
        dateRange.setValue(new Pair<>(startDate, endDate));
    }

    public MutableLiveData<User> getTargetUser() {
        if (targetUser == null) {
            targetUser = new MutableLiveData<>();
        }
        return targetUser;
    }
}
