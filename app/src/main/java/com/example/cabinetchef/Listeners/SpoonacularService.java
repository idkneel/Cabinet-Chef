package com.example.cabinetchef.Listeners;

import com.example.cabinetchef.Recipe.RecipeDetail;
import com.example.cabinetchef.Recipe.RecipesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularService {
    @GET("recipes/complexSearch")
    Call<RecipesResponse> getRecipes(@Query("apiKey") String apiKey, @Query("query") String query);

    @GET("recipes/{id}/information")
    Call<RecipeDetail> getRecipeDetails(@Path("id") int recipeId, @Query("apiKey") String apiKey);
}


