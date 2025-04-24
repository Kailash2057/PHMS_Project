package com.example.phms

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddContactActivity : AppCompatActivity() {

    private lateinit var db: ContactDbHelper
    private lateinit var username: String
    private var contactId = -1 // -1 means new contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val editTextName = findViewById<EditText>(R.id.contactName)
        val editTextEmail = findViewById<EditText>(R.id.contactEmail)
        val editTextPhone = findViewById<EditText>(R.id.contactPhone)
        val btnSave = findViewById<Button>(R.id.btnSaveContact)
        val btnCancel = findViewById<Button>(R.id.btnCancelContact)


        username = intent.getStringExtra("username") ?: ""
        if (username.isEmpty()) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db = ContactDbHelper(this)

        contactId = intent.getIntExtra("contactId", -1)
        if (contactId != -1) {
            val contact = db.getContactById(contactId)
            contact?.let {
                editTextName.setText(it.name)
                editTextEmail.setText(it.email)
                editTextPhone.setText(it.phone)
            }
        }

        btnSave.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val phone = editTextPhone.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!phone.all { it.isDigit() }) {
                Toast.makeText(this, "Phone number must be digits only", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contact = Contact(contactId, name, email, phone, username)
            val success = if (contactId == -1) db.insertContact(contact) else db.updateContact(contact)

            if (success) {
                Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Save failed (maybe duplicate email)", Toast.LENGTH_SHORT).show()
            }
        }
        btnCancel.setOnClickListener {
            Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}