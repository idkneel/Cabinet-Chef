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

    private void fetchFavoriteRecipes() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes");
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

                // Display the first 5 recipes
                List<Recipe> favoriteRecipes = allRecipes.subList(0, Math.min(allRecipes.size(), 5));

                // Update the list of favorite recipes
                favoriteRecipesList.addAll(favoriteRecipes);

                // Notify the adapter that the dataset has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FavoritesScreen", "Failed to read recipes", databaseError.toException());
            }
        });
    }
}
