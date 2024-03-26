package com.example.cabinetchef;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabinetchef.Recipe.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Activity to display favorite recipes.
 */
public class FavoritesScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteRecipesAdapter adapter;
    private List<Recipe> favoriteRecipesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_recipes);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list of favorite recipes
        favoriteRecipesList = new ArrayList<>();

        // Fetch favorite recipes from the database
        fetchFavoriteRecipes();

        // Initialize the adapter with the list of favorite recipes
        adapter = new FavoriteRecipesAdapter(favoriteRecipesList);

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        // Set up back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Fetches favorite recipes from the Firebase Realtime Database.
     */
    private void fetchFavoriteRecipes() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // List to store fetched favorite recipes
                List<Recipe> favoriteRecipes = new ArrayList<>();
                int count = 0;
                // Loop through the data snapshot to get favorite recipes
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        favoriteRecipes.add(recipe);
                        count++;
                        // Exit the loop after retrieving the first 5 recipes
                        if (count >= 5) {
                            break;
                        }
                    }
                }

                // Update the list of favorite recipes
                favoriteRecipesList.addAll(favoriteRecipes);

                // Notify the adapter that the dataset has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log an error message if reading recipes fails
                Log.e("FavoritesScreen", "Failed to read recipes", databaseError.toException());
            }
        });
    }
}
