package com.example.phms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MonitoringSystemActivity extends AppCompatActivity {

    private Button yourDoctorButton;
    private Button checkMedicineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_system);  // layout XML ファイルを読み込む

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

