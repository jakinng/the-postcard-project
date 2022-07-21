package com.example.thepostcardproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.ActivityMainBinding;
import com.google.android.libraries.places.api.Places;

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
}