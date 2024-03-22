package com.example.cabinetchef;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetchef.Recipe.Recipe;
import com.example.cabinetchef.Recipe.RecipeDetail;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.Random;
import java.util.Set;


public class Home_screen extends AppCompatActivity {

    private PopupWindow screenSelectWindow;
    private DatabaseReference databaseReference;
    private PopupWindow filtersWindow;
    private ImageView recipeImage;
    private TextView recipeTitle;
    private PopupWindow mealTimesWindow;
    private PopupWindow cookingDifficultyWindow;
    private View cookingDifficultyPopupView;
    private View screenSelectView;
    private View filtersPopupView;
    private View mealTimesPopupView;
    private TextView allergenWarning;
    private EditText searchEditText;
    private static final int REQUEST_MEAL_TIME = 1;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the content view to the home_screen layout
        setContentView(R.layout.home_screen);

        recipeImage = findViewById(R.id.recipeImage);
        recipeTitle = findViewById(R.id.recipeTitle);
        allergenWarning = findViewById(R.id.allergenWarning);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes");
        // Initialize popupWindow and popupView
        screenSelectView = LayoutInflater.from(this).inflate(R.layout.screen_select_popup, null);
        screenSelectWindow = new PopupWindow(
                screenSelectView,
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

        // Setting animation style for the filters window to use default dialog animations
        filtersWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // Setting the background to null for the filters window
        filtersWindow.setBackgroundDrawable(null);

        // Finding and setting up the button to show the screen selection popup
        Button showScreenSelectButton = findViewById(R.id.showPopupButton);

        Button showFilterPopupButton = findViewById(R.id.showFiltersButton);

        showScreenSelectButton.setOnClickListener(v -> showScreenSelectPopup());
        showFilterPopupButton.setOnClickListener(v -> showFilterPopup());
        displayRandomRecipe();

        searchEditText = findViewById(R.id.searchEditText);
        setupSearchListener();
    }

    // Method to show the screen selection popup
    @SuppressLint("RtlHardcoded")
    private void showScreenSelectPopup() {
        // Inflating the root view of the home_screen layout for use in showing the popup
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);


        // Finding buttons within the popupView and setting their onClick listeners to launch different activities
        Button profileButton = screenSelectView.findViewById(R.id.profile);
        Button favoritesButton = screenSelectView.findViewById(R.id.Favorites);
        Button pantryButton = screenSelectView.findViewById(R.id.Pantry);
        Button utensilsButton = screenSelectView.findViewById(R.id.Utensils);
        Button settingsButton = screenSelectView.findViewById(R.id.Settings);

