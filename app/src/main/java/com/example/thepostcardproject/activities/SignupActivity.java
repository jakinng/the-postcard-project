package com.example.thepostcardproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }



//    @Override
//    public void onClick(View v) {
//        String username = etUsername.getText().toString();
//        String password = etPassword.getText().toString();
//        signupUser(username, password);
//    }

    /**
     * Signs up a new ParseUser with the associated username and password
     * @param username The provided username
     * @param password The provided password
     */
    private void signupUser(String username, String password) {
        ParseUser user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    goMainActivity();
                } else {
                    // TODO : better error handling
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(SignupActivity.this, "This username already exists. Try again!", Toast.LENGTH_SHORT).show();
                }
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
}