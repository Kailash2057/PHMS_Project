package com.example.phms

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ViewContactActivity : AppCompatActivity() {

    private lateinit var db: ContactDbHelper
    private lateinit var username: String
    private var contactId: Int = -1
    private lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_contact)

        db = ContactDbHelper(this)
        contactId = intent.getIntExtra("contactId", -1)
        username = intent.getStringExtra("username") ?: ""

        if (contactId == -1 || username.isEmpty()) {
            Toast.makeText(this, "Invalid contact data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val textViewName = findViewById<TextView>(R.id.contactName)
        val textViewEmail = findViewById<TextView>(R.id.contactEmail)
        val textViewPhone = findViewById<TextView>(R.id.contactPhone)
        val btnCall = findViewById<Button>(R.id.btnCall)
        val btnEmail = findViewById<Button>(R.id.btnEmail)
        val btnText = findViewById<Button>(R.id.btnText)
        val btnEdit = findViewById<Button>(R.id.btnEditContact)
        val btnDelete = findViewById<Button>(R.id.btnDeleteContact)

        contact = db.getContactById(contactId) ?: run {
            Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        textViewName.text = contact.name
        textViewEmail.text = contact.email
        textViewPhone.text = contact.phone

        btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
            startActivity(intent)
        }

        btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${contact.email}")
                putExtra(Intent.EXTRA_SUBJECT, "Hello ${contact.name}")
            }
            startActivity(intent)
        }

        btnText.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:${contact.phone}")
            }
            startActivity(intent)
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java).apply {
                putExtra("contactId", contact.id)
                putExtra("username", username)
            }
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Delete ${contact.name}?")
                setMessage("Are you sure you want to delete this contact?")
                setPositiveButton("Yes") { _, _ ->
                    if (db.deleteContact(contact.id, username)) {
                        Toast.makeText(
                            this@ViewContactActivity,
                            "Deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@ViewContactActivity,
                            "Deletion failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                setNegativeButton("Cancel", null)
                show()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        contact = db.getContactById(contactId) ?: return
        findViewById<TextView>(R.id.contactName).text = contact.name
        findViewById<TextView>(R.id.contactEmail).text = contact.email
        findViewById<TextView>(R.id.contactPhone).text = contact.phone
    }
}
