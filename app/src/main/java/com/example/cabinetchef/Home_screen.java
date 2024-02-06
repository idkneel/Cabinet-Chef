package com.example.cabinetchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

// Defines the Home_screen class that extends AppCompatActivity for basic Android activity behavior
public class Home_screen extends AppCompatActivity {

    // Declaration of PopupWindow objects for different UI elements
    private PopupWindow popupWindow;
    private PopupWindow filtersWindow;

    // Declaration of View objects for inflating layout resources
    private View popupView;
    private View filtersPopupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the content view to the home_screen layout
        setContentView(R.layout.home_screen);

        // Inflating and initializing the popup views for screen selection and filter options
        popupView = LayoutInflater.from(this).inflate(R.layout.screen_select_popup, null);
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // Focusable
        );
        filtersPopupView = LayoutInflater.from(this).inflate(R.layout.filter_options_popup, null);
        filtersWindow = new PopupWindow(
                filtersPopupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // Focusable
        );

        // Setting animation style for the filters window to use default dialog animations
        filtersWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // Setting the background to null for the filters window
        filtersWindow.setBackgroundDrawable(null);

        // Finding and setting up the button to show the screen selection popup
        Button showScreenSelectButton = findViewById(R.id.showPopupButton);
        showScreenSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScreenSelectPopup(); // Call method to show the screen selection popup
            }
        });
        // Finding and setting up the button to show the filters popup
        Button showFilterPopupButton = findViewById(R.id.showFiltersButton);
        showFilterPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopup(); // Call method to show the filter popup
            }
        });
    }

    // Method to show the screen selection popup
    private void showScreenSelectPopup() {
        // Inflating the root view of the home_screen layout for use in showing the popup
        View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);

        // Finding buttons within the popupView and setting their onClick listeners to launch different activities
        Button profileButton = popupView.findViewById(R.id.profile);
        Button favoritesButton = popupView.findViewById(R.id.Favorites);
        Button pantryButton = popupView.findViewById(R.id.Pantry);
        Button utensilsButton = popupView.findViewById(R.id.Utencils);
        Button settingsButton = popupView.findViewById(R.id.Settings);

        // Set up onClick listeners for each button to start different activities and dismiss the popup
        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(Home_screen.this, ProfileScreen.class));
            popupWindow.dismiss();
        });
        favoritesButton.setOnClickListener(v -> {
            startActivity(new Intent(Home_screen.this, FavoritesScreen.class));
            popupWindow.dismiss();
        });
        pantryButton.setOnClickListener(v -> {
            startActivity(new Intent(Home_screen.this, Pantry.class));
            popupWindow.dismiss();
        });
        utensilsButton.setOnClickListener(v -> {
            startActivity(new Intent(Home_screen.this, UtensilsScreen.class));
            popupWindow.dismiss();
        });
        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(Home_screen.this, Settings.class));
            popupWindow.dismiss();
        });

        // Setting the width and height for the popup window and displaying it on the screen
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.showAtLocation(rootView, Gravity.LEFT, 0, 0);
    }

    // Method to show the filter popup
    private void showFilterPopup() {
        // Inflating the root view of the home_screen layout for use in showing the popup
        View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);

        // Setting the width and height for the filters window and displaying it on the screen
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        filtersWindow.setWidth(width);
        filtersWindow.setHeight(height);
        filtersWindow.showAtLocation(rootView, Gravity.RIGHT, 0, 0);
    }
}