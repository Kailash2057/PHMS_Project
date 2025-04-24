package com.example.phms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;

public class DietActivity extends AppCompatActivity {

    private ImageButton btnWeight, btnCalorie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);  // Make sure this is the correct XML layout file

        // Initialize the buttons
        btnWeight = findViewById(R.id.btnWeight);  // Button for Weight
        btnCalorie = findViewById(R.id.btnCalorie);  // Button for Calorie

        // Set onClickListeners for the buttons
        btnWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the Blood Pressure Activity
                Intent intent = new Intent(DietActivity.this, WeightActivity.class);
                startActivity(intent);
            }
        });

        btnCalorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the Glucose Activity
                Intent intent = new Intent(DietActivity.this, CalorieActivity.class);
                startActivity(intent);
            }
        });

    }
}