        // Set up onClick listeners for each button to start different activities and dismiss the popup
        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(Home_screen.this, ProfileScreen.class));
            screenSelectWindow.dismiss();
        });
        favoritesButton.setOnClickListener(v -> {
            startActivity(new Intent(Home_screen.this, FavoritesScreen.class));
            screenSelectWindow.dismiss();
        });
        pantryButton.setOnClickListener(v -> {
            startActivity(new Intent(Home_screen.this, Pantry.class));
            screenSelectWindow.dismiss();
        });
        utensilsButton.setOnClickListener(v -> {
            startActivity(new Intent(Home_screen.this, UtensilsScreen.class));
            screenSelectWindow.dismiss();
        });
        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(Home_screen.this, Settings.class));
            screenSelectWindow.dismiss();

        });

        // Setting the width and height for the popup window and displaying it on the screen
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // Show popup on the left half of the screen
        screenSelectWindow.setWidth(width);
        screenSelectWindow.setHeight(height);
        screenSelectWindow.showAtLocation(rootView, Gravity.LEFT, 0, 0);

    }

    // Method to show the filter popup
    @SuppressLint("RtlHardcoded")
    private void showFilterPopup() {
        // Inflating the root view of the home_screen layout for use in showing the popup
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);

        Button cookingDifficultyPopup = filtersPopupView.findViewById(R.id.Cooking_difficulty);
        Button mealTimePopup = filtersPopupView.findViewById(R.id.meal_time);
        Button ingredientsButton = filtersPopupView.findViewById(R.id.Item_preferences);

        mealTimePopup.setOnClickListener(v -> {
            showMealTimePopup();
            filtersWindow.dismiss();
        });

        cookingDifficultyPopup.setOnClickListener(v -> {
            showCookingDifficultyPopup();
            filtersWindow.dismiss();
        });

        ingredientsButton.setOnClickListener(v -> {
            startActivity(new Intent(Home_screen.this, IngredientsList.class));
            filtersWindow.dismiss();
        });
        // Calculate the width and height of the screen

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        filtersWindow.setWidth(width);
        filtersWindow.setHeight(height);
        filtersWindow.showAtLocation(rootView, Gravity.RIGHT, 0, 0);
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
//                //itemToBeUsedButton start activitiy goes here
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


    private void displayRandomRecipe() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Recipe> recipes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipes.add(recipe);
                }

                if (!recipes.isEmpty()) {
                    Random random = new Random();
                    Recipe randomRecipe = recipes.get(random.nextInt(recipes.size()));
                    displayRecipe(randomRecipe);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeScreen", "Failed to read recipe", error.toException());
            }
        });
    }

    private void displayRecipe(Recipe recipe) {
        if (recipe != null) {
            Glide.with(this).load(recipe.getImage()).into(recipeImage);
            recipeTitle.setText(recipe.getTitle());

            // Check if the recipe contains any allergens
            if (containsAllergens(recipe)) {
                allergenWarning.setVisibility(View.VISIBLE);
            } else {
                allergenWarning.setVisibility(View.GONE);
            }

            // Set up click listener for the recipe details
            View.OnClickListener recipeClickListener = v -> {
                Intent intent = new Intent(Home_screen.this, CookingScreen.class);
                intent.putExtra("RECIPE_IMAGE", recipe.getImage());
                intent.putExtra("RECIPE_TITLE", recipe.getTitle());
                intent.putExtra("RECIPE_TIME", recipe.getReadyInMinutes());

                Gson gson = new Gson();
                String instructionsJson = gson.toJson(recipe.getInstructions());
                Type ingredientListType = new TypeToken<List<RecipeDetail.Ingredient>>(){}.getType();
                String ingredientsJson = gson.toJson(recipe.getIngredients(), ingredientListType);

                intent.putExtra("RECIPE_INSTRUCTIONS_JSON", instructionsJson);
                intent.putExtra("RECIPE_INGREDIENTS_JSON", ingredientsJson);

                startActivity(intent);
            };

            recipeImage.setOnClickListener(recipeClickListener);
            recipeTitle.setOnClickListener(recipeClickListener);
        }
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                searchRecipes(editable.toString());
            }
        });
    }

    private void searchRecipes(String query) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Recipe> filteredRecipes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null && recipe.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        filteredRecipes.add(recipe);
                    }
                }

                if (!filteredRecipes.isEmpty()) {
                    // Display the first recipe from the filtered list for demonstration purposes
                    displayRecipe(filteredRecipes.get(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeScreen", "Failed to read recipe", databaseError.toException());
            }
        });
    }

    private boolean containsAllergens(Recipe recipe) {
        Set<String> userAllergens = getUserAllergens();
        for (RecipeDetail.Ingredient ingredient : recipe.getIngredients()) {
            if (userAllergens.contains(ingredient.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private Set<String> getUserAllergens() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        return sharedPreferences.getStringSet("allergens", new HashSet<>());
    }



    //DONT REMOVE IT PLEASE IT IS VERY IMPORTANT BECAUSE BECAUSE------------------------------------------------------------------------------------------------------------!!!

    /*private void validateIngredientsDataType(DatabaseReference databaseReference) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot ingredientsSnapshot = recipeSnapshot.child("ingredients");
                    for (DataSnapshot ingredientSnapshot : ingredientsSnapshot.getChildren()) {
                        boolean nameIsString = ingredientSnapshot.child("name").getValue() instanceof String;
                        boolean amountIsString = ingredientSnapshot.child("amount").getValue() instanceof String;
                        boolean unitIsString = ingredientSnapshot.child("unit").getValue() instanceof String;

                        if (!nameIsString || !amountIsString || !unitIsString) {
                            Log.d("ValidateIngredients", "No: Ingredient data types are not all strings.");
                            return;
                        }
                    }
                }
                Log.d("ValidateIngredients", "Yes: All ingredient fields are strings.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ValidateIngredients", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void identifyNonStringIngredientData(DatabaseReference databaseReference) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    String recipeId = recipeSnapshot.getKey();
                    boolean hasNonStringField = false;

                    for (DataSnapshot ingredientSnapshot : recipeSnapshot.child("ingredients").getChildren()) {
                        Object name = ingredientSnapshot.child("name").getValue();
                        Object amount = ingredientSnapshot.child("amount").getValue();
                        Object unit = ingredientSnapshot.child("unit").getValue();

                        // Check if any field is not a String
                        if (!(name instanceof String) || !(amount instanceof String) || !(unit instanceof String)) {
                            hasNonStringField = true;
                            break;
                        }
                    }

                    if (hasNonStringField) {
                        Log.d("NonStringIngredientData", "Recipe ID with non-string ingredient fields: " + recipeId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NonStringIngredientData", "Database error", databaseError.toException());
            }
        });
    }*/







}