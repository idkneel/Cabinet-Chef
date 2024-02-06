package com.example.cabinetchef;

// Import statements
import static com.example.cabinetchef.R.id.home_screen_button;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.example.cabinetchef.Login.Login;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

// MainActivity class definition
public class MainActivity extends AppCompatActivity {

    // Firebase authentication object
    FirebaseAuth auth;

    // TextView for displaying user details
    TextView textView;
    // FirebaseUser object for the current user
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the content view to the layout of the main activity
        setContentView(R.layout.activity_main);

        // Initializing the FirebaseAuth instance
        auth = FirebaseAuth.getInstance();
        // Finding the logout button by its ID
        Button logout_button = findViewById(R.id.logout);
        // Finding the TextView for user details by its ID
        textView = findViewById(R.id.user_details);
        // Getting the current user from FirebaseAuth
        user = auth.getCurrentUser();

        // Checking if the user is null (i.e., not logged in)
        if (user == null) {
            // If user is not logged in, redirect to the Login activity
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            // Finishing the current activity to remove it from the activity stack
            finish();
        } else {
            // If the user is logged in, display the user's email in the textView
            textView.setText(user.getEmail());
        }
        // Setting an onClickListener for the logout button
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // Signing out the user
                FirebaseAuth.getInstance().signOut();
                // Redirecting to the Login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                // Finishing the current activity
                finish();
            }
        });

        // Finding the home screen button by its ID and setting its onClickListener
        Button homeScreenButton = findViewById(R.id.home_screen_button);
        homeScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to navigate to the Home_screen activity
                Intent intent = new Intent(MainActivity.this, Home_screen.class);
                startActivity(intent);
            }
        });
    }
}