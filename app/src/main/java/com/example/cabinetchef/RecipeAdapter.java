
package com.example.cabinetchef;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cabinetchef.Recipe.Recipe;
import com.example.cabinetchef.Recipe.RecipeDetail;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> mRecipeList;
    private OnItemClickListener listener;

    // Constructor and other methods...
    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        mRecipeList = recipeList;
    }

    // Method to set item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Inner interface to handle item clicks
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe currentItem = mRecipeList.get(position);
        holder.bind(currentItem); // Bind recipe data to views

        // Set up click listener for the item
        holder.itemView.setOnClickListener(v -> {
            // Check if the listener is not null
            if (listener != null) {
                // Invoke the onItemClick method of the listener interface
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(clickedPosition);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private ImageView recipeImage;
        private TextView recipeTitle;
        private TextView allergenWarning;

        private List<String> userAllergens;
        private Context context;


        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeTitle = itemView.findViewById(R.id.recipeTitle);
            allergenWarning = itemView.findViewById(R.id.allergenWarning);
        }


        public void bind(Recipe recipe) {
            // Bind recipe data to views
            recipeTitle.setText(recipe.getTitle());

            // Load image into recipeImage using Glide
            if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(recipe.getImage())
                        .placeholder(R.drawable.app_logo_background)
                        .error(R.mipmap.app_logo)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e("Glide", "Failed to load image: " + e != null ? e.getMessage() : "");
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(recipeImage);
            } else {
                // Set a placeholder image if image URL is empty or null
                recipeImage.setImageResource(R.drawable.app_logo_background);
            }

            // Check if userAllergens is null or empty
            if (userAllergens == null || userAllergens.isEmpty()) {
                allergenWarning.setVisibility(View.GONE); // Hide allergen warning if no allergens are set
            } else {
                // Check if the recipe contains any allergens and show/hide allergen warning accordingly
                if (containsAllergens(recipe)) {
                    allergenWarning.setVisibility(View.VISIBLE);
                } else {
                    allergenWarning.setVisibility(View.GONE);
                }
            }
        }


        // Method to check if a recipe contains allergens
        private boolean containsAllergens(Recipe recipe) {
            if (userAllergens == null || userAllergens.isEmpty()) {
                return false; // No allergens set, so recipe cannot contain allergens
            }

            List<String> ingredientNames = new ArrayList<>();
            for (RecipeDetail.Ingredient ingredient : recipe.getIngredients()) {
                ingredientNames.add(ingredient.getName().toLowerCase());
            }
            for (String allergen : userAllergens) {
                if (ingredientNames.contains(allergen.toLowerCase())) {
                    return true; // Recipe contains allergen
                }
            }
            return false; // Recipe does not contain allergen
        }




    }
}
