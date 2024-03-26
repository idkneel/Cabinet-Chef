package com.example.cabinetchef;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Activity for managing user allergies.
 */
public class AllergiesActivity extends AppCompatActivity {

    private EditText allergyInput;
    private ArrayAdapter<String> allergenAdapter;
    private List<String> userAllergens = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allergies_activity);

        allergyInput = findViewById(R.id.allergyInput);
        ListView allergenListView = findViewById(R.id.allergenListView);
        Button backButton = findViewById(R.id.backButton);

        userAllergens = getAllergens();
        allergenAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userAllergens);
        allergenListView.setAdapter(allergenAdapter);

        Button addButton = findViewById(R.id.addAllergyButton);
        addButton.setOnClickListener(v -> addAllergen());
        backButton.setOnClickListener(view -> finish());

        allergenListView.setOnItemClickListener(this::deleteAllergen);
    }

    /**
     * Adds a new allergen to the list of user allergens.
     */
    private void addAllergen() {
        String allergen = allergyInput.getText().toString().trim();
        if (!allergen.isEmpty() && !userAllergens.contains(allergen)) {
            userAllergens.add(allergen);
            saveAllergens(userAllergens);
            allergenAdapter.notifyDataSetChanged();
            allergyInput.setText("");
        } else {
            Toast.makeText(this, "Allergen is already added or empty", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes an allergen from the list of user allergens.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked.
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    private void deleteAllergen(AdapterView<?> parent, View view, int position, long id) {
        String allergenToRemove = userAllergens.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.are_you_sure_popup_delete, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button confirmYes = dialogView.findViewById(R.id.confirmYes);
        Button confirmNo = dialogView.findViewById(R.id.confirmNo);

        confirmYes.setOnClickListener(v -> {
            userAllergens.remove(allergenToRemove);
            saveAllergens(userAllergens);
            allergenAdapter.notifyDataSetChanged();
            Toast.makeText(AllergiesActivity.this, "Allergen removed", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });

        confirmNo.setOnClickListener(v -> alertDialog.dismiss());
    }

    /**
     * Saves the list of user allergens in SharedPreferences.
     *
     * @param allergens List of user allergens to be saved.
     */
    private void saveAllergens(List<String> allergens) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("allergens", new HashSet<>(allergens));
        editor.apply();
    }

    /**
     * Retrieves the list of user allergens from SharedPreferences.
     *
     * @return List of user allergens.
     */
    private List<String> getAllergens() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        Set<String> allergenSet = sharedPreferences.getStringSet("allergens", new HashSet<>());
        return new ArrayList<>(allergenSet);
    }
}
