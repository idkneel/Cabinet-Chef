package com.example.cabinetchef;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class Home_screen extends AppCompatActivity {

    private PopupWindow popupWindow;
    private View popupView;

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

        // Customize popupWindow appearance and behavior
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupWindow.setBackgroundDrawable(null);

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
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.showAtLocation(rootView, Gravity.LEFT, 0, 0);
    }

}