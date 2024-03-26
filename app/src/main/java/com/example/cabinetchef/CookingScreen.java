package com.example.cabinetchef;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cabinetchef.Recipe.Recipe;
import com.example.cabinetchef.Recipe.RecipeDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class represents the Cooking Screen activity
public class CookingScreen extends AppCompatActivity {
    FirebaseFirestore fStore;
    Recipe recipe = new Recipe();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cooking_screen); // Set the layout for this activity

        // Initialize views
        ImageView recipeImageView = findViewById(R.id.recipeImageDialog);
        TextView recipeTitleView = findViewById(R.id.recipeTitleDialog);
        TextView recipeTimeView = findViewById(R.id.recipeTimeDialog);
        TextView recipeIngredientsView = findViewById(R.id.recipeIngredientsDialog);
        TextView recipeInstructionsView = findViewById(R.id.recipeInstructionsDialog);
        Button finishCookingButton = findViewById(R.id.finishCooking);
        Button addToFavoritesButton = findViewById(R.id.addToFavorites);

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

        // Parse ingredients JSON and display in TextView
        Gson gson = new Gson();
        Type ingredientListType = new com.google.gson.reflect.TypeToken<List<RecipeDetail.Ingredient>>() {}.getType();
        List<RecipeDetail.Ingredient> ingredients = gson.fromJson(ingredientsJson, ingredientListType);
        StringBuilder ingredientsText = new StringBuilder();
        assert ingredients != null;
        for (RecipeDetail.Ingredient ingredient : ingredients) {
            ingredientsText.append(ingredient.getName())
                    .append(": ")
                    .append(ingredient.getAmount())
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append("\n");
        }
        recipeIngredientsView.setText(ingredientsText.toString());

        // Process instructions JSON and display in TextView
        recipeInstructionsView.setText(processInstructions(instructionsJson, "</?li>|</?ol>"));

        // OnClickListener for finishCookingButton to return to Home screen
        finishCookingButton.setOnClickListener(v -> startActivity(new Intent(CookingScreen.this, Home_screen.class)));

        // OnClickListener for addToFavoritesButton to add the recipe to favorites
        addToFavoritesButton.setOnClickListener( v -> {
            updateData(recipe); // Update data in Firestore
            startActivity(new Intent(CookingScreen.this, Home_screen.class)); // Return to Home screen
        });
    }

    // Method to process instructions JSON and format it for display
    private String processInstructions(String instructionsJson, String removeChars) {
        Gson gson = new Gson();
        Type instructionsType = new com.google.gson.reflect.TypeToken<List<String>>() {}.getType();
        List<String> instructionsList = gson.fromJson(instructionsJson, instructionsType);

        StringBuilder processedInstructions = new StringBuilder();
        if (instructionsList != null) {
            for (String instruction : instructionsList) {
                String cleanInstruction = instruction.replaceAll(removeChars, "");
                processedInstructions.append(cleanInstruction).append("\n");
            }
        } else {
            Log.e(TAG, "Instructions list is null.");
        }

        return processedInstructions.toString().trim();
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

    // Method to update data in Firestore when adding to favorites
    private void updateData(Recipe recipe) {
        // Create a map to hold the update
        Map<String, Object> userDetail = new HashMap<>();

        Gson gson = new Gson();
        String instructionsJson = gson.toJson(recipe.getInstructions());

        // Retrieve recipe details from intent
        String recipeImage = getIntent().getStringExtra("RECIPE_IMAGE");
        String recipeTitle = getIntent().getStringExtra("RECIPE_TITLE");
        int recipeTime = getIntent().getIntExtra("RECIPE_TIME", 0);
        String ingredientsJson = getIntent().getStringExtra("RECIPE_INGREDIENTS_JSON");

        // Process instructions JSON to remove unnecessary characters
        String instructionsJson2 = processInstructions(instructionsJson, "u003col\u003e\u003cli\u003e|<ol><li>|</li></ol>|</li></li>");

        // Add recipe details to the map
        userDetail.put("Recipe Name", recipeTitle);
        userDetail.put("Recipe image", recipeImage);
        userDetail.put("Ready in (Minutes)", recipeTime);
        userDetail.put("Ingredients", ingredientsJson);
        userDetail.put("Instructions", instructionsJson2);

        // Initialize Firestore instance
        fStore = FirebaseFirestore.getInstance();

        // Get the current user's ID from FirebaseAuth
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the specific user's document in the "users" collection
        DocumentReference userDocRef = fStore.collection("users").document(userId);

        // Try to get the document for the current user
        fStore.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                fStore.collection("users").document(userId).collection("favorites").document(recipeTitle).set(userDetail);
            }
        });
    }
}
