    package com.example.cabinetchef;

    import static android.content.ContentValues.TAG;

    import android.content.Intent;
    import android.graphics.drawable.ColorDrawable;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
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

        FirebaseAuth authProfile;
        TextView deleteAccount;
        AlertDialog confirmDialog;

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

            deleteAccount = findViewById(R.id.deleteAccount);
            deleteAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.delete_account_dialogue, null);

                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    dialogView.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if(confirmDialog == null){
                                AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(Settings.this);
                                View confirmPopup = getLayoutInflater().inflate(R.layout.are_you_sure_popup, null);

                                confirmBuilder.setView(confirmPopup);
                                confirmDialog = builder.create();

                                confirmPopup.findViewById(R.id.confirmYes).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        assert user != null;
                                        userDelete(dialog, user);
                                    }
                                });
                                confirmPopup.findViewById(R.id.confirmNo).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        confirmDialog.findViewById(R.id.confirmNo).setOnClickListener(v12 -> confirmDialog.dismiss());
                                    }
                                });
                                if (confirmDialog.getWindow() != null) {
                                    confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                                }
                            }

                            confirmDialog.show();
                            }

                    });

                    dialogView.findViewById(R.id.btnCancel).setOnClickListener(v12 -> dialog.dismiss());

                    // Ensure the dialog window is not null, then set its background to transparent
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }

                    // Display the dialog to the user
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
