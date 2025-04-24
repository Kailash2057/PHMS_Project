package com.example.phms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Set;

public class CalorieActivity extends AppCompatActivity {

    EditText calorieInput;
    TextView selectedDateText, historyTextView;
    CalendarView calendarView;
    Button addButton, deleteButton;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie);  // Link to your layout XML

        // Initialize views
        calorieInput = findViewById(R.id.calorieInput);
        selectedDateText = findViewById(R.id.selectedDateText);
        historyTextView = findViewById(R.id.historyTextView);  // TextView for displaying history
        calendarView = findViewById(R.id.calendarView);
        addButton = findViewById(R.id.CalorieBtn);
        deleteButton = findViewById(R.id.deleteButton);  // Button to clear all history

        // Initialize SharedPreferences to store calories
        sharedPreferences = getSharedPreferences("CaloriePrefs", MODE_PRIVATE);

        // Set the CalendarView to today's date
        long currentTime = System.currentTimeMillis();
        calendarView.setDate(currentTime, true, true); // Set to current date (today)

        // Set default date to today's date
        updateSelectedDateText(currentTime);

        // Listener for CalendarView date selection
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Format the selected date into a readable format
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            updateSelectedDateText(selectedDate.getTimeInMillis());
        });

        // Handle ADD button functionality
        addButton.setOnClickListener(v -> {
            String calorieValue = calorieInput.getText().toString();
            if (!calorieValue.isEmpty()) {
                // Get the selected date in the correct format
                String selectedDate = selectedDateText.getText().toString();

                // Get the previous calorie count for this date from SharedPreferences
                int previousCalories = getStoredCaloriesForDate(selectedDate);

                // Add the new calorie value to the previous value
                int newCalories = previousCalories + Integer.parseInt(calorieValue);

                // Save the updated calorie value
                saveCaloriesForDate(selectedDate, newCalories);

                // Display the updated history
                displayCalorieHistory();

                // Clear the input field
                calorieInput.setText("");
            }
        });

        // Handle DELETE button functionality
        deleteButton.setOnClickListener(v -> {
            clearAllHistory();  // Clear all history when the delete button is pressed
        });

        // Display the initial calorie history
        displayCalorieHistory();
    }

    private void updateSelectedDateText(long timeInMillis) {
        // Format the selected date to the desired format (e.g., "Apr 24, 2025")
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        // Format the date as "Apr 24, 2025"
        String formattedDate = String.format("%s %d, %d", getMonthString(month), day, year);
        selectedDateText.setText(formattedDate);
    }

    // Helper method to get month name (e.g., 1 -> Jan, 2 -> Feb)
    private String getMonthString(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month - 1];
    }

    // Get the stored calorie data for a given date from SharedPreferences
    private int getStoredCaloriesForDate(String date) {
        // Get the stored calorie count for this date. Default is 0 if not found.
        return sharedPreferences.getInt(date, 0);
    }

    // Save the calorie data for a given date in SharedPreferences
    private void saveCaloriesForDate(String date, int calories) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(date, calories);  // Store the calorie count for this date
        editor.apply();  // Apply the changes asynchronously
    }

    // Clear all history by removing all saved data in SharedPreferences
    private void clearAllHistory() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Remove all data from SharedPreferences
        editor.apply();  // Apply the changes asynchronously
        Toast.makeText(CalorieActivity.this, "All calorie history cleared.", Toast.LENGTH_SHORT).show();

        // Clear the displayed history
        historyTextView.setText("");
    }

    // Display the history of all stored calorie entries in the historyTextView
    private void displayCalorieHistory() {
        StringBuilder history = new StringBuilder();

        // Iterate over all the stored dates and their calorie values
        Set<String> dateKeys = sharedPreferences.getAll().keySet();

        for (String dateKey : dateKeys) {
            int calories = sharedPreferences.getInt(dateKey, 0);
            history.append(dateKey).append(": ").append(calories).append(" cal\n");
        }

        // Update the TextView with the calorie history
        historyTextView.setText(history.toString());

    }
}