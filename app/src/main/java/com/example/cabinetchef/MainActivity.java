package com.example.cabinetchef;

import android.content.Intent;
import android.os.Bundle;

import com.example.cabinetchef.Login.Login;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cabinetchef.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    Button button;
    TextView textView;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        Button testFirebaseButton = findViewById(R.id.firebaseTestButton);
        testFirebaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchDataAndSaveToFirebase();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
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