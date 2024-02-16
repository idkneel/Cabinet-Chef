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

        FirebaseAuth authProfile;
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

                                userDelete(dialog, user);
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

        private void userDelete(AlertDialog dialog, FirebaseUser user) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User account deleted.");
                                startActivity(new Intent(Settings.this, Login.class));
                                dialog.dismiss();
                            } else {
                                Toast.makeText(Settings.this, "Delete Failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
