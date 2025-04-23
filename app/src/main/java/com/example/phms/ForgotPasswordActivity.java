package com.example.phms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailEditText, securityAnswerEditText, newPasswordEditText, confirmPasswordEditText;
    Button resetPasswordButton;
    Spinner securityQuestionSpinner;
    TextView loginLink;
    DatabaseHelper db;

    // Predefined list of security questions
    List<String> securityQuestions = Arrays.asList(
            "Select a Security Question",
            "In what city were you born?",
            "What is your first pet's name?",
            "What is your mother's mother first name?",
            "What was your first school's name?",
            "What is your favorite food?",
            "In which city did your parents meet?"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Views
        emailEditText = findViewById(R.id.editTextEmail);  // Changed from editTextUsername to editTextEmail
        securityAnswerEditText = findViewById(R.id.editTextSecurityAnswer);
        newPasswordEditText = findViewById(R.id.editTextNewPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        resetPasswordButton = findViewById(R.id.buttonResetPassword);
        securityQuestionSpinner = findViewById(R.id.spinnerSecurityQuestion);
        loginLink = findViewById(R.id.textViewLoginLink);

        db = new DatabaseHelper(this);

        // Set up the Spinner with predefined questions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, securityQuestions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        securityQuestionSpinner.setAdapter(adapter);

        // When the email is entered, check if it matches a valid user
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = emailEditText.getText().toString();
                if (!email.isEmpty()) {
                    // Check if the email exists in the database
                    boolean exists = db.checkEmail(email); // Changed method from checkUsername to checkEmail
                    if (!exists) {
                        Toast.makeText(ForgotPasswordActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Reset Password Button Click
        resetPasswordButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String selectedQuestion = securityQuestionSpinner.getSelectedItem().toString();
            String securityAnswer = securityAnswerEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Validate email format
            if (email.isEmpty() || !isValidEmail(email)) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate all fields
            if (securityAnswer.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate that a proper security question is selected
            if (selectedQuestion.equals("Select a Security Question")) {
                Toast.makeText(ForgotPasswordActivity.this, "Please select a security question", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate security question and answer
            if (!db.validateEmailSecurityQuestionAndAnswer(email, selectedQuestion, securityAnswer)) {
                Toast.makeText(ForgotPasswordActivity.this, "Security question or answer is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ForgotPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update password
            boolean updated = db.updatePasswordByEmail(email, newPassword);
            if (updated) {
                Toast.makeText(ForgotPasswordActivity.this, "Password reset successful! Please login.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "Error resetting password", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate back to login page
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });
    }

    // Email validation using regular expression
    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }
}