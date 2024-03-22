package com.example.cabinetchef;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetchef.Enums.UserSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// ProfileScreen.java
public class ProfileScreen extends AppCompatActivity {

    UserSettings uSettings;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen); // Set the layout for the profile screen
        // Add any additional logic for the profile screen if needed
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish(); // Close the current activity and go back
        });
        // household members button handling
        Button householdMembersButton = findViewById(R.id.household_members);
        householdMembersButton.setOnClickListener(v -> showNumberSelectorPopup());

        Button allergiesButton = findViewById(R.id.allergies_page);
        allergiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileScreen.this, AllergiesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showNumberSelectorPopup() {
        @SuppressLint("InflateParams") View popupView = LayoutInflater.from(this).inflate(R.layout.household_member_count_popup, null);
        PopupWindow numberSelectorPopup = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Get numberPicker and selectButton from the popupView
        NumberPicker numberPicker = popupView.findViewById(R.id.numberPicker);
        Button selectButton = popupView.findViewById(R.id.selectButton);

        // Set numberPicker properties
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(20);

        numberPicker.setValue(1); // Initial value

        // Set OnClickListener for the selectButton
        selectButton.setOnClickListener(v -> {
            // Get the selected number
            int selectedNumber = numberPicker.getValue();

            // TODO: Handle the selected number (e.g., update UI)

            uSettings = new UserSettings();

            updateData(uSettings.uHousehold, String.valueOf(selectedNumber));

            // Dismiss the popup
            numberSelectorPopup.dismiss();
        });

        // Show the popup
        numberSelectorPopup.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private void updateData(String oldNumber, String selectedNumber) {
        // Create a map to hold the update, in this case, the new "Household members" value
        Map<String, Object> userDetail = new HashMap<>();
        userDetail.put("Household members", selectedNumber);

        // Initialize Firestore instance
        fStore = FirebaseFirestore.getInstance();

        // Get the current user's ID from FirebaseAuth (assuming user is logged in)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the specific user's document in the "users" collection
        DocumentReference userDocRef = fStore.collection("users").document(userId);

        // Try to get the document for the current user
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // If the document exists, update it with the new details
                    userDocRef.update(userDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Success: Show a success message
                            Toast.makeText(ProfileScreen.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failure: Show an error message
                            Toast.makeText(ProfileScreen.this, "Error updating document", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // If the document does not exist (fallback scenario),
                    // attempt to find the document by "Household members" field and old number
                    fStore.collection("users")
                            .whereEqualTo("Household members", oldNumber)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        // If successful and documents are found,
                                        // get the first document and its ID
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        String documentID = documentSnapshot.getId();
                                        // Update the found document with the new details
                                        fStore.collection("users")
                                                .document(documentID)
                                                .update(userDetail)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        // Success: Show a success message
                                                        Toast.makeText(ProfileScreen.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Failure: Show an error message
                                                        Toast.makeText(ProfileScreen.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        // If no documents found by "Household members",
                                        // show a failure message
                                        Toast.makeText(ProfileScreen.this, "Failed to find document by Household members", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If there was an error accessing the document, show an error message
                Toast.makeText(ProfileScreen.this, "Error accessing document", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
