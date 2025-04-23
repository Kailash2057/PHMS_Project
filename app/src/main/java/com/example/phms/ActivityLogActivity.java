package com.example.phms; // Change this to your package name

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phms.R;

import java.util.ArrayList;

public class ActivityLogActivity extends AppCompatActivity {

    private ListView listViewLogs;
    private ArrayList<String> loginLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_log);

        listViewLogs = findViewById(R.id.listViewLogs);

        loginLogs = new ArrayList<>();

        android.content.SharedPreferences prefs = getSharedPreferences("LoginLogs", MODE_PRIVATE);
        String logs = prefs.getString("logs", "");

        if (!logs.isEmpty()) {
            String[] logsArray = logs.split(";"); // Split by ;
            for (String log : logsArray) {
                if (!log.isEmpty()) {
                    loginLogs.add(log);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                loginLogs
        );

        listViewLogs.setAdapter(adapter);
    }
}