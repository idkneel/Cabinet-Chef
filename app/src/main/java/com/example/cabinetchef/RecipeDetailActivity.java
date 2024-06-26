package com.example.cabinetchef;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cabinetchef.Recipe.RecipeDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// This class represents the Recipe Detail Activity
public class RecipeDetailActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail); // Set the layout for this activity

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPantry", MODE_PRIVATE);
        SharedPreferences userPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        Set<String> userAllergens = userPreferences.getStringSet("allergens", new HashSet<>());

        // Initialize views
        ImageView recipeImageView = findViewById(R.id.recipeImageDialog);
        TextView recipeTitleView = findViewById(R.id.recipeTitleDialog);
        TextView recipeTimeView = findViewById(R.id.recipeTimeDialog);
        TextView recipeIngredientsView = findViewById(R.id.recipeIngredientsDialog);
        TextView recipeInstructionsView = findViewById(R.id.recipeInstructionsDialog);
        Button startCookingButton = findViewById(R.id.startCooking);

        // Retrieve recipe details from intent
        String recipeImage = getIntent().getStringExtra("RECIPE_IMAGE");
        String recipeTitle = getIntent().getStringExtra("RECIPE_TITLE");
        int recipeTime = getIntent().getIntExtra("RECIPE_TIME", 0);
        String instructionsJson = getIntent().getStringExtra("RECIPE_INSTRUCTIONS_JSON");
        String ingredientsJson = getIntent().getStringExtra("RECIPE_INGREDIENTS_JSON");

        // Load recipe image using Glide
        Glide.with(this).load(recipeImage).into(recipeImageView);
        recipeTitleView.setText(recipeTitle);
        recipeTimeView.setText(String.format("%d minutes", recipeTime));

        // Parse instructions JSON and display in TextView
        Gson gson = new Gson();
        Type instructionsType = new TypeToken<List<String>>() {}.getType();
        List<String> instructions = gson.fromJson(instructionsJson, instructionsType);

        // Parse ingredients JSON and display in TextView
        Type ingredientListType = new TypeToken<List<RecipeDetail.Ingredient>>() {}.getType();
        List<RecipeDetail.Ingredient> ingredients = gson.fromJson(ingredientsJson, ingredientListType);
        StringBuilder ingredientsText = new StringBuilder();
        for (RecipeDetail.Ingredient ingredient : ingredients) {
            boolean isInPantry = sharedPreferences.getBoolean(ingredient.getName().toLowerCase(), false);

            // Building the ingredient string
            ingredientsText.append(ingredient.getName())
                    .append(": ")
                    .append(ingredient.getAmount())
                    .append(" ")
                    .append(ingredient.getUnit());

            // Append the asterisk if the ingredient is in the pantry
            if (isInPantry) {
                ingredientsText.append(" !");
            }

            // Append the newline character to move to the next ingredient
            ingredientsText.append("\n");
        }
        recipeIngredientsView.setText(ingredientsText.toString());

        // OnClickListener for startCookingButton to navigate to CookingScreen activity
        startCookingButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeDetailActivity.this, CookingScreen.class);
            startActivity(intent);
        });
    }

    // Method to check if an ingredient contains any allergens
    private boolean containsAllergens(String ingredientName, Set<String> allergens) {
        String lowerCaseIngredient = ingredientName.toLowerCase().trim();
        for (String allergen : allergens) {
            String lowerCaseAllergen = allergen.toLowerCase().trim();
            if (lowerCaseIngredient.contains(lowerCaseAllergen)) {
                return true;
            }
        }
        return false;
    }

    // Method to process instructions JSON and format it for display
    private String processInstructions(String instructionsJson) {
        // Convert JSON string to List<String> using Gson
        Gson gson = new Gson();
        Type instructionsType = new TypeToken<List<String>>() {}.getType();
        List<String> instructionsList = gson.fromJson(instructionsJson, instructionsType);

        // Define characters to be removed
        String charactersToRemove = "</?li>|</?ol>";

        // Replace all occurrences of charactersToRemove with an empty string
        StringBuilder processedInstructions = new StringBuilder();
        for (String instruction : instructionsList) {
            String cleanInstruction = instruction.replaceAll(charactersToRemove, "");
            processedInstructions.append(cleanInstruction).append("\n");
        }
        return processedInstructions.toString().trim(); // Trim any leading or trailing whitespaces
    }

    // Method to toggle visibility of ingredients dropdown
    public void toggleIngredientsDropdown(View view) {
        LinearLayout ingredientsContent = findViewById(R.id.recipeIngredientsContent);
        if (ingredientsContent.getVisibility() == View.VISIBLE) {
            ingredientsContent.setVisibility(View.GONE);
        } else {
            ingredientsContent.setVisibility(View.VISIBLE);
        }
    }

    // Method to toggle visibility of instructions dropdown
    public void toggleInstructionsDropdown(View view) {
        LinearLayout instructionsContent = findViewById(R.id.recipeInstructionsContent);
        if (instructionsContent.getVisibility() == View.VISIBLE) {
            instructionsContent.setVisibility(View.GONE);
        } else {
            instructionsContent.setVisibility(View.VISIBLE);
        }
    }
}
