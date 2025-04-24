package com.example.phms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class YourDoctorActivity extends AppCompatActivity {

    private LinearLayout doctorLayout;
    private Doctor[] doctors = new Doctor[3];
    private String[] defaultInfo = {"N/A", "N/A", "N/A"};
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_doctor);

        doctorLayout = findViewById(R.id.doctor_layout);  // XMLにこのIDが必要！

        dbHelper = new DatabaseHelper(this);

        // データベースから情報を取得
        List<Doctor> doctorList = dbHelper.getAllDoctors();

        // 初期化：最大3人。無ければデフォルトで埋める
        for (int i = 0; i < 3; i++) {
            if (i < doctorList.size()) {
                doctors[i] = doctorList.get(i);
            } else {
                doctors[i] = new Doctor(i + 1, defaultInfo[0], defaultInfo[1], defaultInfo[2]);
                dbHelper.addDoctor(doctors[i]);  // デフォルト登録しておく
            }
        }

        displayDoctors();
    }

    private void displayDoctors() {
        doctorLayout.removeAllViews();

        for (int i = 0; i < doctors.length; i++) {
            Doctor doctor = doctors[i];

            LinearLayout doctorItem = new LinearLayout(this);
            doctorItem.setOrientation(LinearLayout.VERTICAL);
            doctorItem.setPadding(20, 20, 20, 20);

            TextView nameTextView = new TextView(this);
            nameTextView.setText("Name: " + doctor.getName());

            TextView emailTextView = new TextView(this);
            emailTextView.setText("Email: " + doctor.getEmail());

            TextView phoneTextView = new TextView(this);
            phoneTextView.setText("Phone: " + doctor.getPhone());

            Button editButton = new Button(this);
            editButton.setText("Edit");
            int finalI = i;
            editButton.setOnClickListener(v -> editDoctorInfo(finalI));

            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(v -> deleteDoctorInfo(finalI));

            doctorItem.addView(nameTextView);
            doctorItem.addView(emailTextView);
            doctorItem.addView(phoneTextView);
            doctorItem.addView(editButton);
            doctorItem.addView(deleteButton);

            doctorLayout.addView(doctorItem);
        }
    }

    private void editDoctorInfo(int index) {
        Doctor doctor = doctors[index];

        EditDoctorDialog dialog = new EditDoctorDialog(this, doctor, updatedDoctor -> {
            doctors[index] = updatedDoctor;
            dbHelper.updateDoctor(updatedDoctor);  // データベース更新
            displayDoctors();
        });

        dialog.show();
    }

    private void deleteDoctorInfo(int index) {
        Doctor defaultDoctor = new Doctor(index + 1, defaultInfo[0], defaultInfo[1], defaultInfo[2]);
        doctors[index] = defaultDoctor;
        dbHelper.updateDoctor(defaultDoctor);  // デフォルトで上書き
        displayDoctors();
        Toast.makeText(this, "Doctor info reset to default", Toast.LENGTH_SHORT).show();
    }
}
