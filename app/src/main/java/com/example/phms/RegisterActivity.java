package com.example.phms;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    EditText firstNameEditText, middleNameEditText, lastNameEditText, dobEditText, phoneEditText,
            emailEditText, addressEditText, usernameEditText, passwordEditText;
    RadioGroup genderRadioGroup;
    EditText securityAnswerEditText;
    Spinner securityQuestionSpinner;
    Button registerButton;
    TextView loginLink;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        // Initialize Views
        firstNameEditText = findViewById(R.id.editTextFirstName);
        middleNameEditText = findViewById(R.id.editTextMiddleName);
        lastNameEditText = findViewById(R.id.editTextLastName);
        phoneEditText = findViewById(R.id.editTextPhone);
        emailEditText = findViewById(R.id.editTextEmail);
        addressEditText = findViewById(R.id.editTextCountry);
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        dobEditText = findViewById(R.id.editTextDOB);
        genderRadioGroup = findViewById(R.id.radioGroupGender);
        registerButton = findViewById(R.id.buttonRegister);
        loginLink = findViewById(R.id.textViewLogin);
        securityQuestionSpinner = findViewById(R.id.spinnerSecurityQuestion);
        securityAnswerEditText = findViewById(R.id.editTextSecurityAnswer);

        // Set up Date Picker
        dobEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RegisterActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String dob = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dobEditText.setText(dob);
                    },
                    year, month, day
            );
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        // Set up Spinner
        String[] securityQuestions = {
                "Select a Security Question",
                "In what city were you born?",
                "What is your first pet's name?",
                "What is your mother's mother first name?",
                "What was your first school's name?",
                "What is your favorite food?",
                "In which city did your parents meet?"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, securityQuestions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        securityQuestionSpinner.setAdapter(adapter);

        // Register Button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString().trim();
                String middleName = middleNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String dob = dobEditText.getText().toString().trim();
                String securityQuestion = securityQuestionSpinner.getSelectedItem().toString();
                String securityAnswer = securityAnswerEditText.getText().toString().trim();

                int genderId = genderRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedGender = findViewById(genderId);
                String gender = (selectedGender != null) ? selectedGender.getText().toString() : "";

                // Validation for mandatory fields
                if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() ||
                        email.isEmpty() || address.isEmpty() || username.isEmpty() ||
                        password.isEmpty() || gender.isEmpty() || dob.isEmpty() ||
                        securityAnswer.isEmpty() || securityQuestion.equals("Select a Security Question")) {
                    Toast.makeText(RegisterActivity.this, "Please fill all mandatory fields correctly", Toast.LENGTH_SHORT).show();
                }
                // Validate age (user must be 18 or older)
                else if (!isValidAge(dob)) {
                    Toast.makeText(RegisterActivity.this, "You must be 18 years or older to register", Toast.LENGTH_SHORT).show();
                }
                // Validate email format
                else if (!isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                }
                // Validate names (only alphabets)
                else if (!isValidName(firstName)) {
                    Toast.makeText(RegisterActivity.this, "First name must contain alphabets only", Toast.LENGTH_SHORT).show();
                }
                else if (!isValidName(lastName)) {
                    Toast.makeText(RegisterActivity.this, "Last name must contain alphabets only", Toast.LENGTH_SHORT).show();
                }
                // Validate address format
                else if (!isValidAddress(address)) {
                    Toast.makeText(RegisterActivity.this, "Address contains invalid characters", Toast.LENGTH_SHORT).show();
                }
                // Validate phone number format
                else if (!isValidPhone(phone)) {
                    Toast.makeText(RegisterActivity.this, "Phone number must be in valid format (+CountryCodePhoneNumber)", Toast.LENGTH_SHORT).show();
                }
                // Validate username format
                else if (!isValidUsername(username)) {
                    Toast.makeText(RegisterActivity.this, "Username must be at least 8 characters long and contain only letters and numbers", Toast.LENGTH_SHORT).show();
                }
                // Validate password format
                else if (!isValidPassword(password)) {
                    Toast.makeText(RegisterActivity.this, "Password must meet all criteria", Toast.LENGTH_LONG).show();
                }
                // Check if username or email already exists in the database
                else {
                    if (db.checkUsername(username)) {
                        Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    } else if (db.checkEmail(email)) {
                        Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean insert = db.insertUser(firstName, middleName, lastName, gender, dob,
                                phone, email, address, username, password, securityQuestion, securityAnswer);
                        if (insert) {
                            Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


        // Login Link
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    // Validation for Name Fields (first name, middle name, last name)
    private boolean isValidName(String name) {
        // Allows alphabets, spaces, apostrophes, and hyphens
        return name.matches("[a-zA-Z\\s'-]+");
    }

    // Validation for Address (allows alphanumeric characters, spaces, commas, periods, and hyphens)
    private boolean isValidAddress(String address) {
        // Allows alphabets, numbers, spaces, commas, periods, and hyphens
        return address.matches("[a-zA-Z0-9\\s,.'-]+");
    }

    // Validation for phone number format
    private boolean isValidPhone(String phone) {
        // Allows optional '+' followed by digits
        return phone.matches("^\\+?[0-9]{1,}[0-9]{6,}$");
    }

    private boolean isValidUsername(String username) {
        return username.matches("[A-Za-z0-9]{8,}");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 &&
                password.matches(".*[A-Z].*") &&  // at least one uppercase
                password.matches(".*[a-z].*") &&  // at least one lowercase
                password.matches(".*\\d.*") &&    // at least one digit
                password.matches(".*[@#$%^&+=!].*"); // at least one special character
    }
    private boolean isValidAge(String dob) {
        try {
            // Date format is expected to be dd/mm/yyyy
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date birthDate = sdf.parse(dob);
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            calendar.setTime(birthDate);
            int birthYear = calendar.get(Calendar.YEAR);
            int birthMonth = calendar.get(Calendar.MONTH);
            int birthDay = calendar.get(Calendar.DAY_OF_MONTH);

            int age = currentYear - birthYear;
            if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
                age--;
            }

            return age >= 18;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}