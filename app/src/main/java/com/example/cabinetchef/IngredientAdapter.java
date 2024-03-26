package com.example.cabinetchef;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private List<String> ingredientNames = new ArrayList<>();
    private Map<String, Boolean> checkedStates;
    private SharedPreferences sharedPreferences;
    private OnIngredientClickListener listener;

    public IngredientAdapter(SharedPreferences sharedPreferences, OnIngredientClickListener listener) {
        this.sharedPreferences = sharedPreferences;
        this.listener = listener;
        this.checkedStates = new HashMap<>();
    }

    public void setIngredients(List<String> ingredientNames) {
        this.ingredientNames = ingredientNames;
        for (String ingredient : ingredientNames) {
            checkedStates.put(ingredient, sharedPreferences.getBoolean(ingredient, false));
        }
        notifyDataSetChanged();
    }

    public void updateIngredientCheckedState(String ingredient, boolean isChecked) {
        checkedStates.put(ingredient, isChecked);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ingredient, isChecked);
        editor.apply();
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
        holder.ingredientCheckbox.setOnCheckedChangeListener(null); // Detach listener before setting checked state
        holder.ingredientCheckbox.setChecked(checkedStates.getOrDefault(ingredientName, false));
        holder.ingredientCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkedStates.put(ingredientName, isChecked);
            if (listener != null) {
                listener.onIngredientClick(ingredientName, isChecked);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ingredientName, isChecked);
            editor.apply();
        });
    }


    @Override
    public int getItemCount() {
        return ingredientNames.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        CheckBox ingredientCheckbox;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.ingredientName);
            ingredientCheckbox = itemView.findViewById(R.id.ingredientCheckbox);
        }
    }

    public interface OnIngredientClickListener {
        void onIngredientClick(String ingredient, boolean isInPantry);
    }
}

