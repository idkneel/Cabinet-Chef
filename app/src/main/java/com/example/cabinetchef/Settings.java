package com.example.cabinetchef;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetchef.Login.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// This class represents the Settings activity
public class Settings extends AppCompatActivity {

    // Flag to track whether dark mode is enabled or not
    private boolean isDarkModeEnabled = false; //Default is light mode

    // Views
    TextView deleteAccount;
    AlertDialog confirmDialog;
    Button themeToggleButton;

    // Method called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen); // Set the layout for this activity

        // Find views from the layout
        Button logout_button = findViewById(R.id.logout);
        Button backButton = findViewById(R.id.backButton);

        // Set OnClickListener for the back button to finish the activity
        backButton.setOnClickListener(v -> {
            finish(); // Close the current activity and go back
        });

        // Setting an onClickListener for the logout button
        logout_button.setOnClickListener(view -> {
            AlertDialog.Builder logoutBuilder = new AlertDialog.Builder(Settings.this);
            View logoutDialogView = getLayoutInflater().inflate(R.layout.are_you_sure_popup, null);

            logoutBuilder.setView(logoutDialogView);
            AlertDialog logoutDialog = logoutBuilder.create();

            logoutDialogView.findViewById(R.id.confirmYes).setOnClickListener(v -> {
                // Signing out the user
                FirebaseAuth.getInstance().signOut();
                // Redirecting to the Login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                // Finishing the current activity
                finish();
            });

            logoutDialogView.findViewById(R.id.confirmNo).setOnClickListener(v -> logoutDialog.findViewById(R.id.confirmNo).setOnClickListener(v2 -> logoutDialog.dismiss()));
            if (logoutDialog.getWindow() != null) {
                logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                logoutDialog.dismiss();
            }

            logoutDialog.show();
        });

        // Initialize deleteAccount TextView and set OnClickListener
        deleteAccount = findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
            View dialogView = getLayoutInflater().inflate(R.layout.delete_account_dialogue, null);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            dialogView.findViewById(R.id.btnConfirm).setOnClickListener(v1 -> {
                if(confirmDialog == null){
                    AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(Settings.this);
                    View confirmPopup = getLayoutInflater().inflate(R.layout.are_you_sure_popup, null);

                    confirmBuilder.setView(confirmPopup);
                    confirmDialog = confirmBuilder.create();

                    confirmPopup.findViewById(R.id.confirmYes).setOnClickListener(v11 -> {
                        assert user != null;
                        userDelete(dialog, user);
                    });
                    confirmPopup.findViewById(R.id.confirmNo).setOnClickListener(v112 -> confirmDialog.findViewById(R.id.confirmNo).setOnClickListener(v12 -> confirmDialog.dismiss()));
                    if (confirmDialog.getWindow() != null) {
                        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }
                }

                confirmDialog.show();
            });

            dialogView.findViewById(R.id.btnCancel).setOnClickListener(v13 -> dialog.dismiss());

            // Ensure the dialog window is not null, then set its background to transparent
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            // Display the dialog to the user
            dialog.show();
        });

        // Find the theme toggle button from the layout and set OnClickListener
        themeToggleButton = findViewById(R.id.ColorModePreference);
        themeToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTheme(); // Toggle the theme when the button is clicked
            }
        });
    }

    // Method to toggle between light and dark theme
    private void toggleTheme(){
        isDarkModeEnabled = !isDarkModeEnabled; // Toggle the flag

        if (isDarkModeEnabled){
            setTheme(R.style.DarkTheme); // Apply dark mode theme
        } else {
            setTheme(R.style.LightTheme); // Apply light mode theme
            // Update button icon if needed
        }

        // Recreate the activity to apply the new theme
        recreate();
    }

    // Method to handle user account deletion
    private void userDelete(AlertDialog dialog, FirebaseUser user) {
        // Initiates the deletion process for the current user
        // Attaches a listener that will respond once the delete operation is complete
        user.delete()
                .addOnCompleteListener(task -> { // This method is called when the delete operation completes
                    if (task.isSuccessful()) { // Checks if the delete operation was successful
                        Log.d(TAG, "User account deleted."); // Logs a message indicating the user was successfully deleted
                        // Redirects the user to the Login activity after successful deletion
                        startActivity(new Intent(Settings.this, Login.class));
                        dialog.dismiss(); // Dismisses the alert dialog if it was showing
                    } else {
                        // Shows a toast message to the user indicating the delete operation failed
                        Toast.makeText(Settings.this, "Delete Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
