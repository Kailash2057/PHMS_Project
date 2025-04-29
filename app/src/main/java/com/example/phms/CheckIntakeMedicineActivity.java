package com.example.phms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;
import android.content.SharedPreferences;



public class CheckIntakeMedicineActivity extends AppCompatActivity {

    private LinearLayout medicationLayout;
    private MedicationDbHelper medDbHelper;
    private DatabaseHelper dbHelper;
    private String username;
    private List<Medication> medications;
    private Map<Integer, Boolean> intakeStatus;
    private Map<Integer, String> medicationTimes; // MedicationごとのintakeTime記録用
    private Handler handler;
    private EmailSender emailSender;
    private MedicationIntakeDbHelper medIntakeDbHelper;
    private static final String PREFS_NAME = "MedicationPrefs";
    private static final String KEY_INTAKE_STATUS = "IntakeStatus";
    private Map<String, List<String>> dangerousCombinations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_intake_medicine);

        medicationLayout = findViewById(R.id.medication_layout);
        medDbHelper = new MedicationDbHelper(this);
        dbHelper = new DatabaseHelper(this);
        intakeStatus = new HashMap<>();
        medicationTimes = new HashMap<>();
        handler = new Handler();
        emailSender = new EmailSender(this);
        medIntakeDbHelper = new MedicationIntakeDbHelper(this);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        if (username == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        medications = medDbHelper.getAllMedications(username);
        loadIntakeStatus();
        displayMedications();
        initializeDangerousCombinations();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // SharedPreferencesから前回の保存日付を取得
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String lastCheckedDate = prefs.getString("lastCheckedDate", "");

        // 今日の日付を取得
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        // 前回の日付と今日の日付を比較
        if (!today.equals(lastCheckedDate)) {
            // 日付が変わった場合、intakeStatusをリセット
            resetIntakeStatus();

            // SharedPreferencesに今日の日付を保存
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("lastCheckedDate", today);
            editor.apply();
        }
    }

    private void resetIntakeStatus() {
        // intakeStatusの状態をリセットし、ボタンを再度有効にする
        for (Medication med : medications) {
            boolean taken = intakeStatus.getOrDefault(med.getId(), false);
            if (taken) {
                intakeStatus.put(med.getId(), false); // 以前に摂取済みの場合、状態をリセット
            }
        }

        // ボタンを有効にするために再描画する
        displayMedications();
    }

    private void loadIntakeStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        for (Medication med : medications) {
            boolean taken = prefs.getBoolean(KEY_INTAKE_STATUS + med.getId(), false);
            intakeStatus.put(med.getId(), taken);
        }
    }

    private void displayMedications() {
        medicationLayout.removeAllViews();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        for (Medication med : medications) {
            if (isWithinDateRange(today, med.getStartDate(), med.getEndDate())) {
                View medItem = getLayoutInflater().inflate(R.layout.list_item_medication, medicationLayout, false);

                TextView nameTextView = medItem.findViewById(R.id.medication_name);
                TextView dateTextView = medItem.findViewById(R.id.medication_date);
                TextView timeTextView = medItem.findViewById(R.id.medication_time);
                Button intakeButton = medItem.findViewById(R.id.intake_button);

                nameTextView.setText("Medicine: " + med.getName());
                dateTextView.setText("Date: " + today);
                timeTextView.setText("Intake Time: " + med.getIntakeTime());

                boolean taken = intakeStatus.getOrDefault(med.getId(), false);
                intakeButton.setEnabled(!taken);
                intakeButton.setBackgroundColor(taken ? getResources().getColor(R.color.gray) : getResources().getColor(R.color.teal_200));

                intakeButton.setOnClickListener(v -> {
                    boolean conflictFound = false;

                    for (Integer takenMedId : intakeStatus.keySet()) {
                        if (intakeStatus.getOrDefault(takenMedId, false)) {
                            String takenMedName = "";
                            for (Medication m : medications) {
                                if (m.getId() == takenMedId) {
                                    takenMedName = m.getName();
                                    break;
                                }
                            }

                            List<String> dangerousList = dangerousCombinations.get(med.getName());
                            if (dangerousList != null && dangerousList.contains(takenMedName)) {
                                conflictFound = true;

                                break;
                            }
                        }
                    }

                    if (conflictFound) {
                        Toast.makeText(this, "Warning: Dangerous combination detected!", Toast.LENGTH_LONG).show();
                        sendDangerousMedicinesIntakeEmail(med);
                    }


                    intakeStatus.put(med.getId(), true);
                    intakeButton.setEnabled(false);
                    intakeButton.setBackgroundColor(getResources().getColor(R.color.gray));
                    Toast.makeText(this, med.getName() + " marked as Intake.", Toast.LENGTH_SHORT).show();

                    // Save the intake status to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(KEY_INTAKE_STATUS + med.getId(), true);
                    editor.apply();

                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    String currentDateTime = dateTimeFormat.format(new Date());
                    medIntakeDbHelper.insertMedicationIntake(med.getId(), username, currentDateTime);
                });

                medicationLayout.addView(medItem);

                medicationTimes.put(med.getId(), med.getIntakeTime());
                scheduleIntakeCheck(med);
            }
        }
    }

    private boolean isWithinDateRange(String currentDate, String startDate, String endDate) {
        return currentDate.compareTo(startDate) >= 0 && currentDate.compareTo(endDate) <= 0;
    }

    private void scheduleIntakeCheck(Medication med) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date now = new Date();
            Date intakeTime = timeFormat.parse(med.getIntakeTime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);

            Calendar intakeCalendar = Calendar.getInstance();
            intakeCalendar.setTime(intakeTime);
            intakeCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            intakeCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            intakeCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));

            long delay = intakeCalendar.getTimeInMillis() - calendar.getTimeInMillis();
            if (delay < 0) {
                delay = 0;
            }

            handler.postDelayed(() -> {
                if (!intakeStatus.getOrDefault(med.getId(), false)) {
                    sendMissedIntakeEmail(med);
                }
            }, delay);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMissedIntakeEmail(Medication med) {
        String userEmail = dbHelper.getUserEmail(username);

        String subject = "Missed Medication Intake Notification";
        String body = "User " + username + " missed intake of " + med.getName() + " at " + med.getIntakeTime() + ".";

        emailSender.sendEmail(userEmail, subject, body);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Doctor> validDoctors = dbHelper.getValidDoctors();

        if (validDoctors.isEmpty()) {
            Toast.makeText(this, "No valid doctors found", Toast.LENGTH_SHORT).show();
        } else {
            for (Doctor doctor : validDoctors) {
                String doctorEmail = doctor.getEmail();
                emailSender.sendEmail(doctorEmail, subject, body);
            }
        }
    }

    private void sendDangerousMedicinesIntakeEmail(Medication med) {

        String subject = "Intaking Dangerous Combination of Medications Notification";
        String body = "User " + username + " intaked dangerous combination of medicines.";

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Doctor> validDoctors = dbHelper.getValidDoctors();

        if (validDoctors.isEmpty()) {
            Toast.makeText(this, "No valid doctors found", Toast.LENGTH_SHORT).show();
        } else {
            for (Doctor doctor : validDoctors) {
                String doctorEmail = doctor.getEmail();
                emailSender.sendEmail(doctorEmail, subject, body);
            }
        }
    }

    private void checkAndResetIntakeStatus() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        for (Map.Entry<Integer, Boolean> entry : intakeStatus.entrySet()) {
            if (entry.getValue() == true) {
                intakeStatus.put(entry.getKey(), false);
            }
        }
    }

    private void initializeDangerousCombinations() {
        dangerousCombinations = new HashMap<>();

        dangerousCombinations.put("Warfarin", Arrays.asList("NSAIDs"));
        dangerousCombinations.put("NSAIDs", Arrays.asList("Warfarin"));

        dangerousCombinations.put("ACE Inhibitors", Arrays.asList("Potassium Supplements"));
        dangerousCombinations.put("Potassium Supplements", Arrays.asList("ACE Inhibitors"));

        dangerousCombinations.put("Benzodiazepines", Arrays.asList("Opioids"));
        dangerousCombinations.put("Opioids", Arrays.asList("Benzodiazepines"));

        dangerousCombinations.put("SSRIs", Arrays.asList("MAO Inhibitors"));
        dangerousCombinations.put("MAO Inhibitors", Arrays.asList("SSRIs"));
    }
}
