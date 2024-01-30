package com.example.cabinetchef.Login;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetchef.MainActivity;
import com.example.cabinetchef.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login  extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
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

        // Set a click listener for the "Register Now" TextView
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to the Register activity
                Intent intent = new Intent(getApplicationContext(), Register.class);

                // Start the Register activity using the created Intent
                startActivity(intent);

                // Finish the current activity to prevent going back to the login screen
                finish();
            }
        });

        // Set a click listener for the Login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
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
                            }
                        });
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                View dialogView = getLayoutInflater().inflate(R.layout.forgot_password_dialogue, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialog.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();

                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            Toast.makeText(Login.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Login.this,"Check your email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(Login.this, "", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
    }
}
