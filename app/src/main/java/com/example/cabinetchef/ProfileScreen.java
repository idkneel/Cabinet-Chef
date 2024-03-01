package com.example.cabinetchef;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

// ProfileScreen.java
public class ProfileScreen extends AppCompatActivity {
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

            // Dismiss the popup
            numberSelectorPopup.dismiss();
        });

        // Show the popup
        numberSelectorPopup.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }


}

