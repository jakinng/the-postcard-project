package com.example.thepostcardproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupViews();
        setupLogin();
        setupSignup();
    }

    /**
     * Binds all the views in the XML file to the instance variables for the login activity
     */
    private void setupViews() {
        etUsername = (TextInputEditText) findViewById(R.id.et_username);
        etPassword = (TextInputEditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
    }

    /**
     * Sets up the login button by adding an onclick listener to sign in a user
     */
    private void setupLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });
    }

    /**
     * Sets up the sign up button by adding an onclick listener to sign up a new user
     */
    private void setupSignup() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signupUser(username, password);
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

    // ********************************************************
    // **    HELPER METHODS FOR LOGGING IN AND SIGNING UP    **
    // ********************************************************

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
                    Toast.makeText(LoginActivity.this, "Wrong password. Try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Signs up a new ParseUser with the associated username and password
     * @param username The provided username
     * @param password The provided password
     */
    private void signupUser(String username, String password) {
//        Log.d(TAG, "hii");
        ParseUser user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    goMainActivity();
                } else {
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(LoginActivity.this, "This username already exists. Try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}