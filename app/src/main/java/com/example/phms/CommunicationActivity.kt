package com.example.phms;

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CommunicationActivity : AppCompatActivity() {

    private lateinit var db: ContactDbHelper
    private lateinit var contactList: MutableList<Contact>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var username: String
    private val displayList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communication)

        username = intent.getStringExtra("username") ?: ""
        if (username.isEmpty()) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db = ContactDbHelper(this)
        val listView = findViewById<ListView>(R.id.lvContacts)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, _ ->
            val contact = contactList[position]

            val intent = Intent(this, ViewContactActivity::class.java)
            intent.putExtra("contactId", contact.id)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAddContact).setOnClickListener {
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
        displayList.clear()
        displayList.addAll(contactList.map { it.name })
        adapter.notifyDataSetChanged()
    }
}