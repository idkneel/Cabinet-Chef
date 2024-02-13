package com.example.cabinetchef;

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



