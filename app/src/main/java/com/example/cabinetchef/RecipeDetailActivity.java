package com.example.cabinetchef;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cabinetchef.Recipe.RecipeDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ImageView recipeImageView = findViewById(R.id.recipeImageDialog);
        TextView recipeTitleView = findViewById(R.id.recipeTitleDialog);
        TextView recipeTimeView = findViewById(R.id.recipeTimeDialog);
        TextView recipeIngredientsView = findViewById(R.id.recipeIngredientsDialog);
        TextView recipeInstructionsView = findViewById(R.id.recipeInstructionsDialog);
        Button finishCookingButton = findViewById(R.id.finishCooking);

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
            ingredientsText.append(ingredient.getName())
                    .append(": ")
                    .append(ingredient.getAmount())
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append("\n");
        }
        recipeIngredientsView.setText(ingredientsText.toString());

        recipeInstructionsView.setText(processInstructions(instructionsJson));

        finishCookingButton.setOnClickListener(v -> {
            startActivity(new Intent(RecipeDetailActivity.this, Home_screen.class));

            //add code to save recipe to recents
            //make favorites page and ask user if they wish to favorite recipe

        });

    }

    private String processInstructions(String instructionsJson) {
        // Convert JSON string to List<String> using Gson
        Gson gson = new Gson();
        Type instructionsType = new TypeToken<List<String>>() {}.getType();
        List<String> instructionsList = gson.fromJson(instructionsJson, instructionsType);

        // Define characters to be removed
        String charactersToRemove = "</?li>|</?ol>"; // Include <ol> in the characters to remove

        // Replace all occurrences of charactersToRemove with an empty string
        StringBuilder processedInstructions = new StringBuilder();
        for (String instruction : instructionsList) {
            String cleanInstruction = instruction.replaceAll(charactersToRemove, "");
            processedInstructions.append(cleanInstruction).append("\n");
        }
        return processedInstructions.toString().trim(); // Trim any leading or trailing whitespaces
    }


}
