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

public class RecipeDetailActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        sharedPreferences = getSharedPreferences("UserPantry", MODE_PRIVATE);
        SharedPreferences userPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        Set<String> userAllergens = userPreferences.getStringSet("allergens", new HashSet<>());

        ImageView recipeImageView = findViewById(R.id.recipeImageDialog);
        TextView recipeTitleView = findViewById(R.id.recipeTitleDialog);
        TextView recipeTimeView = findViewById(R.id.recipeTimeDialog);
        TextView recipeIngredientsView = findViewById(R.id.recipeIngredientsDialog);
        TextView recipeInstructionsView = findViewById(R.id.recipeInstructionsDialog);
        Button startCookingButton = findViewById(R.id.startCooking);

        String recipeImage = getIntent().getStringExtra("RECIPE_IMAGE");
        String recipeTitle = getIntent().getStringExtra("RECIPE_TITLE");
        int recipeTime = getIntent().getIntExtra("RECIPE_TIME", 0);
        String instructionsJson = getIntent().getStringExtra("RECIPE_INSTRUCTIONS_JSON");
        String ingredientsJson = getIntent().getStringExtra("RECIPE_INGREDIENTS_JSON");

        Glide.with(this).load(recipeImage).into(recipeImageView);
        recipeTitleView.setText(recipeTitle);
        recipeTimeView.setText(String.format("%d minutes", recipeTime));

        Gson gson = new Gson();
        Type instructionsType = new TypeToken<List<String>>() {}.getType();
        List<String> instructions = gson.fromJson(instructionsJson, instructionsType);

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

        startCookingButton.setOnClickListener(v -> {
             Intent intent = new Intent(RecipeDetailActivity.this, CookingScreen.class);

//            intent.putExtra("RECIPE_IMAGE", clickedRecipe.getImage());
//            intent.putExtra("RECIPE_TITLE", clickedRecipe.getTitle());
//            intent.putExtra("RECIPE_TIME", clickedRecipe.getReadyInMinutes());
//
//            // Convert instructions and ingredients to JSON strings
//            Gson gson = new Gson();
//            String instructionsJson = gson.toJson(clickedRecipe.getInstructions());
//            Type ingredientListType = new com.google.common.reflect.TypeToken<List<RecipeDetail.Ingredient>>(){}.getType();
//            String ingredientsJson = gson.toJson(clickedRecipe.getIngredients(), ingredientListType);
//
//            // Pass JSON strings to the intent
//            intent.putExtra("RECIPE_INSTRUCTIONS_JSON", instructionsJson);
//            intent.putExtra("RECIPE_INGREDIENTS_JSON", ingredientsJson);
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

            // Start the CookingScreen activity with the intent
            startActivity(intent);
    private String determineCookingDifficulty(int numberOfIngredients) {
        if (numberOfIngredients <= 4) {
            return "Easy";
        } else if (numberOfIngredients <= 8) {
            return "Medium";
        } else {
            return "Hard";
        }
    }

        });

    private boolean isIngredientInPantry(String ingredient) {
        return sharedPreferences.getBoolean(ingredient, false);
    }

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

    public void toggleIngredientsDropdown(View view) {
        LinearLayout ingredientsContent = findViewById(R.id.recipeIngredientsContent);
        if (ingredientsContent.getVisibility() == View.VISIBLE) {
            ingredientsContent.setVisibility(View.GONE);
        } else {
            ingredientsContent.setVisibility(View.VISIBLE);
        }
    }

    public void toggleInstructionsDropdown(View view) {
        LinearLayout instructionsContent = findViewById(R.id.recipeInstructionsContent);
        if (instructionsContent.getVisibility() == View.VISIBLE) {
            instructionsContent.setVisibility(View.GONE);
        } else {
            instructionsContent.setVisibility(View.VISIBLE);
        }
    }

}
