package com.example.cabinetchef;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabinetchef.Recipe.RecipeDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class Pantry extends AppCompatActivity {

    private RecyclerView ingredientsRecyclerView;
    private IngredientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_screen);

        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView);
        adapter = new IngredientAdapter();
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRecyclerView.setAdapter(adapter);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        fetchSimpleIngredients();
    }

    private void fetchSimpleIngredients() {
        FirebaseDatabase.getInstance().getReference().child("recipes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> allIngredientNames = new ArrayList<>();
                        for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                            DataSnapshot ingredientsSnapshot = recipeSnapshot.child("ingredients");
                            for (DataSnapshot ingredientSnapshot : ingredientsSnapshot.getChildren()) {
                                String ingredientName = ingredientSnapshot.child("name").getValue(String.class);
                                if (ingredientName != null) {
                                    allIngredientNames.add(ingredientName);
                                }
                            }
                        }
                        adapter.setIngredients(allIngredientNames);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Pantry", "Error fetching recipes: " + databaseError.getMessage());
                    }
                });
    }






}
