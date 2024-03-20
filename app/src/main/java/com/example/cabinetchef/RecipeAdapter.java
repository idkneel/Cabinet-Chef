package com.example.cabinetchef;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cabinetchef.Recipe.Recipe;
import com.example.cabinetchef.Recipe.RecipeDetail;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipes;
    private Context context;
    private LayoutInflater inflater;
    private List<String> userAllergens;

    public RecipeAdapter(Context context, List<Recipe> recipes, List<String> userAllergens) {
        this.context = context;
        this.recipes = recipes;
        this.userAllergens = userAllergens;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeTitle.setText(recipe.getTitle());
        Glide.with(context).load(recipe.getImage()).into(holder.recipeImage);

        // Check for allergens
        boolean containsAllergens = false;
        for (String allergen : userAllergens) {
            for (RecipeDetail.Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.getName().equalsIgnoreCase(allergen)) {
                    containsAllergens = true;
                    break;
                }
            }
            if (containsAllergens) {
                break;
            }
        }

        if (containsAllergens) {
            holder.allergenWarning.setVisibility(View.VISIBLE);
            holder.allergenWarning.setText("Careful! Contains allergen product");
        } else {
            holder.allergenWarning.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_IMAGE", recipe.getImage());
            intent.putExtra("RECIPE_TITLE", recipe.getTitle());
            // Add other details as needed
            context.startActivity(intent);
        });
    }




    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public void setAllergen(String allergen) {
        this.userAllergens = new ArrayList<>();
        if (allergen != null && !allergen.isEmpty()) {
            this.userAllergens.add(allergen.toLowerCase());
        }
        notifyDataSetChanged();
    }


    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        ImageView recipeImage;
        TextView allergenWarning;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipeTitle);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            allergenWarning = itemView.findViewById(R.id.allergenWarning);
        }
    }

}
