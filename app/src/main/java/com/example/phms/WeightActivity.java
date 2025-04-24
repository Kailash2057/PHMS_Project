package com.example.phms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Set;

public class WeightActivity extends AppCompatActivity {

    EditText weightInput;
    TextView selectedDateText, historyTextView;
    CalendarView calendarView;
    Button addButton;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);  // Link to your layout XML

        // Initialize views
        weightInput = findViewById(R.id.weightInput);
        selectedDateText = findViewById(R.id.selectedDateText);
        calendarView = findViewById(R.id.calendarView);
        addButton = findViewById(R.id.WeightBtn);
        historyTextView = findViewById(R.id.historyTextView);  // TextView for displaying history

        // Initialize SharedPreferences to store weight
        sharedPreferences = getSharedPreferences("WeightPrefs", MODE_PRIVATE);

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
            String weightValue = weightInput.getText().toString();
            String selectedDate = selectedDateText.getText().toString();

            // Check if weight is already recorded for the selected date
            if (isWeightAlreadyRecorded(selectedDate)) {
                // If the weight for this date is already recorded, don't add again
                return;
            }

            // Proceed if the user hasn't recorded weight for the day yet
            if (!weightValue.isEmpty()) {
                // Save the new weight for the selected date
                saveWeightForDate(selectedDate, Float.parseFloat(weightValue));

                // Display the updated history
                displayWeightHistory();
            }
        });

        // Display the initial weight history
        displayWeightHistory();
    }

    private void updateSelectedDateText(long timeInMillis) {
        // Format the selected date to a readable format (month/day)
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Format the date as month/day (e.g., 4/24)
        String formattedDate = month + "/" + day;
        selectedDateText.setText(formattedDate);
    }

    // Check if weight has already been recorded for the selected date
    private boolean isWeightAlreadyRecorded(String date) {
        return sharedPreferences.contains(date);  // Returns true if the date already exists in SharedPreferences
    }

    // Save the weight data for a given date in SharedPreferences
    private void saveWeightForDate(String date, float weight) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(date, weight);  // Store the weight value for this date
        editor.apply();  // Apply the changes asynchronously
    }

    // Display the history of all stored weight entries in the historyTextView
    private void displayWeightHistory() {
        StringBuilder history = new StringBuilder();

        // Iterate over all the stored dates and their weight values
        Set<String> dateKeys = sharedPreferences.getAll().keySet();

        for (String dateKey : dateKeys) {
            float weight = sharedPreferences.getFloat(dateKey, 0);
            history.append(dateKey).append(": ").append(weight).append(" lb\n");
        }

        // Update the TextView with the weight history
        historyTextView.setText(history.toString());
    }
}
