package com.example.cabinetchef.Login;

// Import statements for Android and Firebase components
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetchef.Enums.UserSettings;
import com.example.cabinetchef.MainActivity;
import com.example.cabinetchef.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// Register activity class definition
public class Register extends AppCompatActivity {

    // Declaration of UI components and Firebase authentication instance
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    FirebaseFirestore fStore;
    UserSettings uSettings;
    CheckBox hidePassword;

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

        uSettings = new UserSettings();
        // Initializing FirebaseAuth instance and UI components
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.button_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        hidePassword = findViewById(R.id.hide_password);

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
                            FirebaseUser authUser = mAuth.getCurrentUser();

                            // Display success message and redirect to MainActivity
                            Toast.makeText(Register.this, "Account created.",
                                    Toast.LENGTH_SHORT).show();

                            uSettings.userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();//
                            DocumentReference documentReference = fStore.collection("users").document(uSettings.userID);
                            Map<String,Object> user = new HashMap<>();

                            uSettings.uEmail = Objects.requireNonNull(editTextEmail.getText()).toString();
                            uSettings.uPassword = Objects.requireNonNull(editTextPassword.getText()).toString();
                            // Set the user's household size to 1 by default

                            user.put("Email", uSettings.uEmail);
                            user.put("Password", uSettings.uPassword);
                            user.put("Household members", "1");
                            user.put("Light Mode", "true");
                            user.put("Cooking difficulty", "0");
                            user.put("User ID", uSettings.userID);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"On Success: user profile is created for " + uSettings.userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: "+ e.toString());
                                }
                            });

                            // Where you designate where to go after the account has been created
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

        // Set a listener on the hidePassword checkbox to listen for check changes
        hidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // This method is called whenever the checked state of the hidePassword CompoundButton changes
                if (isChecked) {
                    // If the hidePassword button is checked, change the editTextPassword's transformation method
                    // to HideReturnsTransformationMethod to show the password
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // If the hidePassword button is not checked, change the editTextPassword's transformation method
                    // back to PasswordTransformationMethod to hide the password
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }
}