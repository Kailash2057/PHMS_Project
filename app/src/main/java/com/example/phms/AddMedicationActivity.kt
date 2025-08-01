package com.example.phms

import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*


class AddMedicationActivity : AppCompatActivity() {

    private lateinit var db: MedicationDbHelper
    private lateinit var username: String
    private var medicationId: Int? = null

    private lateinit var editTextStart: TextInputEditText
    private lateinit var editTextEnd: TextInputEditText
    private lateinit var editTextTime: TextInputEditText
    private lateinit var editTextFrequency: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        setContentView(R.layout.activity_add_medication)

        db = MedicationDbHelper(this)
        username = intent.getStringExtra("username") ?: ""
        medicationId = intent.getIntExtra("medicationId", -1).takeIf { it != -1 }

        val editTextName = findViewById<EditText>(R.id.medName)
        val editTextDosage = findViewById<EditText>(R.id.medDosage)
        editTextFrequency = findViewById(R.id.medFrequency)
        editTextStart = findViewById(R.id.medStartDate)
        editTextEnd = findViewById(R.id.medEndDate)
        editTextTime = findViewById(R.id.medTime)
        val checkBoxReminder = findViewById<CheckBox>(R.id.reminder)
        val submitButton = findViewById<Button>(R.id.btnSaveMedication)

        editTextStart.setOnClickListener { showDatePickerDialog(editTextStart) }
        editTextEnd.setOnClickListener { showDatePickerDialog(editTextEnd) }
        editTextTime.setOnClickListener { showTimePickerDialog(editTextTime) }

        if (medicationId != null) {
            medicationId?.let { id ->
                val med = db.getMedicationById(id)
                editTextName.setText(med.name)
                editTextDosage.setText(med.dosage)
                editTextFrequency.setText(med.frequency)
                editTextStart.setText(med.startDate)
                editTextEnd.setText(med.endDate)
                editTextTime.setText(med.intakeTime)
                checkBoxReminder.isChecked = med.reminderSet
            }
        }

        submitButton.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val dosage = editTextDosage.text.toString().trim()
            val frequency = editTextFrequency.text.toString().trim()
            val start = editTextStart.text.toString().trim()
            val end = editTextEnd.text.toString().trim()
            val time = editTextTime.text.toString().trim()
            val reminder = checkBoxReminder.isChecked

            if (name.isEmpty() || dosage.isEmpty() || frequency.isEmpty() ||
                start.isEmpty() || end.isEmpty() || time.isEmpty()
            ) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val med = Medication(0, name, dosage, frequency, start, end, time, reminder, username)

            val success = if (medicationId == null) {
                db.insertMedication(med)
            } else {
                db.updateMedication(med)
            }

            if (success) {
                if (reminder) scheduleReminder(med)
                Toast.makeText(this, "Medication saved", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to save (duplicate name?)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog(targetEditText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                targetEditText.setText(formatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePickerDialog(targetEditText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val formatted = String.format("%02d:%02d", hourOfDay, minute)
                targetEditText.setText(formatted)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun scheduleReminder(med: Medication) {
        val intent = Intent(this, ReminderActivity::class.java).apply {
            putExtra("medName", med.name)
        }

        val timeParts = med.intakeTime.split(":")
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            set(Calendar.MINUTE, timeParts[1].toInt())
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            med.name.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Reminder notifications won't work without permission", Toast.LENGTH_LONG).show()
            }
        }
    }
}