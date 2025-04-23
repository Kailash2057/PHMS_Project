package com.example.phms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewNotesActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch;
    private RecyclerView recyclerViewNotes;
    private NotesAdapter notesAdapter;
    private DatabaseHelper db;
    private String currentUsername;
    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);

        db = new DatabaseHelper(this);
        currentUsername = getIntent().getStringExtra("username");

        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        noteList = db.getAllNotes(currentUsername);
        notesAdapter = new NotesAdapter(this, noteList);
        recyclerViewNotes.setAdapter(notesAdapter);

        buttonSearch.setOnClickListener(v -> {
            String keyword = editTextSearch.getText().toString().trim();
            searchNotes(keyword);
        });
    }

    private void searchNotes(String keyword) {
        List<Note> filteredList = new ArrayList<>();
        for (Note note : noteList) {
            if (note.getTitle() != null && note.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(note);
            }
        }
        notesAdapter.updateNotes(filteredList);
    }
}