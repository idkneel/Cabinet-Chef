package com.example.cabinetchef;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.cabinetchef.APIfetch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cabinetchef.databinding.FragmentFirstBinding;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private APIfetch service;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(APIfetch.class);

        fetchRecipes();

        return binding.getRoot();
    }

    private void fetchRecipes() {
        service.getRecipes("cb765e381a874b6abf2f6f605c92ecec").enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

                    databaseRef.child("recipes").setValue(response.body().getResults());

                } else {

                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
