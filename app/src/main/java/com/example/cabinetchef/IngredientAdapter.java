    package com.example.cabinetchef;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;
    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.cabinetchef.Recipe.RecipeDetail.Ingredient;

    import java.util.ArrayList;
    import java.util.List;

    public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
        private List<String> ingredientNames = new ArrayList<>();

        public void setIngredients(List<String> ingredientNames) {
            this.ingredientNames = ingredientNames;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_layout, parent, false);
            return new IngredientViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
            String ingredientName = ingredientNames.get(position);
            holder.nameTextView.setText(ingredientName);
        }

        @Override
        public int getItemCount() {
            return ingredientNames.size();
        }

        static class IngredientViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;

            public IngredientViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.ingredientName);
            }
        }
    }

