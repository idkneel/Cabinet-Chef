package com.example.cabinetchef;

import com.example.cabinetchef.Recipe.Recipe;
import com.example.cabinetchef.Recipe.RecipeDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for calculating cooking difficulty based on the number of ingredients in a recipe.
 */
public class CookingDifficultyCalculator {

    /**
     * Listener interface for receiving the calculated cooking difficulty.
     */
    public interface CookingDifficultyListener {
        /**
         * Callback method to receive the calculated cooking difficulty.
         *
         * @param difficulty The calculated cooking difficulty.
         */
        void onDifficultyCalculated(String difficulty);
    }

    /**
     * Calculates the cooking difficulty for a given recipe.
     *
     * @param recipeId The ID of the recipe to calculate the difficulty for.
     * @param listener The listener to receive the calculated difficulty.
     */
    public void calculateDifficulty(String recipeId, final CookingDifficultyListener listener) {
        FirebaseDatabase.getInstance().getReference().child("recipes").child(recipeId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Recipe recipe = dataSnapshot.getValue(Recipe.class);
                        if (recipe != null) {
                            List<RecipeDetail.Ingredient> ingredients = recipe.getIngredients();
                            String difficulty = determineCookingDifficulty(ingredients.size());
                            listener.onDifficultyCalculated(difficulty);
                        } else {
                            listener.onDifficultyCalculated("Unknown");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onDifficultyCalculated("Error");
                    }
                });
    }

    /**
     * Determines the cooking difficulty based on the number of ingredients.
     *
     * @param numberOfIngredients The number of ingredients in the recipe.
     * @return The cooking difficulty level.
     */
    private String determineCookingDifficulty(int numberOfIngredients) {
        if (numberOfIngredients <= 4) {
            return "Easy";
        } else if (numberOfIngredients <= 8) {
            return "Medium";
        } else {
            return "Hard";
        }
    }
}
