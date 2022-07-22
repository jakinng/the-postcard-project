package com.example.thepostcardproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.thepostcardproject.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = "SplashActivity";
    private ActivitySplashBinding binding;

    // Time to display in milliseconds
    private static final long SPLASH_TIME = 10000;
    private static final long SPLASH_TIME_SHORT = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configureActionBar();
        goLoginActivity();
    }
    private void configureActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    /**
     * Uses an intent to start the MainActivity after a delay
     */
    private void goLoginActivity() {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                return false;
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME);
    }
}