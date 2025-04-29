package com.example.phms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.Toast;

public class MonitoringSystemActivity extends AppCompatActivity {

    private ImageButton yourDoctorButton;
    private ImageButton checkMedicineButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_system);

        yourDoctorButton = findViewById(R.id.yourDoctorButton);
        checkMedicineButton = findViewById(R.id.checkMedicineButton);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        if (username != null) {
            Toast.makeText(this, "Username: " + username, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Username not received", Toast.LENGTH_SHORT).show();
        }

        yourDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username != null) {
                    Intent intent = new Intent(MonitoringSystemActivity.this, YourDoctorActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Toast.makeText(MonitoringSystemActivity.this, "Please login again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username != null) {
                    Intent intent = new Intent(MonitoringSystemActivity.this, CheckIntakeMedicineActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Toast.makeText(MonitoringSystemActivity.this, "Please login again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    }

