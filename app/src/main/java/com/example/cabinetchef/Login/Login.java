package com.example.cabinetchef.Login;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetchef.MainActivity;
import com.example.cabinetchef.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login  extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    CheckBox hidePassword;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView, forgotPassword;

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
        forgotPassword = findViewById(R.id.forgot_password);
        hidePassword = findViewById(R.id.hide_password);

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
                            Toast.makeText(Login.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Set an onClickListener for the forgotPassword TextView
        forgotPassword.setOnClickListener(v -> {
            // Create a new AlertDialog builder with the current context
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            // Inflate the custom layout for the forgot password dialog
            View dialogView = getLayoutInflater().inflate(R.layout.forgot_password_dialogue, null);
            // Find the EditText within the dialog for the user to enter their email
            EditText emailBox = dialogView.findViewById(R.id.emailBox);

            // Set the custom layout as the view for the dialog
            builder.setView(dialogView);
            // Create an AlertDialog instance based on the builder
            AlertDialog dialog = builder.create();

            // Set an onClickListener for the "Reset" button within the dialog
            dialogView.findViewById(R.id.btnReset).setOnClickListener(v1 -> {
                // Extract the text entered by the user as their email
                String userEmail = emailBox.getText().toString();

                // Validate the entered email. Show a toast message if it's empty or not a valid email format
                if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    Toast.makeText(Login.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Send a password reset email to the entered email address
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    // Check if the request to send a reset email was successful
                    if (task.isSuccessful()){
                        // Notify the user to check their email
                        Toast.makeText(Login.this, "Check your email", Toast.LENGTH_SHORT).show();
                        // Dismiss the dialog upon successful email submission
                        dialog.dismiss();
                    }else {
                        // Notify the user if there was an error sending the reset email
                        Toast.makeText(Login.this, "Unable to send email", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            // Set an onClickListener for the "Cancel" button in the dialog, which simply dismisses the dialog
            dialogView.findViewById(R.id.btnCancel).setOnClickListener(v12 -> dialog.dismiss());

            // Ensure the dialog window is not null, then set its background to transparent
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            // Display the dialog to the user
            dialog.show();
        });

        hidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }
}