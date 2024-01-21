package com.example.cabinetchef;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIfetch {
    @GET("recipes/complexSearch")
    Call<RecipeResponse> getRecipes(@Query("apiKey") String apiKey);

    @GET("products/search")
    Call<ProductResponse> getProducts(@Query("apiKey") String apiKey, @Query("query") String query);
}
