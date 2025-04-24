package com.example.phms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;

public class VitalSignActivity extends AppCompatActivity {

    private ImageButton btnBloodPressure, btnGlucose, btnCholesterol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitalsigns);  // Make sure this is the correct XML layout file

        // Initialize the buttons
        btnBloodPressure = findViewById(R.id.btnBloodPressure);  // Button for Blood Pressure
        btnGlucose = findViewById(R.id.btnGlucose);  // Button for Glucose
        btnCholesterol = findViewById(R.id.btnCholesterol);  // Button for Cholesterol

        // Set onClickListeners for the buttons
        btnBloodPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the Blood Pressure Activity
                Intent intent = new Intent(VitalSignActivity.this, BloodPressureActivity.class);
                startActivity(intent);
            }
        });

        btnGlucose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the Glucose Activity
                Intent intent = new Intent(VitalSignActivity.this, GlucoseActivity.class);
                startActivity(intent);
            }
        });

        btnCholesterol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the Cholesterol Activity
                Intent intent = new Intent(VitalSignActivity.this, CholesterolActivity.class);
                startActivity(intent);
            }
        });
    }
}
