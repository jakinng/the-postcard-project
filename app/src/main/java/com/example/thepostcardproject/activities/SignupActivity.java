package com.example.thepostcardproject.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.ActivitySignupBinding;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.User;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.Arrays;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1000;

    private ActivitySignupBinding binding;

    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Places.initialize(getApplicationContext(), "AIzaSyAVrhwVJs0zsb_X8HcFuWBkqhp4LTIsJ2g");
        PlacesClient placesClient = Places.createClient(this);

        setupLocation();
        setupSignup();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Add the selected location as the current location
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            handleLocationSelected(resultCode, data);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // **********************************************
    // **     DEAL WITH SIGNING UP A NEW USER      **
    // **********************************************

    /**
     * When the sign up button is clicked, sign up a new user with the provided information
     */
    private void setupSignup() {
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                String name = binding.etName.getText().toString();
                // Make sure required fields are filled out
                if (username == null || password == null || name == null || currentLocation == null) {
                    Snackbar.make(binding.btnSignup, "Please fill out all the fields!", Snackbar.LENGTH_SHORT).show();
                } else {
                    // Sign up the user with the provided information
                    signupUser(username, password, name, currentLocation);
                }
            }
        });
    }

    /**
     * Signs up a new ParseUser with the associated username and password
     * @param username The provided username
     * @param password The provided password
     */
    private void signupUser(String username, String password, String name, Location location) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        location.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(TAG, "location has been saved!");
                user.setCurrentLocation(location);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            goMainActivity();
                        } else {
                            // TODO : better error handling
                            Log.d(TAG, e.getMessage());
                            Toast.makeText(SignupActivity.this, "Error signing up. Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * Uses an intent to start the MainActivity
     */
    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // ***********************************************************
    // **     HANDLE SELECTING LOCATION USING AUTOCOMPLETE      **
    // ***********************************************************

    /**
     * If the resultCode is OK, saves the selected location as the current location
     * @param resultCode The result code indicating whether the intent went through successfully
     */
    private void handleLocationSelected(int resultCode, @Nullable Intent data) {
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
    }

    /**
     * Saves the provided Place as a Location and updates the currentLocation of the current user
     * @param place The Place to save as the new location
     */
    private void saveNewLocation(Place place) {
        ParseGeoPoint coordinates = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
        Location newLocation = new Location(place.getName(), place.getAddress(), coordinates);
        binding.etLocation.setText(place.getName());
        currentLocation = newLocation;
    }

    /**
     * Allow user to select location when the location edittext is clicked
     */
    private void setupLocation() {
        binding.etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sets which fields the API request is asking for
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(SignupActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }


}