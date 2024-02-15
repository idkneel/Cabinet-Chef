package com.example.cabinetchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class Home_screen extends AppCompatActivity {

    private PopupWindow screenSelectWindow;
    private PopupWindow filtersWindow;
    private PopupWindow mealTimesWindow;
    private PopupWindow cookingDifficultyWindow;
    private View cookingDifficultyPopupView;
    private View screenSelectView;
    private View filtersPopupView;
    private View mealTimesPopupView;
    private static final int REQUEST_MEAL_TIME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        // Initialize popupWindow and popupView
        screenSelectView = LayoutInflater.from(this).inflate(R.layout.screen_select_popup, null);
        screenSelectWindow = new PopupWindow(
                screenSelectView,
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
        mealTimesPopupView = LayoutInflater.from(this).inflate(R.layout.meal_time_popup, null);
        mealTimesWindow = new PopupWindow(
                mealTimesPopupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        cookingDifficultyPopupView = LayoutInflater.from(this).inflate(R.layout.cooking_difficulty_popup, null);
        cookingDifficultyWindow = new PopupWindow(
                cookingDifficultyPopupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Customize popupWindow appearance and behavior
        filtersWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        filtersWindow.setBackgroundDrawable(null);

        // Show popup when the button is clicked
        Button showScreenSelectButton = findViewById(R.id.showPopupButton);
        Button showFilterPopupButton = findViewById(R.id.showFiltersButton);

        showScreenSelectButton.setOnClickListener(v -> showScreenSelectPopup());
        showFilterPopupButton.setOnClickListener(v -> showFilterPopup());
    }

    private void showScreenSelectPopup() {
        // Get the root view of the current activity
        View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);

        Button profileButton = screenSelectView.findViewById(R.id.profile);
        Button favoritesButton = screenSelectView.findViewById(R.id.Favorites);
        Button pantryButton = screenSelectView.findViewById(R.id.Pantry);
        Button utensilsButton = screenSelectView.findViewById(R.id.Utensils);
        Button settingsButton = screenSelectView.findViewById(R.id.Settings);

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Home_screen.this, ProfileScreen.class);
            startActivity(intent);
            screenSelectWindow.dismiss(); // Dismiss the popup after navigating
        });
        favoritesButton.setOnClickListener(v -> {
            Intent intent = new Intent(Home_screen.this, FavoritesScreen.class);
            startActivity(intent);
            screenSelectWindow.dismiss(); // Dismiss the popup after navigating
        });
        pantryButton.setOnClickListener(v -> {
            Intent intent = new Intent(Home_screen.this, Pantry.class);
            startActivity(intent);
            screenSelectWindow.dismiss();
        });
        utensilsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Home_screen.this, UtensilsScreen.class);
            startActivity(intent);
            screenSelectWindow.dismiss();
        });
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Home_screen.this, Settings.class);
            startActivity(intent);
            screenSelectWindow.dismiss();
        });

        // Calculate the width and height of the screen
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // Show popup on the left half of the screen
        screenSelectWindow.setWidth(width);
        screenSelectWindow.setHeight(height);
        screenSelectWindow.showAtLocation(rootView, Gravity.LEFT, 0, 0);
    }
    private void showFilterPopup() {
        // Get the root view of the current activity
        View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);

        Button cookingDifficultyPopup = filtersPopupView.findViewById(R.id.Cooking_difficulty);
        Button mealTimePopup = filtersPopupView.findViewById(R.id.meal_time);
        mealTimePopup.setOnClickListener(v -> {
            showMealTimePopup();
            filtersWindow.dismiss();
        });

        cookingDifficultyPopup.setOnClickListener(v -> {
            showCookingDifficultyPopup();
            filtersWindow.dismiss();
        });

        // Calculate the width and height of the screen
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // Show popup on the left half of the screen
        filtersWindow.setWidth(width);
        filtersWindow.setHeight(height);
        filtersWindow.showAtLocation(rootView, Gravity.RIGHT, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEAL_TIME && resultCode == RESULT_OK && data != null) {
            String selectedMealTime = data.getStringExtra("mealTime");
            // Use the selected meal time to filter RecyclerView data
            // Update RecyclerView adapter accordingly

            //when recycler view gets set up for meal times, input following 3 lines
                // Inside onActivityResult method
                // Assuming you have a RecyclerView and its adapter
                //adapter.filterDataByMealTime(selectedMealTime);
        }
    }

    private void showMealTimePopup() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.filter_options_popup, null);

        Button mealBackButton = mealTimesPopupView.findViewById(R.id.backButton);
        Button itemToBeUsedButton = mealTimesPopupView.findViewById(R.id.Item_preferences);

        mealBackButton.setOnClickListener(v -> {
            showFilterPopup();
            mealTimesWindow.dismiss();
        });
//        itemToBeUsedButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //itemToBeUsedButton start actvitiy goes here
//            }
//        });

        // Show the meal time popup
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // Show popup on the left half of the screen
        mealTimesWindow.setWidth(width);
        mealTimesWindow.setHeight(height);
        mealTimesWindow.showAtLocation(rootView, Gravity.RIGHT, 0, 0);
    }

    private void showCookingDifficultyPopup(){
        View rootView = LayoutInflater.from(this).inflate(R.layout.filter_options_popup, null);

        Button cookingDifficultyBackButton = cookingDifficultyPopupView.findViewById(R.id.backButton);

        cookingDifficultyBackButton.setOnClickListener(v -> {
            showFilterPopup();
            cookingDifficultyWindow.dismiss();
        });

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // Show popup on the left half of the screen
        cookingDifficultyWindow.setWidth(width);
        cookingDifficultyWindow.setHeight(height);
        cookingDifficultyWindow.showAtLocation(rootView, Gravity.RIGHT, 0, 0);
    }




}

