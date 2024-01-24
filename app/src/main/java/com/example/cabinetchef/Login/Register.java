package com.example.cabinetchef.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetchef.MainActivity;
import com.example.cabinetchef.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
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
        // Call the onCreate method of the superclass with the saved instance state
        super.onCreate(savedInstanceState);

        // Set the content view of the activity to the specified layout resource
        setContentView(R.layout.activity_register);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Retrieve references to UI elements
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.button_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        // Set an onClickListener for the "Login Now" TextView
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to the Login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);

                // Start the Login activity using the created Intent
                startActivity(intent);

                // Finish the current activity to prevent the user from going back to the registration screen
                finish();
            }
        });

        // Set an onClickListener for the registration button
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Display the progress bar to indicate ongoing processing
                progressBar.setVisibility(View.VISIBLE);

                // Extract email and password from the input fields
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());

                // Check if the email is empty
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this,"Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the password is empty
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this,"Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Attempt to create a new user account with the provided email and password
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Hide the progress bar after the account creation attempt is complete
                                progressBar.setVisibility(View.GONE);

                                // Check if the account creation was successful
                                if (task.isSuccessful()) {
                                    // Display a success message
                                    Toast.makeText(Register.this, "Account created.", Toast.LENGTH_SHORT).show();

                                    // Create an Intent to navigate to the MainActivity
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                    // Start the MainActivity using the created Intent
                                    startActivity(intent);

                                    // Finish the current activity to prevent the user from going back to the registration screen
                                    finish();
                                } else {
                                    // Display a failure message if account creation fails
                                    Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
