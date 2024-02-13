package com.example.cabinetchef;

import android.content.Intent;
import android.os.Bundle;

import com.example.cabinetchef.Listeners.SpoonacularService;
import com.example.cabinetchef.Login.Login;
import com.example.cabinetchef.Recipe.Recipe;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.example.cabinetchef.Recipe.RecipeDetail;
import com.example.cabinetchef.Recipe.RecipeSummary;
import com.example.cabinetchef.Recipe.RecipesResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import android.widget.Button;
import android.widget.TextView;

// MainActivity class definition
import java.util.Arrays;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // Firebase authentication object
    FirebaseAuth auth;

    // TextView for displaying user details
    FirebaseDatabase database;
    Button button;
    TextView textView;
    // FirebaseUser object for the current user
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the content view to the layout of the main activity
        setContentView(R.layout.activity_main);

        // Initializing the FirebaseAuth instance
        auth = FirebaseAuth.getInstance();
        // Finding the logout button by its ID
        Button logout_button = findViewById(R.id.logout);
        // Finding the TextView for user details by its ID
        database = FirebaseDatabase.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        // Getting the current user from FirebaseAuth
        user = auth.getCurrentUser();

        // Checking if the user is null (i.e., not logged in)
        if (user == null) {
            // If user is not logged in, redirect to the Login activity
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            // Finishing the current activity to remove it from the activity stack
            finish();
        } else {
            // If the user is logged in, display the user's email in the textView
            textView.setText(user.getEmail());
        }

        Button testFirebaseButton = findViewById(R.id.firebaseTestButton);
        testFirebaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchDataAndSaveToFirebase();
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            // Setting an onClickListener for the logout button
            @Override
            public void onClick(View view) {
                // Signing out the user
                FirebaseAuth.getInstance().signOut();
                // Redirecting to the Login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                // Finishing the current activity
                finish();
            }
        });

        // Finding the home screen button by its ID and setting its onClickListener
        Button homeScreenButton = findViewById(R.id.home_screen_button);
        homeScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to navigate to the Home_screen activity
                Intent intent = new Intent(MainActivity.this, Home_screen.class);
                startActivity(intent);
            }
        });
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Button addButton = findViewById(R.id.firebaseTestButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchDataAndSaveToFirebase();
            }
        });

        DatabaseReference recipesRef = database.getReference("recipes");
        recipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    //use or display recipes - later
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //errors - later
            }
        });
    }
    private void fetchDataAndSaveToFirebase() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpoonacularService service = retrofit.create(SpoonacularService.class);

        String[] searchQueries = {"pasta", "salad", "chicken", "beef", "vegetarian", "soup", "dessert"};

        for (String query : searchQueries) {
            Call<RecipesResponse> call = service.getRecipes("cb765e381a874b6abf2f6f605c92ecec", query);
            call.enqueue(new Callback<RecipesResponse>() {
                @Override
                public void onResponse(Call<RecipesResponse> call, Response<RecipesResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (RecipeSummary summary : response.body().getResults()) {
                            fetchRecipeDetailsById(summary.getId(), service);
                        }
                    } else {
                        // ??API error 2 fuck this shit
                    }
                }

                @Override
                public void onFailure(Call<RecipesResponse> call, Throwable t) {
                    // API error
                }
            });
        }
    }

    private void fetchRecipeDetailsById(int recipeId, SpoonacularService service) {
        Call<RecipeDetail> recipeDetailCall = service.getRecipeDetails(recipeId, "cb765e381a874b6abf2f6f605c92ecec");
        recipeDetailCall.enqueue(new Callback<RecipeDetail>() {
            @Override
            public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                if (response.isSuccessful()) {
                    RecipeDetail detail = response.body();

                    Recipe recipe = convertToRecipe(detail);
                    saveDataToFirebase(Arrays.asList(recipe));
                } else {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<RecipeDetail> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private Recipe convertToRecipe(RecipeDetail detail) {
        List<String> ingredients = detail.getExtendedIngredients().stream()
                .map(ingredient -> ingredient.getName() + ": " + ingredient.getAmount() + " " + ingredient.getUnit())
                .collect(Collectors.toList());
        List<String> instructions = Arrays.asList(detail.getInstructions().split("\n"));

        return new Recipe(detail.getTitle(), ingredients, detail.getReadyInMinutes(), detail.getImage(), instructions);
    }

    private void saveDataToFirebase(List<Recipe> recipes) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
        for (Recipe recipe : recipes) {
            databaseReference.push().setValue(recipe);
        }
    }




}