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
        Call<RecipesResponse> recipesCall = service.getRecipes("cb765e381a874b6abf2f6f605c92ecec", "pasta");

        recipesCall.enqueue(new Callback<RecipesResponse>() {
            @Override
            public void onResponse(Call<RecipesResponse> call, Response<RecipesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> recipes = response.body().getResults();
                    saveDataToFirebase(recipes);
                } else {
                    // log error later
                }
            }

            @Override
            public void onFailure(Call<RecipesResponse> call, Throwable t) {
                // log error later
            }
        });
    }
    private void saveDataToFirebase(List<Recipe> recipes) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
        for (Recipe recipe : recipes) {
            databaseReference.push().setValue(recipe);
        }
    }




}