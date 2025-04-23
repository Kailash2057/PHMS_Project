package com.example.phms;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NoteDetailActivity extends AppCompatActivity {

    TextView typeTextView, titleTextView, descriptionTextView;
    EditText typeEditText, titleEditText, descriptionEditText;
    Button editButton, doneButton, deleteButton;
    DatabaseHelper dbHelper;
    private int noteId;
    String username, noteType, noteTitle, noteDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        dbHelper = new DatabaseHelper(this);

        typeTextView = findViewById(R.id.typeTextView);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        typeEditText = findViewById(R.id.typeEditText);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        editButton = findViewById(R.id.editButton);
        doneButton = findViewById(R.id.doneButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Receive data from Intent
        Intent intent = getIntent();
        //      noteId = intent.getIntExtra("note_id", -1);
        noteId = getIntent().getIntExtra("noteId", -1);
        if (noteId == -1) {
            Toast.makeText(this, "Error loading note", Toast.LENGTH_SHORT).show();
            finish();
        }
        username = intent.getStringExtra("username");
        noteType = intent.getStringExtra("type");
        noteTitle = intent.getStringExtra("title");
        noteDescription = intent.getStringExtra("description");

        // Set initial values
        typeTextView.setText("Type: " + noteType);
        titleTextView.setText(noteTitle);
        descriptionTextView.setText(noteDescription);

        typeEditText.setText(noteType);
        titleEditText.setText(noteTitle);
        descriptionEditText.setText(noteDescription);

        // Initially, hide EditTexts and Done button
        typeEditText.setVisibility(View.GONE);
        titleEditText.setVisibility(View.GONE);
        descriptionEditText.setVisibility(View.GONE);
        doneButton.setVisibility(View.GONE);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterEditMode();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });
    }

    private void enterEditMode() {
        // Hide TextViews
        typeTextView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        descriptionTextView.setVisibility(View.GONE);

        // Show EditTexts and Done button
        typeEditText.setVisibility(View.VISIBLE);
        titleEditText.setVisibility(View.VISIBLE);
        descriptionEditText.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.VISIBLE);

        // Hide Edit button
        editButton.setVisibility(View.GONE);
    }

    private void saveChanges() {
        String updatedType = typeEditText.getText().toString().trim();
        String updatedTitle = titleEditText.getText().toString().trim();
        String updatedDescription = descriptionEditText.getText().toString().trim();

        if (updatedType.isEmpty() || updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.updateNote(noteId, updatedTitle, updatedDescription, updatedType);

        if (success) {
            Toast.makeText(this, "Note updated successfully!", Toast.LENGTH_SHORT).show();

            // Update the TextViews
            typeTextView.setText(updatedType);
            titleTextView.setText(updatedTitle);
            descriptionTextView.setText(updatedDescription);

            // Exit edit mode
            typeEditText.setVisibility(View.GONE);
            titleEditText.setVisibility(View.GONE);
            descriptionEditText.setVisibility(View.GONE);
            doneButton.setVisibility(View.GONE);

            typeTextView.setVisibility(View.VISIBLE);
            titleTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Failed to update note.", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteNote() {
        boolean success = dbHelper.deleteNote(noteId);

        if (success) {
            Toast.makeText(this, "Note deleted successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close this activity and go back
        } else {
            Toast.makeText(this, "Failed to delete note.", Toast.LENGTH_SHORT).show();
        }
    }
}