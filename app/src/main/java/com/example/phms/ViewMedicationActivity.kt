package com.example.phms

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ViewMedicationActivity : AppCompatActivity() {

    private lateinit var db: MedicationDbHelper
    private lateinit var username: String
    private var medicationId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_medication)

        db = MedicationDbHelper(this)
        username = intent.getStringExtra("username") ?: ""
        medicationId = intent.getIntExtra("medicationId", -1)

        val med = db.getAllMedications(username).find { it.id == medicationId }

        if (med == null) {
            Toast.makeText(this, "Medication not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val detailsText = findViewById<TextView>(R.id.medDetailsText)
        detailsText.text = """
            Name: ${med.name}
            Dosage: ${med.dosage}
            Frequency: ${med.frequency}
            Start Date: ${med.startDate}
            End Date: ${med.endDate}
            Intake Time: ${med.intakeTime}
            Reminder Set: ${med.reminderSet}
        """.trimIndent()

        findViewById<Button>(R.id.btnEditMed).setOnClickListener {
            val intent = Intent(this, AddMedicationActivity::class.java)
            intent.putExtra("medicationId", med.id)
            intent.putExtra("username", username)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnDeleteMed).setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Delete ${med.name}?")
                setMessage("Are you sure you want to delete this medication?")
                setPositiveButton("Yes") { _, _ ->
                    db.deleteMedication(med.name, username)
                    Toast.makeText(this@ViewMedicationActivity, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                setNegativeButton("Cancel", null)
                show()
            }
        }
    }
}