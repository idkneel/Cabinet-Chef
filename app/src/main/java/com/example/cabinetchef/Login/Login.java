package com.example.cabinetchef.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetchef.MainActivity;
import com.example.cabinetchef.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login  extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    //idkmn
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        // Call the onStart method of the superclass (the parent class)
        super.onStart();

        // Get the current user from Firebase Authentication
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if a user is already authenticated (signed in)
        if(currentUser != null){
            // If the user is authenticated, create an Intent to navigate to the MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            // Start the MainActivity using the created Intent
            startActivity(intent);

            // Finish the current activity to prevent the user from going back to the authentication screen
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call the onCreate method of the superclass and pass the saved instance state
        super.onCreate(savedInstanceState);

        // Set the content view to the layout defined in activity_login.xml
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Find UI elements by their IDs
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.button_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);

        // Set a click listener for the "Register Now" TextView
        textView.setOnClickListener(view -> {
            // Create an Intent to navigate to the Register activity
            Intent intent = new Intent(getApplicationContext(), Register.class);

            // Start the Register activity using the created Intent
            startActivity(intent);

            // Finish the current activity to prevent going back to the login screen
            finish();
        });

        // Set a click listener for the Login button
        buttonLogin.setOnClickListener(view -> {
            // Make the progress bar visible to indicate ongoing login attempt
            progressBar.setVisibility(View.VISIBLE);

            // Get email and password from the corresponding EditText fields
            String email = String.valueOf(editTextEmail.getText());
            String password = String.valueOf(editTextPassword.getText());

            // Validate if email and password are not empty
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Use Firebase Authentication to sign in with email and password
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        // Hide the progress bar after authentication attempt is complete
                        progressBar.setVisibility(View.GONE);

                        // Check if the authentication was successful
                        if (task.isSuccessful()) {
                            // If successful, show a toast message and navigate to MainActivity
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);

                            // Finish the current activity to prevent going back to the login screen
                            finish();
                        } else {
                            // If authentication failed, show a toast message
                            Toast.makeText(Login.this, "Sign-in Failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}