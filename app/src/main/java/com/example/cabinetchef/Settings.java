package com.example.cabinetchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetchef.Login.Login;
import com.example.cabinetchef.R;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);

        Button logout_button = findViewById(R.id.logout);
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity and go back
            }
        });

        // Setting an onClickListener for the logout button
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // Signing out the user
                FirebaseAuth.getInstance().signOut();
                // Redirecting to the Login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                // Finishing the current activity
                finish();
            }
        });
    }
}
