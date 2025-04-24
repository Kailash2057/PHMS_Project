package com.example.phms;

import android.os.Bundle
import android.widget.*
import android.content.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

public class MedicationActivity : AppCompatActivity(){
    private lateinit var db: MedicationDbHelper
    private lateinit var username: String
    private lateinit var medicationList: MutableList<Medication>
    private lateinit var adapter: ArrayAdapter<String>
    private val displayList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication)

        username = intent.getStringExtra("username") ?: ""
        if(username.isEmpty()){
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        db = MedicationDbHelper(this)
        val listView = findViewById<ListView>(R.id.lvMedications)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, i, _ ->
            val med = medicationList[i]
            val intent = Intent(this, ViewMedicationActivity::class.java)
            intent.putExtra("medicationId", med.id)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAddMedication).setOnClickListener {
            val intent = Intent(this, AddMedicationActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        medicationList = db.getAllMedications(username).toMutableList()
        displayList.clear()
        displayList.addAll(
            medicationList.map {
                "${it.name}\nDosage: ${it.dosage}\nIntake Time: ${it.intakeTime}"
            }
        )
        adapter.notifyDataSetChanged()
    }

}
