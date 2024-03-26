package com.example.cabinetchef;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// This class represents the activity for displaying utensils screen
public class UtensilsScreen extends AppCompatActivity {

    // This method is called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.utensils_screen); // Set the layout for this activity

        // Find the back button from the layout
        Button backButton = findViewById(R.id.backButton);

        // Set OnClickListener to the back button
        backButton.setOnClickListener(v -> {
            finish(); // Close the current activity and go back
        });
    }
}
