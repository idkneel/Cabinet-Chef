package com.example.cabinetchef;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabinetchef.Recipe.Recipe;
import com.example.cabinetchef.Recipe.RecipeDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.w3c.dom.Document;

import java.util.Map;
import java.util.Random;
import java.util.Set;


public class FavoritesScreen extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private ImageView recipeImage;
    private TextView recipeTitle;
    private TextView allergenWarning;
    private EditText searchEditText;

    RecyclerView recyclerView;
    ArrayList<Recipe> recipeArrayList;
    RecipeAdapter recipeAdapter;
    private FirebaseFirestore fStore;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the content view to the home_screen layout
        setContentView(R.layout.home_screen);

        recipeImage = findViewById(R.id.recipeImage);
        recipeTitle = findViewById(R.id.recipeTitle);
        allergenWarning = findViewById(R.id.allergenWarning);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes");

        // Finding and setting up the button to show the screen selection popup
        Button showScreenSelectButton = findViewById(R.id.showPopupButton);
        Button showFilterPopupButton = findViewById(R.id.showFiltersButton);

        displayRandomRecipe();

        searchEditText = findViewById(R.id.searchEditText);
    }

    private void displayRandomRecipe() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Recipe> recipes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipes.add(recipe);
                }

                if (!recipes.isEmpty()) {
                    Random random = new Random();
                    Recipe randomRecipe = recipes.get(random.nextInt(recipes.size()));
                    displayRecipe(randomRecipe);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FavoritesScreen", "Failed to read recipe", error.toException());
            }
        });
    }

    private void displayRecipe(Recipe recipe) {
        if (recipe != null) {

            getRecipe(recipe);

            Glide.with(this).load(recipe.getImage()).into(recipeImage);
            recipeTitle.setText(recipe.getTitle());

            // Check if the recipe contains any allergens
            if (containsAllergens(recipe)) {
                allergenWarning.setVisibility(View.VISIBLE);
            } else {
                allergenWarning.setVisibility(View.GONE);
            }

            // Set up click listener for the recipe details
            View.OnClickListener recipeClickListener = v -> {
                Intent intent = new Intent(FavoritesScreen.this, CookingScreen.class);
                intent.putExtra("RECIPE_IMAGE", recipe.getImage());
                intent.putExtra("RECIPE_TITLE", recipe.getTitle());
                intent.putExtra("RECIPE_TIME", recipe.getReadyInMinutes());

                Gson gson = new Gson();
                String instructionsJson = gson.toJson(recipe.getInstructions());
                Type ingredientListType = new TypeToken<List<RecipeDetail.Ingredient>>(){}.getType();
                String ingredientsJson = gson.toJson(recipe.getIngredients(), ingredientListType);

                intent.putExtra("RECIPE_INSTRUCTIONS_JSON", instructionsJson);
                intent.putExtra("RECIPE_INGREDIENTS_JSON", ingredientsJson);

                startActivity(intent);
            };

            recipeImage.setOnClickListener(recipeClickListener);
            recipeTitle.setOnClickListener(recipeClickListener);
        }
    }

    private boolean containsAllergens(Recipe recipe) {
        Set<String> userAllergens = getUserAllergens();
        for (RecipeDetail.Ingredient ingredient : recipe.getIngredients()) {
            if (userAllergens.contains(ingredient.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private Set<String> getUserAllergens() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        return sharedPreferences.getStringSet("allergens", new HashSet<>());
    }

    private void getRecipe(Recipe recipe) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();



//        Map<String, Object> recipeDetail = new HashMap<>();

//        fStore.collection("users")
//                .document(userID)
//                .collection("Favorites")
//                .whereNotEqualTo(userID, true)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
    }
}