package com.example.cabinetchef;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.cabinetchef.Recipe.Recipe;
import com.example.cabinetchef.Recipe.RecipeDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Random;


public class CookingScreen extends AppCompatActivity {
    FirebaseFirestore fStore;
    Recipe recipe = new Recipe();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cooking_screen);


        Button finishCookingButton = findViewById(R.id.finishCooking);
        Button addToFavoritesButton = findViewById(R.id.addToFavorites);

        finishCookingButton.setOnClickListener(v -> {
            startActivity(new Intent(CookingScreen.this, Home_screen.class));
        });

        addToFavoritesButton.setOnClickListener( v -> {

            updateData(recipe);
        });

    }


    private void updateData(Recipe recipe) {
        // Create a map to hold the update, in this case, the new "Household members" value
        Map<String, Object> userDetail = new HashMap<>();

        Intent intent = new Intent(CookingScreen.this, RecipeDetailActivity.class);
        String image = recipe.getImage();
        String title = recipe.getTitle();
        String time = String.valueOf(recipe.getReadyInMinutes());

        Gson gson = new Gson();
        String instructionsJson = gson.toJson(recipe.getInstructions());

        Type ingredientListType = new TypeToken<List<RecipeDetail.Ingredient>>(){}.getType();
        String ingredientsJson = gson.toJson(recipe.getIngredients(), ingredientListType);

        intent.putExtra("RECIPE_INSTRUCTIONS_JSON", instructionsJson);
        intent.putExtra("RECIPE_INGREDIENTS_JSON", ingredientsJson);

        startActivity(intent);

        userDetail.put("Recipe Name", title);
        userDetail.put("Recipe image", image);
        userDetail.put("Ready in (Minutes)", time);
        userDetail.put("Ingredients", ingredientsJson);
        userDetail.put("Ingredients Type", ingredientListType);

        // Initialize Firestore instance
        fStore = FirebaseFirestore.getInstance();

        // Get the current user's ID from FirebaseAuth (assuming user is logged in)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the specific user's document in the "users" collection
        DocumentReference userDocRef = fStore.collection("favorite recipes").document(userId);

        // Try to get the document for the current user
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // If the document exists, update it with the new details
                    userDocRef.update(userDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Success: Show a success message
                            Log.d(TAG, "successfully Updated");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failure: Show an error message
                            Log.d(TAG, "Error updating document");
                        }
                    });
                } else {
                    // If the document does not exist (fallback scenario),
                    // attempt to find the document by "Household members" field and old number
                    fStore.collection("favorite recipes")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        // If successful and documents are found,
                                        // get the first document and its ID
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        String documentID = documentSnapshot.getId();
                                        // Update the found document with the new details
                                        fStore.collection("favorite recipes")
                                                .document(documentID)
                                                .update(userDetail)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        // Success: Show a success message
                                                        Log.d(TAG, "successfully Updated");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Failure: Show an error message
                                                        Log.d(TAG, "Some error occurred");
                                                    }
                                                });
                                    } else {
                                        Log.d(TAG, "Failure to find document");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // If there was an error accessing the document, show an error message
                                    Log.d(TAG, "Error accessing document");
                                }
                            });
                }
            }
        });
    }
}