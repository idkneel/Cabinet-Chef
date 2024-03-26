package com.example.cabinetchef;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabinetchef.Recipe.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavoritesScreen extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private RecyclerView recyclerView;
    private FavoriteRecipesAdapter adapter;
    private List<Recipe> favoriteRecipesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_recipes);

        // Initialize Firestore instance
        fStore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteRecipesList = new ArrayList<>();
        adapter = new FavoriteRecipesAdapter(favoriteRecipesList);
        recyclerView.setAdapter(adapter);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish(); // Close the current activity and go back
        });

        // Load favorite recipes from Firestore
        loadFavoriteRecipes();
    }

    private void loadFavoriteRecipes() {
        // Get the current user's ID from FirebaseAuth (assuming user is logged in)
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Reference to the user's favorite recipes collection in Firestore
        CollectionReference favoriteRecipesRef = fStore.collection("favorites").document(userId).getParent();

        // Query to get all favorite recipes
        favoriteRecipesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            //favoriteRecipesList.clear(); // Clear the list before adding new data

            // Loop through each document snapshot
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                // Get recipe details from the document snapshot
                String recipeTitle = documentSnapshot.getString("Recipe Name");
                String recipeImage = documentSnapshot.getString("Recipe Image");
                // Add more fields as needed

                // Create a Recipe object and add it to the list
                Recipe recipe = new Recipe();
                recipe.setTitle(recipeTitle);
                recipe.setImage(recipeImage);
                // Set more fields as needed
                favoriteRecipesList.add(recipe);
            }

            // Notify the adapter that the dataset has changed
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            // Handle failure to fetch favorite recipes
            Log.e(TAG, "Error fetching favorite recipes", e);
        });
    }

}


