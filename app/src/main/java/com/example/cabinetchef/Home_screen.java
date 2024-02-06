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

public class Home_screen extends AppCompatActivity {

    private PopupWindow popupWindow;
    private PopupWindow filtersWindow;
    private View popupView;
    private View filtersPopupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        // Initialize popupWindow and popupView
        popupView = LayoutInflater.from(this).inflate(R.layout.screen_select_popup, null);
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        filtersPopupView = LayoutInflater.from(this).inflate(R.layout.filter_options_popup, null);
        filtersWindow = new PopupWindow(
                filtersPopupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Customize popupWindow appearance and behavior
        filtersWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        filtersWindow.setBackgroundDrawable(null);

        // Show popup when the button is clicked
        Button showScreenSelectButton = findViewById(R.id.showPopupButton);
        showScreenSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScreenSelectPopup();
            }
        });
        Button showFilterPopupButton = findViewById(R.id.showFiltersButton);
        showFilterPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopup();
            }
        });
    }

    private void showScreenSelectPopup() {
        // Get the root view of the current activity
        View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);

        Button profileButton = popupView.findViewById(R.id.profile);
        Button favoritesButton = popupView.findViewById(R.id.Favorites);
        Button pantryButton = popupView.findViewById(R.id.Pantry);
        Button utensilsButton = popupView.findViewById(R.id.Utencils);
        Button settingsButton = popupView.findViewById(R.id.Settings);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_screen.this, ProfileScreen.class);
                startActivity(intent);
                popupWindow.dismiss(); // Dismiss the popup after navigating
            }
        });
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_screen.this, FavoritesScreen.class);
                startActivity(intent);
                popupWindow.dismiss(); // Dismiss the popup after navigating
            }
        });
        pantryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_screen.this, Pantry.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        utensilsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_screen.this, UtensilsScreen.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_screen.this, Settings.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });

        // Calculate the width and height of the screen
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // Show popup on the left half of the screen
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.showAtLocation(rootView, Gravity.LEFT, 0, 0);
    }
    private void showFilterPopup() {
        // Get the root view of the current activity
        View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);

        // Calculate the width and height of the screen
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // Show popup on the left half of the screen
        filtersWindow.setWidth(width);
        filtersWindow.setHeight(height);
        filtersWindow.showAtLocation(rootView, Gravity.RIGHT, 0, 0);
    }

}

