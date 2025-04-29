package com.example.phms;

import android.os.Bundle
import android.widget.*
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

public class MedicationActivity : AppCompatActivity(){
    private lateinit var db: MedicationDbHelper
    private lateinit var username: String
    private lateinit var medicationList: MutableList<Medication>
    private lateinit var adapter: MedicationAdapter

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
        val recyclerView = findViewById<RecyclerView>(R.id.viewMedications)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MedicationAdapter(this)
        recyclerView.adapter = adapter
        refreshList()

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setIconifiedByDefault(false)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText.orEmpty())
                return true
            }
        })

        findViewById<FloatingActionButton>(R.id.btnAddMedications).setOnClickListener {
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
        adapter.setData(medicationList)
    }

    private fun filterList(query:String){
        val filteredList = medicationList.filter{ it.name.contains(query, ignoreCase = true)}
        adapter.setData(filteredList)
    }

}
