package com.example.cabinetchef;

import static android.content.ContentValues.TAG;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cabinetchef.Recipe.Recipe;
import com.example.cabinetchef.Recipe.RecipeDetail;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;


public class CookingScreen extends AppCompatActivity {
    FirebaseFirestore fStore;
    Recipe recipe = new Recipe();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cooking_screen);

        ImageView recipeImageView = findViewById(R.id.recipeImageDialog);
        TextView recipeTitleView = findViewById(R.id.recipeTitleDialog);
        TextView recipeTimeView = findViewById(R.id.recipeTimeDialog);
        TextView recipeIngredientsView = findViewById(R.id.recipeIngredientsDialog);
        TextView recipeInstructionsView = findViewById(R.id.recipeInstructionsDialog);
        Button finishCookingButton = findViewById(R.id.finishCooking);
        Button addToFavoritesButton = findViewById(R.id.addToFavorites);

        String recipeImage = getIntent().getStringExtra("RECIPE_IMAGE");
        String recipeTitle = getIntent().getStringExtra("RECIPE_TITLE");
        int recipeTime = getIntent().getIntExtra("RECIPE_TIME", 0);
        String instructionsJson = getIntent().getStringExtra("RECIPE_INSTRUCTIONS_JSON");
        String ingredientsJson = getIntent().getStringExtra("RECIPE_INGREDIENTS_JSON");

        Glide.with(this).load(recipeImage).into(recipeImageView);
        recipeTitleView.setText(recipeTitle);
        recipeTimeView.setText(String.format("%d minutes", recipeTime));

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

        recipeInstructionsView.setText(processInstructions(instructionsJson, "</?li>|</?ol>"));

        finishCookingButton.setOnClickListener(v -> startActivity(new Intent(CookingScreen.this, Home_screen.class)));

        addToFavoritesButton.setOnClickListener( v -> {
            updateData(recipe);

            startActivity(new Intent(CookingScreen.this, Home_screen.class));
        });

    }

    private String processInstructions(String instructionsJson, String removeChars) {
        // Convert JSON string to List<String> using Gson
        Gson gson = new Gson();
        Type instructionsType = new com.google.gson.reflect.TypeToken<List<String>>() {}.getType();
        List<String> instructionsList = gson.fromJson(instructionsJson, instructionsType);


        // Replace all occurrences of charactersToRemove with an empty string
        StringBuilder processedInstructions = new StringBuilder();
        if (instructionsList != null) {
            for (String instruction : instructionsList) {
                String cleanInstruction = instruction.replaceAll(removeChars, "");
                processedInstructions.append(cleanInstruction).append("\n");
            }
        } else {
            Log.e(TAG, "Instructions list is null.");
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


    private void updateData(Recipe recipe) {
        // Create a map to hold the update, in this case, the new "Household members" value
        Map<String, Object> userDetail = new HashMap<>();


        String recipeImage = getIntent().getStringExtra("RECIPE_IMAGE");
        String recipeTitle = getIntent().getStringExtra("RECIPE_TITLE");
        int recipeTime = getIntent().getIntExtra("RECIPE_TIME", 0);
        String instructionsJson2 = getIntent().getStringExtra("RECIPE_INSTRUCTIONS_JSON");
        String ingredientsJson = getIntent().getStringExtra("RECIPE_INGREDIENTS_JSON");

        assert instructionsJson2 != null;
        if (instructionsJson2.contains("u003col\\u003e\\u003cli\\u003e")){
            instructionsJson2 = processInstructions(instructionsJson2, "u003col\\u003e\\u003cli\\u003e");
        } else if (instructionsJson2.contains("<ol><li>")){
            instructionsJson2 = processInstructions(instructionsJson2, "<ol><li>");
        } else if (instructionsJson2.contains("</li></ol>")){
            instructionsJson2 = processInstructions(instructionsJson2, "</li></ol>");
        } else if (instructionsJson2.contains("</li></li>")) {
            instructionsJson2 = processInstructions(instructionsJson2, "</li></li>");
        }

        userDetail.put("Recipe Name", recipeTitle);
        userDetail.put("Recipe image", recipeImage);
        userDetail.put("Ready in (Minutes)", recipeTime);
        userDetail.put("Ingredients", ingredientsJson);
        userDetail.put("Instructions", instructionsJson2);

        // Initialize Firestore instance

        fStore = FirebaseFirestore.getInstance();

        // Get the current user's ID from FirebaseAuth (assuming user is logged in)
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Reference to the specific user's document in the "users" collection
        DocumentReference userRecipeDocRef = fStore.collection("favorite recipes").document(userId);

        // Try to get the document for the current user
        userRecipeDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // If the document exists, update it with the new details
                userRecipeDocRef.update(userDetail).addOnSuccessListener(unused -> {
                    // Success: Show a success message
                    Log.d(TAG, "successfully Updated");
                }).addOnFailureListener(e -> {
                    // Failure: Show an error message
                    Log.d(TAG, "Error updating document");
                });
            } else {
                // If the document does not exist (fallback scenario),
                // attempt to find the document by "Household members" field and old number
                fStore.collection("favorite recipes")
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                // If successful and documents are found,
                                // get the first document and its ID
                                DocumentSnapshot documentSnapshot1 = task.getResult().getDocuments().get(0);
                                String documentID = documentSnapshot1.getId();
                                // Update the found document with the new details
                                fStore.collection("favorite recipes")
                                        .document(documentID)
                                        .update(userDetail)
                                        .addOnSuccessListener(unused -> {
                                            // Success: Show a success message
                                            Log.d(TAG, "successfully Updated");
                                        }).addOnFailureListener(e -> {
                                            // Failure: Show an error message
                                            Log.d(TAG, "Some error occurred");
                                        });
                            } else {
                                Log.d(TAG, "Failure to find document");
                            }
                        }).addOnFailureListener(e -> {
                            // If there was an error accessing the document, show an error message
                            Log.d(TAG, "Error accessing document");
                        });
            }
        });


        // Try to get the document for the current user
        fStore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (!task.getResult().exists()){
                userDetail.put("User ID", userId);
                fStore.collection("users").document(userId).collection("favorites").add(userDetail);
            } else {
                fStore.collection("users").document(userId).collection("favorites").add(userDetail);
            }
        });

    }
}