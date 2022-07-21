package com.example.thepostcardproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.ActivityMainBinding;
import com.example.thepostcardproject.fragments.CreateFragment;
import com.example.thepostcardproject.fragments.HomeBackdropFragment;
import com.example.thepostcardproject.fragments.MapFragment;
import com.example.thepostcardproject.fragments.PostcardDetailFragment;
import com.example.thepostcardproject.fragments.ProfileFragment;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    FragmentManager fragmentManager = getSupportFragmentManager();

    private ActivityMainBinding binding;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Places.initialize(getApplicationContext(), "AIzaSyAVrhwVJs0zsb_X8HcFuWBkqhp4LTIsJ2g");
        placesClient = Places.createClient(this);
        setupBottomNavigation();
//        goHomeFragment();
    }

    /**
     * Sets up the bottom navigation bar to navigate to the appropriate fragment upon clicking a navigation item
     */
    private void setupBottomNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

//        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment fragment;
//                switch (item.getItemId()) {
//                    case R.id.home_backdrop_fragment:
//                        goHomeFragment();
//                        return true;
////                    case R.id.action_map:
////                        fragment = mapFragment;
////                        break;
//                    case R.id.create_fragment:
//                        fragment = createFragment;
//                        break;
//                    case R.id.profile_fragment:
//                        goProfileFragment();
//                        return true;
//                    case R.id.login_activity:
//                        logout();
//                    default:
//                        return true;
//                }
//                fragmentManager.beginTransaction().replace(R.id.rl_container, fragment).commit();
//                return true;
//            }
//        });
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
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, homeBackdropFragment).commit();
    }

//    /**
//     * Navigates to the Profile fragment
//     */
//    private void goProfileFragment() {
//        ProfileFragment.GoToDetailViewListener goToDetailViewListener = new ProfileFragment.GoToDetailViewListener() {
//            @Override
//            public void goToDetailView(int postcardPosition) {
//                fragmentManager.beginTransaction()
//                        .replace(R.id.nav_host_fragment, PostcardDetailFragment.newInstance(postcardPosition))
//                        .addToBackStack(null)
//                        .commit();
//            }
//        };
//        final ProfileFragment profileFragment = ProfileFragment.newInstance(goToDetailViewListener);
//        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, profileFragment).commit();
//    }
////
//    public PlacesClient getPlacesClient() {
//        return placesClient;
//    }
}