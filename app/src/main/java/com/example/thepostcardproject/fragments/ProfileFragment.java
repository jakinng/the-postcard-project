package com.example.thepostcardproject.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static com.example.thepostcardproject.utilities.Keys.KEY_USER_FROM;
import static com.example.thepostcardproject.utilities.Keys.KEY_USER_TO;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.adapters.ProfilePostcardAdapter;
import com.example.thepostcardproject.databinding.FragmentProfileBinding;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.models.User;
import com.example.thepostcardproject.utilities.EndlessRecyclerViewScrollListener;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final int NUM_PROFILE_COLUMNS = 3;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1000;
    public static final int LOAD_AT_ONCE = 5;

    private User currentUser;
    private ArrayList<Postcard> sentPostcards;
    private ProfilePostcardAdapter adapter;

    private FragmentProfileBinding binding;

    private GoToDetailViewListener goToDetailViewListener;
    private EndlessRecyclerViewScrollListener scrollListener;

    // Counters for the infinite scroll
    private int limit = 0;
    private boolean loadMore = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(ProfileFragment.GoToDetailViewListener goToDetailViewListener) {
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.goToDetailViewListener = goToDetailViewListener;
        return profileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCurrentUser();
    }

    // TODO : override onresume so when you click on detailview and come back it's still normal

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called shortly after onCreateView
     * @param view The view containing the various visual components
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayUsername();
        displayUserLocation();
        displaySentPostcards();
        setupSwipeToRefresh();
        configureActionBar();
    }


    /**
     * Called after startActivityForResult is completed
     * @param requestCode The request code of the intent launched
     * @param resultCode The result code (RESULT_OK means there were no errors)
     * @param data The data received
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                saveNewLocation(place);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, "Error in picking location. Try again! Error message: " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ********************************************************
    // **    HELPER METHODS DEALING WITH THE CURRENT USER    **
    // ********************************************************
    /**
     * Sets the current Parse User
     */
    private void setCurrentUser() {
        currentUser = (User) ParseUser.getCurrentUser();
    }

    /**
     * Displays the current ParseUser's username
     */
    private void displayUsername() {
        binding.tvUsername.setText("@" + currentUser.getUsername());
    }

    // ********************************************************
    // **            HELPER METHODS FOR LOCATION             **
    // ********************************************************

    /**
     * Displays the current user location and lets the user edit their location
     */
    private void displayUserLocation() {
        // Display the location in the textview
        try {
            binding.tvLocation.setText(currentUser.getCurrentLocation().getLocationName());
        } catch (ParseException e) {
            Log.d(TAG, "The user has no location!");
            e.printStackTrace();
        }
        // Set an onclick listener on the location icon
        binding.ivLocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sets which fields the API request is asking for
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    /**
     * Saves the provided Place as a Location and updates the currentLocation of the current user
     * @param place The Place to save as the new location
     */
    private void saveNewLocation(Place place) {
        ParseGeoPoint coordinates = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
        Location newLocation = new Location(place.getName(), place.getAddress(), coordinates);
        currentUser.setCurrentLocation(newLocation);
        binding.tvLocation.setText(place.getName());
        currentUser.saveInBackground();
    }

    // ********************************************************
    // **         HELPER METHODS FOR POSTCARD FEED           **
    // ********************************************************


    /**
     * Displays the postcards sent by the user
     */
    private void displaySentPostcards() {
        // TODO : if limit == 0 and whatever, show "no postcards!"

        // Set the adapter with a grid layout manager
        adapter = new ProfilePostcardAdapter(getContext(), new ArrayList<>(), goToDetailViewListener);
        binding.rvPostcards.setAdapter(adapter);
        binding.rvPostcards.setHasFixedSize(true);

        // Add infinite scroll
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), NUM_PROFILE_COLUMNS);
        binding.rvPostcards.setLayoutManager(gridLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (loadMore) {
                    loadMorePostcards();
                }
            }
        };
        binding.rvPostcards.addOnScrollListener(scrollListener);
        loadMorePostcards();
    }

    /**
     * Loads more postcards sent by the current user into the variable sentPostcards
     * Used for infinite scroll and for loading the initial postcards
     */
    private void loadMorePostcards() {
        ParseQuery<Postcard> query = ParseQuery.getQuery(Postcard.class);
        query.whereEqualTo(KEY_USER_FROM, currentUser);
        query.setLimit(LOAD_AT_ONCE);
        query.addDescendingOrder("createdAt");

        query.setSkip(limit);
        if (limit == 0) {
            scrollListener.resetState();
        }

        query.findInBackground(new FindCallback<Postcard>() {
            @Override
            public void done(List<Postcard> postcards, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Just got " + postcards.size() + " postcards!");
                    binding.swipeContainer.setRefreshing(false);
                    if (limit == 0) {
                        adapter.clear();
                        if (postcards.size() == 0) {
                            Snackbar.make(binding.rvPostcards, "You have not been sent any postcards!", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    limit += postcards.size();
                    if (postcards.size() != 0) {
                        loadMore = true;
                        adapter.addAll((ArrayList<Postcard>) postcards);
                    } else {
                        loadMore = false;
                    }
                } else {
                    Log.d(TAG, "I can't get the postcards..." + e.getMessage());
                }
            }
        });
    }

    private void setupSwipeToRefresh() {
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                limit = 0;
                loadMorePostcards();
            }
        });
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // ********************************************************
    // **        HELPER METHODS FOR CLICK TO DETAIL VIEW     **
    // ********************************************************

    public interface GoToDetailViewListener {
        public void goToDetailView(Postcard postcard);
    }

    /**
     * Set the action bar to have the appropriate title and icons
     */
    private void configureActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("Profile"); // set the top title
    }
}