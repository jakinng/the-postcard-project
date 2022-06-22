package com.example.thepostcardproject.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static com.example.thepostcardproject.utilities.Keys.KEY_USER_FROM;
import static com.example.thepostcardproject.utilities.Keys.KEY_USER_TO;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.adapters.ProfilePostcardAdapter;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.models.User;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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
    private static int AUTOCOMPLETE_REQUEST_CODE = 1000;

    User currentUser;
    ArrayList<Postcard> sentPostcards;
    ProfilePostcardAdapter adapter;

    ImageView ivLocationIcon;
    RecyclerView rvPostcards;
    ImageView ivProfile;
    TextView tvUsername;
    TextView tvLocation;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    /**
     * Called shortly after onCreateView
     * @param view The view containing the various visual components
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        displayUsername();
        displayUserLocation();
        displaySentPostcards();
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

    /**
     * Attaches the views to the corresponding variables
     * @param view The encapsulating view
     */
    private void setupViews(View view) {
        ivLocationIcon = view.findViewById(R.id.iv_location_icon);
        rvPostcards = view.findViewById(R.id.rv_postcards);
        ivProfile = view.findViewById(R.id.iv_profile);
        tvUsername = view.findViewById(R.id.tv_username);
        tvLocation = view.findViewById(R.id.tv_location);
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
        tvUsername.setText("@" + currentUser.getUsername());
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
            tvLocation.setText(currentUser.getCurrentLocation().getLocationName());
        } catch (ParseException e) {
            Log.d(TAG, "The user has no location!");
            e.printStackTrace();
        }
        // Set an onclick listener on the location icon
        ivLocationIcon.setOnClickListener(new View.OnClickListener() {
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
        tvLocation.setText(place.getName());
        currentUser.saveInBackground();
    }

    // ********************************************************
    // **         HELPER METHODS FOR POSTCARD FEED           **
    // ********************************************************

    /**
     * Sets the variable sentPostcards to be the list of postcards sent by the current user
     */
    private void querySentPostcards() {
        ParseQuery<Postcard> query = ParseQuery.getQuery(Postcard.class);
        // TODO : try to use currentUser instead and see if it works
        query.whereEqualTo(KEY_USER_FROM, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Postcard>() {
            @Override
            public void done(List<Postcard> postcards, ParseException e) {
                if (e == null) {
                    if (postcards.size() == 0) {
                        Log.d(TAG, "No sent postcards yet!");
                    } else {
                        String firstItem = postcards.get(0).getMessage();
                        Log.d(TAG, "Message in first postcard: " + firstItem);
                        sentPostcards = (ArrayList<Postcard>) postcards;
                        adapter.addAll(sentPostcards);
                    }
                } else {
                    Log.d(TAG, "Error in querying for sent postcards: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Displays the postcards sent by the user
     */
    private void displaySentPostcards() {
        adapter = new ProfilePostcardAdapter(getContext(), new ArrayList<>());
        rvPostcards.setAdapter(adapter);
        rvPostcards.setLayoutManager(new GridLayoutManager(getContext(), NUM_PROFILE_COLUMNS));
        querySentPostcards();
    }
}