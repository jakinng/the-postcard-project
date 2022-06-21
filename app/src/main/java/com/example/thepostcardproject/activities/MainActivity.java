package com.example.thepostcardproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.fragments.CreateFragment;
import com.example.thepostcardproject.fragments.HomeFragment;
import com.example.thepostcardproject.fragments.MapFragment;
import com.example.thepostcardproject.fragments.ProfileFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupBottomNavigation();
        goHomeFragment();

        Places.initialize(getApplicationContext(), "AIzaSyAVrhwVJs0zsb_X8HcFuWBkqhp4LTIsJ2g");
        PlacesClient placesClient = Places.createClient(this);
    }

    /**
     * Binds instance variables to the associated views
     */
    private void setupViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();
    }

    /**
     * Sets up the bottom navigation bar to navigate to the appropriate fragment upon clicking a navigation item
     */
    private void setupBottomNavigation() {
        final HomeFragment homeFragment = new HomeFragment();
        final MapFragment mapFragment = new MapFragment();
        final CreateFragment createFragment = new CreateFragment();
        final ProfileFragment profileFragment = new ProfileFragment();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = homeFragment;
                        break;
                    case R.id.action_map:
                        fragment = mapFragment;
                        break;
                    case R.id.action_create:
                        fragment = createFragment;
                        break;
                    case R.id.action_profile:
                        fragment = profileFragment;
                        break;
                    case R.id.action_logout:
                        logout();
                    default:
                        return true;
                }
                fragmentManager.beginTransaction().replace(R.id.rl_container, fragment).commit();
                return true;
            }
        });
    }

    /**
     * Logs out the current Parse user and goes to the login activity
     */
    private void logout() {
        ParseUser.logOut();
        goLoginActivity();
    }

    /**
     * Uses an intent to navigate to the login activity
     */
    private void goLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Navigates to the Home fragment
     */
    private void goHomeFragment() {
        fragmentManager.beginTransaction().replace(R.id.rl_container, new HomeFragment()).commit();
    }
}