package com.example.phms;

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CommunicationActivity : AppCompatActivity() {

    private lateinit var db: ContactDbHelper
    private lateinit var contactList: MutableList<Contact>
    private lateinit var adapter: ContactAdapter
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communication)

        username = intent.getStringExtra("username") ?: ""
        if (username.isEmpty()) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val searchView = findViewById<SearchView>(R.id.searchContacts)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterContacts(newText ?: "")
                return true
            }
        })

        db = ContactDbHelper(this)

        val recyclerView = findViewById<RecyclerView>(R.id.viewContacts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        contactList = mutableListOf()

        adapter = ContactAdapter(this, contactList, username, db)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.btnAddContacts).setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        contactList = db.getAllContacts(username).toMutableList()
        contactList.sortBy{it.name}
        adapter.updateData(contactList)
    }

    private fun filterContacts(query: String) {
        val filteredList = contactList.filter {
            it.name.contains(query, ignoreCase = true)
        }
        adapter.updateData(filteredList)
    }
}