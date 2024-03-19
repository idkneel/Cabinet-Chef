package com.example.cabinetchef;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CookingScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cooking_screen);


        Button finishCookingButton = findViewById(R.id.finishCooking);


        finishCookingButton.setOnClickListener(v -> {
            startActivity(new Intent(CookingScreen.this, Home_screen.class));
        });


    }
}
