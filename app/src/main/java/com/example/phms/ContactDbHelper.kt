package com.example.phms

import android.content.*
import android.database.sqlite.*

class ContactDbHelper(context: Context) :
    SQLiteOpenHelper(context, "contacts.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE contacts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                email TEXT,
                phone TEXT,
                username TEXT,
                UNIQUE(email, username)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS contacts")
        onCreate(db)
    }

    fun insertContact(c: Contact): Boolean {
        val values = ContentValues().apply {
            put("name", c.name)
            put("email", c.email)
            put("phone", c.phone)
            put("username", c.username)
        }
        return writableDatabase.insert("contacts", null, values) != -1L
    }

    fun updateContact(c: Contact): Boolean {
        val values = ContentValues().apply {
            put("name", c.name)
            put("email", c.email)
            put("phone", c.phone)
        }
        return writableDatabase.update(
            "contacts", values,
            "id=? AND username=?", arrayOf(c.id.toString(), c.username)
        ) > 0
    }

    fun deleteContact(id: Int, username: String): Boolean {
        return writableDatabase.delete(
            "contacts",
            "id=? AND username=?",
            arrayOf(id.toString(), username)
        ) > 0
    }

    fun getAllContacts(username: String): List<Contact> {
        val list = mutableListOf<Contact>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM contacts WHERE username=?", arrayOf(username))
        while (cursor.moveToNext()) {
            list.add(Contact(
                id = cursor.getInt(0),
                name = cursor.getString(1),
                email = cursor.getString(2),
                phone = cursor.getString(3),
                username = cursor.getString(4)
            ))
        }
        cursor.close()
        return list
    }
    fun getContactById(id: Int): Contact? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM contacts WHERE id=?", arrayOf(id.toString()))
        return if (cursor.moveToFirst()) {
            val contact = Contact(
                id = cursor.getInt(0),
                name = cursor.getString(1),
                email = cursor.getString(2),
                phone = cursor.getString(3),
                username = cursor.getString(4)
            )
            cursor.close()
            contact
        } else {
            cursor.close()
            null
        }
    }
}