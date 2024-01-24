package com.example.cabinetchef;

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

import com.example.cabinetchef.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call the onCreate method of the superclass with the saved instance state
        super.onCreate(savedInstanceState);

        // Set the content view of the activity to the specified layout resource
        setContentView(R.layout.activity_main);

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Retrieve references to UI elements
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);

        // Get the currently signed-in user
        user = auth.getCurrentUser();

        // Check if a user is not signed in
        if (user == null) {
            // If not signed in, create an Intent to navigate to the Login activity
            Intent intent = new Intent(getApplicationContext(), Login.class);

            // Start the Login activity using the created Intent
            startActivity(intent);

            // Finish the current activity to prevent the user from accessing the main activity without authentication
            finish();
        } else {
            // If signed in, set the TextView to display the user's email
            textView.setText(user.getEmail());
        }

        // Set an onClickListener for the logout button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out the current user
                FirebaseAuth.getInstance().signOut();

                // Create an Intent to navigate to the Login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);

                // Start the Login activity using the created Intent
                startActivity(intent);

                // Finish the current activity to prevent the user from going back to the main activity without re-authentication
                finish();
            }
        });
    }
}