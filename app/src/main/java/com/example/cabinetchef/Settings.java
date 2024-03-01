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

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;

    import com.example.cabinetchef.Login.Login;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;

    public class Settings extends AppCompatActivity {

        TextView deleteAccount;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.settings_screen);

            Button logout_button = findViewById(R.id.logout);
            Button backButton = findViewById(R.id.backButton);

            backButton.setOnClickListener(v -> {
                finish(); // Close the current activity and go back
            });

            // Setting an onClickListener for the logout button
            logout_button.setOnClickListener(view -> {
                // Signing out the user
                FirebaseAuth.getInstance().signOut();
                // Redirecting to the Login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                // Finishing the current activity
                finish();
            });

            // Locate the deleteAccount button in the layout and set a click listener on it
            deleteAccount = findViewById(R.id.deleteAccount);
            deleteAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When the deleteAccount button is clicked, build a new AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                    // Inflate the custom layout for the delete account dialog
                    View dialogView = getLayoutInflater().inflate(R.layout.delete_account_dialogue, null);

                    // Set the custom layout as the dialog view
                    builder.setView(dialogView);
                    // Create the AlertDialog from the builder
                    AlertDialog dialog = builder.create();

                    // Get the current FirebaseUser
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    // Find the confirm button in the dialog and set a click listener on it
                    dialogView.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // When the confirm button is clicked, call the userDelete method
                            // with the current dialog and user as parameters
                            userDelete(dialog, user);
                        }
                    });

                    // Find the cancel button in the dialog and set a click listener on it
                    // Use a lambda expression for a more concise syntax
                    dialogView.findViewById(R.id.btnCancel).setOnClickListener(v12 -> dialog.dismiss());

                    // Check if the dialog window is not null to avoid NullPointerException
                    if (dialog.getWindow() != null) {
                        // Set the dialog window background to transparent
                        // Useful for custom dialog designs or backgrounds
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }
                    // Finally, display the dialog to the user
                    dialog.show();
                }
            });
        }

        // Method to handle user account deletion
        private void userDelete(AlertDialog dialog, FirebaseUser user) {
            // Initiates the deletion process for the current user
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() { // Attaches a listener that will respond once the delete operation is complete
                        @Override
                        public void onComplete(@NonNull Task<Void> task) { // This method is called when the delete operation completes
                            if (task.isSuccessful()) { // Checks if the delete operation was successful
                                Log.d(TAG, "User account deleted."); // Logs a message indicating the user was successfully deleted
                                // Redirects the user to the Login activity after successful deletion
                                startActivity(new Intent(Settings.this, Login.class));
                                dialog.dismiss(); // Dismisses the alert dialog if it was showing
                            } else {
                                // Shows a toast message to the user indicating the delete operation failed
                                Toast.makeText(Settings.this, "Delete Failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
