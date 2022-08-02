package com.example.thepostcardproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.ActivityMainBinding;
import com.example.thepostcardproject.fragments.CreateFragment;
import com.example.thepostcardproject.fragments.HomeBackdropFragment;
import com.example.thepostcardproject.fragments.MapFragment;
import com.example.thepostcardproject.fragments.ProfileFragment;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Places.initialize(getApplicationContext(), "AIzaSyAVrhwVJs0zsb_X8HcFuWBkqhp4LTIsJ2g");
        setupBottomNavigation();
    }

    /**
     * Sets up the bottom navigation bar to navigate to the appropriate fragment upon clicking a navigation item
     */
    private void setupBottomNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

//    private void setupBottomNavigationFragment() {
//        final FragmentManager fragmentManager = getSupportFragmentManager();
//
//        // define your fragments here
//        final Fragment homeBackdropFragment = new HomeBackdropFragment();
//        final Fragment createFragment = new CreateFragment();
//        final Fragment profileFragment = new ProfileFragment();
//        final Fragment mapFragment = new MapFragment();
//
//        binding.bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment fragment;
//                switch (item.getItemId()) {
//                    case R.id.create_fragment:
//                        fragment = createFragment;
//                        break;
//                    case R.id.profile_fragment:
//                        fragment = profileFragment;
//                        break;
////                    case R.id.map_fragment:
////                        fragment = mapFragment;
////                        break;
//                    case R.id.login_activity:
//                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                        ParseUser.logOut();
//                        finish();
//                        startActivity(intent);
//                    default:
//                        fragment = homeBackdropFragment;
//                        break;
//                }
//                fragmentManager.beginTransaction().replace(R.id.rl_container, fragment).commit();
//                return true;
//            }
//        });
//        // set default selection
//        binding.bottomNavigation.setSelectedItemId(R.id.home_backdrop_fragment);
//    }
}