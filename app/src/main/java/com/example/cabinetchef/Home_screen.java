package com.example.cabinetchef;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home_screen extends AppCompatActivity {

    private PopupWindow popupWindow;
    private View popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        popupView = LayoutInflater.from(this).inflate(R.layout.screen_select_popup, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupWindow.setBackgroundDrawable(null);

        initializeButtons();
    }

    private void initializeButtons() {
        Button showScreenSelectButton = findViewById(R.id.showPopupButton);
        showScreenSelectButton.setOnClickListener(v -> showScreenSelectPopup());

        Button showFilterPopupButton = findViewById(R.id.showFiltersButton);
        showFilterPopupButton.setOnClickListener(v -> showFilterPopup());

        Button fetchDataButton = findViewById(R.id.firebaseTestButton);
        fetchDataButton.setOnClickListener(v -> fetchDataAndSaveToFirebase());
    }

    private Recipe convertToRecipe(RecipeDetail detail) {
        List<String> ingredients = detail.getExtendedIngredients().stream()
                .map(ingredient -> ingredient.getName() + " - " + ingredient.getAmount() + " " + ingredient.getUnit())
                .collect(Collectors.toList());
        String instructions = detail.getInstructions();

        return new Recipe(detail.getTitle(), ingredients, detail.getReadyInMinutes(), detail.getImage(), Arrays.asList(instructions.split("\\r?\\n")));
    }

    private void fetchDataAndSaveToFirebase() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpoonacularService service = retrofit.create(SpoonacularService.class);

        // Example of fetching recipes based on user input or predefined queries
        String[] queries = {"pasta", "salad", "chicken", "beef"};
        for (String query : queries) {
            service.getRecipes("YOUR_API_KEY", query).enqueue(new Callback<RecipesResponse>() {
                @Override
                public void onResponse(Call<RecipesResponse> call, Response<RecipesResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (RecipeSummary summary : response.body().getResults()) {
                            fetchRecipeDetailsById(summary.getId(), service);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RecipesResponse> call, Throwable t) {
                    // Log failure
                }
            });
        }
    }

    private void fetchRecipeDetailsById(int recipeId, SpoonacularService service) {
        service.getRecipeDetails(recipeId, "cb765e381a874b6abf2f6f605c92ecec").enqueue(new Callback<RecipeDetail>() {
            @Override
            public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
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

    private void saveDataToFirebase(List<Recipe> recipes) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
        for (Recipe recipe : recipes) {
            databaseReference.push().setValue(recipe);
        }
    }

    private void showScreenSelectPopup() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.showAtLocation(rootView, Gravity.LEFT, 0, 0);
    }
    private void showFilterPopup() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.home_screen, null);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.showAtLocation(rootView, Gravity.LEFT, 0, 0);
    }

}