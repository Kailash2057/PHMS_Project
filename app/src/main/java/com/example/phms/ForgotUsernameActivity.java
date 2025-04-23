package com.example.phms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotUsernameActivity extends AppCompatActivity {

    EditText emailEditText, securityAnswerEditText;
    Button findUsernameButton;
    TextView usernameDisplay, loginLink;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_username);

        emailEditText = findViewById(R.id.editTextEmail);
        securityAnswerEditText = findViewById(R.id.editTextSecurityAnswer);
        findUsernameButton = findViewById(R.id.buttonFindUsername);
        usernameDisplay = findViewById(R.id.textViewUsernameDisplay);
        loginLink = findViewById(R.id.textViewLoginLink);

        db = new DatabaseHelper(this);

        findUsernameButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String securityAnswer = securityAnswerEditText.getText().toString();

            if (email.isEmpty() || securityAnswer.isEmpty()) {
                Toast.makeText(ForgotUsernameActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String username = db.getUsernameByEmailAndSecurityAnswer(email, securityAnswer);
            if (username != null) {
                usernameDisplay.setText("Your username is: " + username);
            } else {
                usernameDisplay.setText("No account found with provided details.");
            }
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(ForgotUsernameActivity.this, LoginActivity.class));
            finish();
        });
    }
}