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
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabinetchef.Recipe.Recipe;
import com.example.cabinetchef.Recipe.RecipeDetail;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Collections;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Random;
import java.util.Set;


public class Home_screen extends AppCompatActivity {

    private PopupWindow screenSelectWindow;
    private DatabaseReference databaseReference;
    private PopupWindow filtersWindow;
    private PopupWindow mealTimesWindow;
    private PopupWindow cookingDifficultyWindow;
    private View cookingDifficultyPopupView;
    private View screenSelectView;
    private View filtersPopupView;
    private View mealTimesPopupView;
    RecyclerView recyclerView;
    private EditText searchEditText;

    private static final int REQUEST_MEAL_TIME = 1;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the content view to the home_screen layout
        setContentView(R.layout.home_screen);

        // Initialize RecyclerView

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize RecyclerView adapter



        databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes");

        // Pass context and user allergens to RecipeAdapter constructor

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Recipe> allRecipes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        allRecipes.add(recipe);
                    }
                }

                // Shuffle the list of recipes
                Collections.shuffle(allRecipes);

                // Display the first 20 recipes
                List<Recipe> randomRecipes = allRecipes.subList(0, Math.min(allRecipes.size(), 20));
                Random random = new Random();
                int randomIndex = random.nextInt(randomRecipes.size());
                Recipe randomRecipe = randomRecipes.get(randomIndex);
                displayRandomRecipes();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

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
        displayRandomRecipes();

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
        //Button utensilsButton = screenSelectView.findViewById(R.id.Utensils);
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
//        utensilsButton.setOnClickListener(v -> {
//            startActivity(new Intent(Home_screen.this, UtensilsScreen.class));
//            screenSelectWindow.dismiss();
//        });
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
//                //itemToBeUsedButton start activity goes here
//            }
//        });

        // Show the meal time popup
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // Show popup on the left half of the screen
        mealTimesWindow.setWidth(width);
        mealTimesWindow.setHeight(height);
        mealTimesWindow.showAtLocation(rootView, Gravity.END, 0, 0);
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
        cookingDifficultyWindow.showAtLocation(rootView, Gravity.END, 0, 0);
    }




    private void displayRandomRecipes() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Recipe> allRecipes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        allRecipes.add(recipe);
                    }
                }

                // Shuffle the list of recipes
                Collections.shuffle(allRecipes);

                // Display the first 20 recipes
                List<Recipe> randomRecipes = allRecipes.subList(0, Math.min(allRecipes.size(), 20));

                // Display all selected random recipes
                displayRecipes(randomRecipes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Home_screen", "Failed to read recipes", databaseError.toException());
            }
        });

    }

    private void displayRecipes(List<Recipe> recipes) {
        if (!recipes.isEmpty()) {
            // Get user allergens from SharedPreferences
            List<String> userAllergens = getAllergens();
            // Initialize RecyclerView adapter
            RecipeAdapter adapter = new RecipeAdapter(this, recipes);
            recyclerView.setAdapter(adapter);

            // Check if the recipe contains any allergens

            adapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    // Get the clicked recipe
                    Recipe clickedRecipe = recipes.get(position);

                    // Create an intent to navigate to the CookingScreen activity
                    Intent intent = new Intent(Home_screen.this, CookingScreen.class);
                    // Pass necessary information about the clicked recipe to the intent
                    intent.putExtra("RECIPE_IMAGE", clickedRecipe.getImage());
                    intent.putExtra("RECIPE_TITLE", clickedRecipe.getTitle());
                    intent.putExtra("RECIPE_TIME", clickedRecipe.getReadyInMinutes());

                    // Convert instructions and ingredients to JSON strings
                    Gson gson = new Gson();
                    String instructionsJson = gson.toJson(clickedRecipe.getInstructions());
                    Type ingredientListType = new TypeToken<List<RecipeDetail.Ingredient>>(){}.getType();
                    String ingredientsJson = gson.toJson(clickedRecipe.getIngredients(), ingredientListType);

                    // Pass JSON strings to the intent
                    intent.putExtra("RECIPE_INSTRUCTIONS_JSON", instructionsJson);
                    intent.putExtra("RECIPE_INGREDIENTS_JSON", ingredientsJson);

                    // Start the CookingScreen activity with the intent
                    startActivity(intent);
                }
            });
        }
    }



    private List<String> getAllergens() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        Set<String> allergenSet = sharedPreferences.getStringSet("allergens", new HashSet<>());
        return new ArrayList<>(allergenSet);
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
                    if (recipe != null) {
                        // Check if the recipe title contains the query
                        if (recipe.getTitle().toLowerCase().contains(query.toLowerCase())) {
                            filteredRecipes.add(recipe);
                        } else {
                            // Check if any ingredient contains the query
                            for (RecipeDetail.Ingredient ingredient : recipe.getIngredients()) {
                                if (ingredient.getName().toLowerCase().contains(query.toLowerCase())) {
                                    filteredRecipes.add(recipe);
                                    break; // Once added, no need to check other ingredients
                                }
                            }
                        }
                    }
                }

                if (!filteredRecipes.isEmpty()) {
                    displayRecipes(filteredRecipes);
                } else {
                    // If no recipes match the query, you may want to display a message or handle it accordingly
                    // For now, you can clear the RecyclerView
                    recyclerView.setAdapter(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeScreen", "Failed to read recipes", databaseError.toException());
            }
        });
    }

    private boolean containsAllergens(Recipe recipe) {
        Set<String> userAllergens = getUserAllergens();
        for (RecipeDetail.Ingredient ingredient : recipe.getIngredients()) {
            for (String allergen : userAllergens) {
                if (ingredient.getName().toLowerCase().contains(allergen.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }


    private Set<String> getUserAllergens() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        return sharedPreferences.getStringSet("allergens", new HashSet<>());
    }


    //DON'T REMOVE IT PLEASE IT IS VERY IMPORTANT BECAUSE BECAUSE------------------------------------------------------------------------------------------------------------!!!

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

