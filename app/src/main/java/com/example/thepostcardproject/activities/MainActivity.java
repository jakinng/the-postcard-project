package com.example.thepostcardproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.fragments.CreateFragment;
import com.example.thepostcardproject.fragments.HomeFragment;
import com.example.thepostcardproject.fragments.MapFragment;
import com.example.thepostcardproject.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupBottomNavigation();
    }

    private void setupViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
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

    private void logout() {
        ParseUser.logOut();
        goLoginActivity();
    }

    private void goLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}