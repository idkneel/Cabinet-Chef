package com.example.cabinetchef;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SpoonacularService {
    @GET("recipes/complexSearch")
    Call<RecipesResponse> getRecipes(@Query("apiKey") String apiKey, @Query("query") String query);
}
