package com.example.thepostcardproject.activities;

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
import com.example.thepostcardproject.databinding.ActivityLoginBinding;
import com.example.thepostcardproject.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupLogin();
        setupSignup();
    }

    // *****************************************
    // **      ADD LISTENERS TO BUTTONS       **
    // *****************************************

    /**
     * Sets up the login button by adding an onclick listener to sign in a user
     */
    private void setupLogin() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                loginUser(username, password);
            }
        });
    }

    /**
     * Sets up the sign up button by adding an onclick listener to sign up a new user
     */
    private void setupSignup() {
        SpannableString content = new SpannableString("create an account");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        binding.tvSignup.setText(content);
        binding.tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSignupActivity();
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

    /**
     * Uses an intent to start the MainActivity
     */
    private void goSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    // *****************************************
    // **    HELPER METHODS FOR LOGGING IN    **
    // *****************************************

    /**
     * Logs in the ParseUser with the associated username and password
     * @param username The provided username
     * @param password The provided password
     */
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    goMainActivity();
                } else {
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(LoginActivity.this, "Wrong password. Try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}