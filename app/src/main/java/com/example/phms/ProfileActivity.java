package com.example.phms;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView textViewUsername, textViewFirstName, textViewMiddleName, textViewLastName,
            textViewGender, textViewDOB, textViewPhone, textViewEmail, textViewAddress;
    Button activityLogButton;
    DatabaseHelper db;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textViewUsername = findViewById(R.id.textViewUsername);
        textViewFirstName = findViewById(R.id.textViewFirstName);
        textViewMiddleName = findViewById(R.id.textViewMiddleName);
        textViewLastName = findViewById(R.id.textViewLastName);
        textViewGender = findViewById(R.id.textViewGender);
        textViewDOB = findViewById(R.id.textViewDOB);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewAddress = findViewById(R.id.textViewAddress);
        activityLogButton = findViewById(R.id.buttonActivityLog); // Initialize button here
        Button changePasswordButton = findViewById(R.id.buttonChangePassword);

        db = new DatabaseHelper(this);

        username = getIntent().getStringExtra("username");

        User user = fetchUser(username);

        if (user != null) {
            textViewUsername.setText(user.getUsername());
            textViewFirstName.setText("First Name: " + user.getFirstName());
            textViewMiddleName.setText("Middle Name: " + user.getMiddleName());
            textViewLastName.setText("Last Name: " + user.getLastName());
            textViewGender.setText("Gender: " + user.getGender());
            textViewDOB.setText("Date of Birth: " + user.getDob());
            textViewPhone.setText("Phone: " + user.getPhone());
            textViewEmail.setText("Email: " + user.getEmail());
            textViewAddress.setText("Country: " + user.getAddress());
        }

        activityLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ActivityLogActivity.class);
                intent.putExtra("username", username); // Pass correct username
                startActivity(intent);
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ForgotPasswordActivity.class);
                intent.putExtra("username", username); // pass current username if needed
                startActivity(intent);
            }
        });
    }

    private User fetchUser(String username) {
        Cursor cursor = db.getUserDetails(username);
        if (cursor != null && cursor.moveToFirst()) {
            return new User(
                    cursor.getString(1), // firstName
                    cursor.getString(2), // middleName
                    cursor.getString(3), // lastName
                    cursor.getString(4), // gender
                    cursor.getString(5), // dob
                    cursor.getString(6), // phone
                    cursor.getString(7), // email
                    cursor.getString(8), // address
                    cursor.getString(9), // username
                    cursor.getString(10) // password
            );
        }
        return null;
    }
}