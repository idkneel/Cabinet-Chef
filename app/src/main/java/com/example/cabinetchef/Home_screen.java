package com.example.cabinetchef;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetchef.Listeners.SpoonacularService;
import com.example.cabinetchef.Recipe.Recipe;
import com.example.cabinetchef.Recipe.RecipeDetail;
import com.example.cabinetchef.Recipe.RecipeSummary;
import com.example.cabinetchef.Recipe.RecipesResponse;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Defines the Home_screen class that extends AppCompatActivity for basic Android activity behavior
public class Home_screen extends AppCompatActivity {

    // Declaration of PopupWindow objects for different UI elements
    private PopupWindow popupWindow;
    private PopupWindow filtersWindow;

    // Declaration of View objects for inflating layout resources
    private View popupView;

    @SuppressLint("InflateParams")
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
        View filtersPopupView = LayoutInflater.from(this).inflate(R.layout.filter_options_popup, null);
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
        showScreenSelectButton.setOnClickListener(v -> {
            showScreenSelectPopup(); // Call method to show the screen selection popup
        });
        // Finding and setting up the button to show the filters popup
        Button showFilterPopupButton = findViewById(R.id.showFiltersButton);
        showFilterPopupButton.setOnClickListener(v -> {
            showFilterPopup(); // Call method to show the filter popup
        });

        initializeButtons();
    }

    private void initializeButtons() {
        Button showScreenSelectButton = findViewById(R.id.showPopupButton);
        showScreenSelectButton.setOnClickListener(v -> showScreenSelectPopup());

        Button showFilterPopupButton = findViewById(R.id.showFiltersButton);
        showFilterPopupButton.setOnClickListener(v -> showFilterPopup());

        Button fetchDataButton = findViewById(R.id.firebaseTestButton);
        fetchDataButton.setOnClickListener(v -> fetchDataAndSaveToFirebase());
    }

        private Recipe convertToRecipe (RecipeDetail detail){
            List<String> ingredients = detail.getExtendedIngredients().stream()
                    .map(ingredient -> ingredient.getName() + " - " + ingredient.getAmount() + " " + ingredient.getUnit())
                    .collect(Collectors.toList());
            String instructions = detail.getInstructions();

            return new Recipe(detail.getTitle(), ingredients, detail.getReadyInMinutes(), detail.getImage(), Arrays.asList(instructions.split("\r?\n")));
        }

        private void fetchDataAndSaveToFirebase () {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.spoonacular.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            SpoonacularService service = retrofit.create(SpoonacularService.class);

            String[] queries = {"pasta", "salad", "chicken", "beef"};
            for (String query : queries) {
                service.getRecipes("cb765e381a874b6abf2f6f605c92ecec", query).enqueue(new Callback<RecipesResponse>() {
                    @Override
                    public void onResponse(Call<RecipesResponse> call, Response<RecipesResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (RecipeSummary summary : response.body().getResults()) {
                                fetchRecipeDetailsById(summary.getId(), service);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RecipesResponse> call, Throwable t) {
                        // Log failure
                    }
                });
            }
        }

        private void fetchRecipeDetailsById ( int recipeId, SpoonacularService service){
            service.getRecipeDetails(recipeId, "cb765e381a874b6abf2f6f605c92ecec").enqueue(new Callback<RecipeDetail>() {
                @Override
                public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RecipeDetail detail = response.body();
                        Recipe recipe = convertToRecipe(detail);
                        saveDataToFirebase(Arrays.asList(recipe));
                    } else {
                        // Handle error
                    }
                }

                @Override
                public void onFailure(Call<RecipeDetail> call, Throwable t) {
                    // Handle failure
                }
            });
        }

        private void saveDataToFirebase (List < Recipe > recipes) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
            for (Recipe recipe : recipes) {
                databaseReference.push().setValue(recipe);
            }
        }
        // Method to show the screen selection popup
        @SuppressLint("RtlHardcoded")
        private void showScreenSelectPopup () {
            // Inflating the root view of the home_screen layout for use in showing the popup
            @SuppressLint("InflateParams") View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);

            // Finding buttons within the popupView and setting their onClick listeners to launch different activities
            Button profileButton = popupView.findViewById(R.id.profile);
            Button favoritesButton = popupView.findViewById(R.id.Favorites);
            Button pantryButton = popupView.findViewById(R.id.Pantry);
            Button utensilsButton = popupView.findViewById(R.id.Utensils);
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
        @SuppressLint("RtlHardcoded")
        private void showFilterPopup () {
            // Inflating the root view of the home_screen layout for use in showing the popup
            @SuppressLint("InflateParams") View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);

            // Setting the width and height for the filters window and displaying it on the screen
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            filtersWindow.setWidth(width);
            filtersWindow.setHeight(height);
            filtersWindow.showAtLocation(rootView, Gravity.RIGHT, 0, 0);
        }
    }