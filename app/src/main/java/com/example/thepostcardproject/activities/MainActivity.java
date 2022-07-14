package com.example.thepostcardproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.ActivityMainBinding;
import com.example.thepostcardproject.fragments.CreateFragment;
import com.example.thepostcardproject.fragments.HomeBackdropFragment;
import com.example.thepostcardproject.fragments.HomeFragment;
import com.example.thepostcardproject.fragments.MapFragment;
import com.example.thepostcardproject.fragments.PostcardDetailFragment;
import com.example.thepostcardproject.fragments.ProfileFragment;
import com.example.thepostcardproject.models.Postcard;
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
    FragmentManager fragmentManager = getSupportFragmentManager();

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setupBottomNavigation();
        goHomeFragment();

        Places.initialize(getApplicationContext(), "AIzaSyAVrhwVJs0zsb_X8HcFuWBkqhp4LTIsJ2g");
        PlacesClient placesClient = Places.createClient(this);
    }

    /**
     * Sets up the bottom navigation bar to navigate to the appropriate fragment upon clicking a navigation item
     */
    private void setupBottomNavigation() {

        final MapFragment mapFragment = new MapFragment();
        final CreateFragment createFragment = new CreateFragment();

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        goHomeFragment();
                        return true;
                    case R.id.action_map:
                        fragment = mapFragment;
                        break;
                    case R.id.action_create:
                        fragment = createFragment;
                        break;
                    case R.id.action_profile:
                        goProfileFragment();
                        return true;
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
        HomeBackdropFragment homeBackdropFragment = new HomeBackdropFragment();
        fragmentManager.beginTransaction().replace(R.id.rl_container, homeBackdropFragment).commit();
    }

    /**
     * Navigates to the Profile fragment
     */
    private void goProfileFragment() {
        ProfileFragment.GoToDetailViewListener goToDetailViewListener = new ProfileFragment.GoToDetailViewListener() {
            @Override
            public void goToDetailView(Postcard postcard) {
                fragmentManager.beginTransaction()
                        .replace(R.id.rl_container, PostcardDetailFragment.newInstance(postcard))
                        .addToBackStack(null)
                        .commit();
            }
        };
        final ProfileFragment profileFragment = ProfileFragment.newInstance(goToDetailViewListener);
        fragmentManager.beginTransaction().replace(R.id.rl_container, profileFragment).commit();
    }
}