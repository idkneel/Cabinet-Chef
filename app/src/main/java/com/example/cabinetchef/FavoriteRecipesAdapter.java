package com.example.cabinetchef;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cabinetchef.Recipe.Recipe;

import java.util.List;

public class FavoriteRecipesAdapter extends RecyclerView.Adapter<FavoriteRecipesAdapter.ViewHolder> {

    private List<Recipe> favoriteRecipesList;

    public FavoriteRecipesAdapter(List<Recipe> favoriteRecipesList) {
        this.favoriteRecipesList = favoriteRecipesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_recipe_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = favoriteRecipesList.get(position);

        // Bind recipe data to views
        holder.recipeTitle.setText(recipe.getTitle());
        Glide.with(holder.itemView).load(recipe.getImage()).into(holder.recipeImage);
    }

    @Override
    public int getItemCount() {
        return favoriteRecipesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeTitle = itemView.findViewById(R.id.recipeTitle);
        }
    }
}

