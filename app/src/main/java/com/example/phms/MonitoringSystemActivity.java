package com.example.phms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;

public class MonitoringSystemActivity extends AppCompatActivity {

    private ImageButton yourDoctorButton;
    private ImageButton checkMedicineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_system);

        yourDoctorButton = findViewById(R.id.yourDoctorButton);
        checkMedicineButton = findViewById(R.id.checkMedicineButton);

        yourDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonitoringSystemActivity.this, YourDoctorActivity.class);
                startActivity(intent);
            }
        });

        checkMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonitoringSystemActivity.this, CheckIntakeMedicineActivity.class);
                startActivity(intent);
            }
        });
    }
}

