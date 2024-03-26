package com.example.cabinetchef;

import com.example.cabinetchef.Recipe.Recipe;
import com.example.cabinetchef.Recipe.RecipeDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CookingDifficultyCalculator {

    public interface CookingDifficultyListener {
        void onDifficultyCalculated(String difficulty);
    }

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

