package com.example.cabinetchef;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Activity class for selecting ingredients to be used in cooking.
 */
public class ItemToBeUsed extends AppCompatActivity implements IngredientAdapter.OnIngredientClickListener {

    private RecyclerView ingredientsRecyclerView;
    private IngredientAdapter adapter;

    private SharedPreferences sharedPreferences;

    // List to store all ingredient names
    private List<String> allIngredientNames = new ArrayList<>();
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cooking_item_to_be_used);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPantry", MODE_PRIVATE);

        // Initialize RecyclerView and Adapter
        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView);
        adapter = new IngredientAdapter(sharedPreferences, this);

        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRecyclerView.setAdapter(adapter);

        // Back button functionality
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Search functionality
        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterIngredients(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Fetch ingredients from Firebase
        fetchSimpleIngredients();
    }

    @Override
    public void onIngredientClick(String ingredient, boolean isInPantry) {
        togglePantryItem(ingredient, !isInPantry); // Toggle the pantry state for the ingredient
        fetchSimpleIngredients(); // Refresh the ingredients list
    }

    // Toggle the pantry state of an ingredient
    private void togglePantryItem(String ingredient, boolean isInPantry) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ingredient, isInPantry);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSimpleIngredients();  // Refresh the ingredients list to reflect the latest state
    }

    // Save pantry items to SharedPreferences
    private void savePantryItems() {
        // Here you might want to loop through the pantry items and save their state
        // However, since the state is saved on each click, this method could just confirm the save operation
        Log.d("Pantry", "Pantry items saved.");
        // You could add a Toast message or some other indicator to inform the user that the items have been saved
        Toast.makeText(this, "Pantry items saved.", Toast.LENGTH_SHORT).show();
    }

    // Filter ingredients based on search query
    private void filterIngredients(String query) {
        List<String> filteredList = new ArrayList<>();
        for (String ingredient : allIngredientNames) {
            if (ingredient.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(ingredient);
            }
        }
        adapter.setIngredients(filteredList);
    }

    // Fetch ingredients from Firebase database
    private void fetchSimpleIngredients() {
        FirebaseDatabase.getInstance().getReference().child("recipes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Set<String> ingredientSet = new HashSet<>();
                        for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                            DataSnapshot ingredientsSnapshot = recipeSnapshot.child("ingredients");
                            for (DataSnapshot ingredientSnapshot : ingredientsSnapshot.getChildren()) {
                                String ingredientName = ingredientSnapshot.child("name").getValue(String.class);
                                if (ingredientName != null) {
                                    ingredientSet.add(ingredientName.toLowerCase());
                                }
                            }
                        }
                        allIngredientNames = new ArrayList<>(ingredientSet);
                        Collections.sort(allIngredientNames);
                        adapter.setIngredients(allIngredientNames);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Pantry", "Error fetching recipes: " + databaseError.getMessage());
                    }
                });
    }
}
