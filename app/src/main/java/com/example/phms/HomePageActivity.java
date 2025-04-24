package com.example.phms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {

    TextView usernameDisplay;
    ImageView profileIcon;
    EditText searchBar;
    Button medicationsButton, vitalSignsButton, communicationButton, yourDataButton, dietButton, notesButton;

    DatabaseHelper db;
    String currentUsername; // To store username passed from Login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        searchBar = findViewById(R.id.editTextSearch);
        profileIcon = findViewById(R.id.imageViewProfile);
        usernameDisplay = findViewById(R.id.textViewUsername);

        medicationsButton = findViewById(R.id.buttonMedications);
        vitalSignsButton = findViewById(R.id.buttonVitalSigns);
        communicationButton = findViewById(R.id.buttonCommunication);
        yourDataButton = findViewById(R.id.buttonYourData);
        dietButton = findViewById(R.id.buttonDiet);
        notesButton = findViewById(R.id.buttonNotes);

        db = new DatabaseHelper(this);

        // Get current username from intent
        currentUsername = getIntent().getStringExtra("username");
        usernameDisplay.setText(currentUsername);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
                intent.putExtra("username", currentUsername); // pass username to ProfileActivity
                startActivity(intent);
            }
        });

        notesButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, NotesActivity.class);
            intent.putExtra("username", currentUsername);  // Send current user's username
            startActivity(intent);
        });

        vitalSignsButton.setOnClickListener(v -> {
            // Create an Intent to go to VitalSignActivity
            Intent intent = new Intent(HomePageActivity.this, VitalSignActivity.class);
            startActivity(intent);  // Start the activity
        });

        // Example: Set action on buttons (You can link these to new Activities later)
        medicationsButton.setOnClickListener(v -> Toast.makeText(this, "Medications clicked", Toast.LENGTH_SHORT).show());

        communicationButton.setOnClickListener(v -> Toast.makeText(this, "Communication clicked", Toast.LENGTH_SHORT).show());
        yourDataButton.setOnClickListener(v -> Toast.makeText(this, "Your Data clicked", Toast.LENGTH_SHORT).show());

        dietButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, DietActivity.class);
            startActivity(intent);  // Start the activity
        });
    }
}