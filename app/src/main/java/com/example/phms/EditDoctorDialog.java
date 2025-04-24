package com.example.phms;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditDoctorDialog extends Dialog {

    private EditText nameEditText, emailEditText, phoneEditText;
    private Button saveButton;
    private Doctor doctor;
    private OnDoctorEditedListener listener;

    public EditDoctorDialog(Context context, Doctor doctor, OnDoctorEditedListener listener) {
        super(context);
        this.doctor = doctor;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_doctor);

        nameEditText = findViewById(R.id.edit_name);
        emailEditText = findViewById(R.id.edit_email);
        phoneEditText = findViewById(R.id.edit_phone);
        saveButton = findViewById(R.id.save_button);

        nameEditText.setText(doctor.getName());
        emailEditText.setText(doctor.getEmail());
        phoneEditText.setText(doctor.getPhone());

        saveButton.setOnClickListener(v -> {
            String updatedName = nameEditText.getText().toString().trim();
            String updatedEmail = emailEditText.getText().toString().trim();
            String updatedPhone = phoneEditText.getText().toString().trim();

            if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty()) {
                // 全てのフィールドが埋まっていない場合
                return;
            }

            doctor.setName(updatedName);
            doctor.setEmail(updatedEmail);
            doctor.setPhone(updatedPhone);

            listener.onDoctorEdited(doctor);
            dismiss();
        });
    }

    public interface OnDoctorEditedListener {
        void onDoctorEdited(Doctor updatedDoctor);
    }
}
