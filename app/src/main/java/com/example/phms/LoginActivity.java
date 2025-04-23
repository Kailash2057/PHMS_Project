package com.example.phms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton, registerLink;
    TextView forgotUsernameLink, forgotPasswordLink;
    DatabaseHelper db;
    int attemptsLeft = 3;  // User has 3 chances

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        forgotUsernameLink = findViewById(R.id.textViewForgotUsername);
        forgotPasswordLink = findViewById(R.id.textViewForgotPassword);
        registerLink = findViewById(R.id.buttonRegister);

        // Initially hide the Forgot Password link
        forgotUsernameLink.setVisibility(View.GONE);
        forgotPasswordLink.setVisibility(View.GONE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = usernameEditText.getText().toString().trim();
                String pass = passwordEditText.getText().toString().trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    boolean checkUser = db.checkUsernamePassword(user, pass);
                    if (checkUser) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        // Save login time
                        saveLoginTime();

                        if (db.isPasswordChanged(user)) {
                            // User already changed password, go to HomeActivity
                            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                            intent.putExtra("username", user);
                            startActivity(intent);
                        } else {
                            // Force user to change password first
                            Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                            intent.putExtra("username", user);
                            startActivity(intent);
                        }
                        finish(); // Finish LoginActivity after successful login
                    } else {
                        attemptsLeft--;
                        if (attemptsLeft == 0) {
                            loginButton.setEnabled(false);  // Disable login button after maximum attempts
                            Toast.makeText(LoginActivity.this, "Incorrect Credentials. Maximum attempts reached!", Toast.LENGTH_LONG).show();
                            forgotPasswordLink.setVisibility(View.VISIBLE); // Show Forgot Password link
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect Credentials. Attempts left: " + attemptsLeft, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        forgotUsernameLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotUsernameActivity.class));
            }
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
    private void saveLoginTime() {
        String currentTime = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
        android.content.SharedPreferences prefs = getSharedPreferences("LoginLogs", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();

        // Fetch existing logins
        String existingLogs = prefs.getString("logs", "");

        // Add new login
        String updatedLogs = currentTime + ";" + existingLogs;

        editor.putString("logs", updatedLogs);
        editor.apply();
    }
}