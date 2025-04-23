package com.example.phms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    private EditText noteTitleEditText, noteDescriptionEditText;
    private Spinner noteTypeSpinner;
    private Button createNoteButton, viewNotesButton;
    private DatabaseHelper db;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        db = new DatabaseHelper(this);
        currentUsername = getIntent().getStringExtra("username");

        noteTitleEditText = findViewById(R.id.editTextNoteTitle);
        noteDescriptionEditText = findViewById(R.id.editTextNoteDescription);
        noteTypeSpinner = findViewById(R.id.spinnerNoteType);
        createNoteButton = findViewById(R.id.buttonCreateNote);
        viewNotesButton = findViewById(R.id.buttonViewNotes);

        setupNoteTypeSpinner();

        createNoteButton.setOnClickListener(v -> createNote());

        viewNotesButton.setOnClickListener(v -> {
            Intent intent = new Intent(NotesActivity.this, ViewNotesActivity.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });
    }

    private void setupNoteTypeSpinner() {
        String[] noteTypes = {"Recipe", "Diet", "Health Article", "General"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, noteTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        noteTypeSpinner.setAdapter(adapter);
    }

    private void createNote() {
        String title = noteTitleEditText.getText().toString();
        String description = noteDescriptionEditText.getText().toString();
        String type = noteTypeSpinner.getSelectedItem().toString();

        if (!title.isEmpty() && !description.isEmpty()) {
            boolean isInserted = db.createNewNote(currentUsername, title, description, type);
            if (isInserted) {
                Toast.makeText(this, "Note Created", Toast.LENGTH_SHORT).show();
                clearInputs();
            } else {
                Toast.makeText(this, "Failed to Create Note", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter both title and description", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        noteTitleEditText.setText("");
        noteDescriptionEditText.setText("");
        noteTypeSpinner.setSelection(0);
    }
}