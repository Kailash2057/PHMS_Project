package com.example.phms

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter (private val context: Context,
                      private var contactList: MutableList<Contact>,
                      private val username: String,
                      private val dbHelper: ContactDbHelper
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    fun updateData(newContacts: List<Contact>) {
        contactList.clear()
        contactList.addAll(newContacts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.contactName)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)

        fun bind(contact: Contact) {
            nameText.text = contact.name

            editButton.setOnClickListener {
                val intent = Intent(context, AddContactActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("contactId", contact.id)
                context.startActivity(intent)
            }

            deleteButton.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Delete Contact")
                    .setMessage("Are you sure you want to delete ${contact.name}?")
                    .setPositiveButton("Yes") { _, _ ->
                        dbHelper.deleteContact(contact.id, contact.username)
                        contactList.removeAt(absoluteAdapterPosition)
                        notifyItemRemoved(bindingAdapterPosition)
                        Toast.makeText(context, "${contact.name} deleted", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            itemView.setOnClickListener {
                val intent = Intent(context, ViewContactActivity::class.java)
                intent.putExtra("contactId", contact.id)
                intent.putExtra("username", username)
                intent.putExtra("contactName", contact.name)
                intent.putExtra("contactPhone", contact.phone)
                intent.putExtra("contactEmail", contact.email)
                intent.putExtra("contactRelation", contact.relation)
                context.startActivity(intent)
            }
        }
    }
}