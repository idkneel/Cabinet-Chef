package com.example.cabinetchef.Login;

// Import statements for Android and Firebase components
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

// Register activity class definition
public class Register extends AppCompatActivity {

    // Declaration of UI components and Firebase authentication instance
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    // Method called when activity is starting
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is already logged in and redirect to MainActivity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Method called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the content view to the register activity layout
        setContentView(R.layout.activity_register);

        // Initializing FirebaseAuth instance and UI components
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.button_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        // Set an onClickListener for the textView to redirect to the Login activity
        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // Set an onClickListener for the register button to initiate the registration process
        buttonReg.setOnClickListener(view -> {
            // Show the progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Retrieve email and password from text inputs
            String email, password;
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());

            // Check if email or password fields are empty and display a message if they are
            if (TextUtils.isEmpty(email)){
                Toast.makeText(Register.this,"Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)){
                Toast.makeText(Register.this,"Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Attempt to create a user with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        // Hide the progress bar after attempt
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Display success message and redirect to MainActivity
                            Toast.makeText(Register.this, "Account created.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (password.length() < 6) {
                            // Check if the password is too short and display a message
                            Toast.makeText(Register.this, "Password is too short",
                                    Toast.LENGTH_SHORT).show();
                        } else{
                            // Display a message if the authentication failed
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}