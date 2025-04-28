package com.example.phms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import android.widget.ImageButton;

public class YourDoctorActivity extends AppCompatActivity {

    private LinearLayout doctorLayout;
    private Doctor[] doctors = new Doctor[3];
    private String[] defaultInfo = {"N/A", "N/A", "N/A"};
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_doctor);

        doctorLayout = findViewById(R.id.doctor_layout);

        dbHelper = new DatabaseHelper(this);

        List<Doctor> doctorList = dbHelper.getAllDoctors();

        for (int i = 0; i < 3; i++) {
            if (i < doctorList.size()) {
                doctors[i] = doctorList.get(i);
            } else {
                doctors[i] = new Doctor(i + 1, defaultInfo[0], defaultInfo[1], defaultInfo[2]);
                dbHelper.addDoctor(doctors[i]);
            }
        }

        displayDoctors();
    }

    private void displayDoctors() {
        doctorLayout.removeAllViews();

        for (int i = 0; i < doctors.length; i++) {
            Doctor doctor = doctors[i];

            // ここでXMLファイル (item_doctor.xml) を読み込む！
            View doctorItem = getLayoutInflater().inflate(R.layout.item_doctor, doctorLayout, false);
            TextView nameTextView = doctorItem.findViewById(R.id.doctor_name);
            TextView emailTextView = doctorItem.findViewById(R.id.doctor_email);
            TextView phoneTextView = doctorItem.findViewById(R.id.doctor_phone);
            Button editButton = doctorItem.findViewById(R.id.edit_button);
            Button deleteButton = doctorItem.findViewById(R.id.delete_button);
            ImageButton consultButton = doctorItem.findViewById(R.id.consult_button);

            nameTextView.setText("Name: " + doctor.getName());
            emailTextView.setText("Email: " + doctor.getEmail());
            phoneTextView.setText("Phone: " + doctor.getPhone());

            int finalI = i;
            editButton.setOnClickListener(v -> editDoctorInfo(finalI));
            deleteButton.setOnClickListener(v -> deleteDoctorInfo(finalI));

            if (!doctor.getEmail().equals("N/A")) {
                consultButton.setVisibility(View.VISIBLE);
                consultButton.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{doctor.getEmail()});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Consultation Request");
                    startActivity(Intent.createChooser(intent, "Send Email"));
                });
            } else {
                consultButton.setVisibility(View.GONE);
            }

            doctorLayout.addView(doctorItem);
        }
    }


    private void editDoctorInfo(int index) {
        Doctor doctor = doctors[index];

        EditDoctorDialog dialog = new EditDoctorDialog(this, doctor, updatedDoctor -> {
            doctors[index] = updatedDoctor;
            dbHelper.updateDoctor(updatedDoctor);
            displayDoctors();
        });

        dialog.show();
    }

    private void deleteDoctorInfo(int index) {
        Doctor defaultDoctor = new Doctor(index + 1, defaultInfo[0], defaultInfo[1], defaultInfo[2]);
        doctors[index] = defaultDoctor;
        dbHelper.updateDoctor(defaultDoctor);
        displayDoctors();
        Toast.makeText(this, "Doctor info reset to default", Toast.LENGTH_SHORT).show();
    }
}
