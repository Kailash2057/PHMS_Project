package com.example.phms

import android.content.*
import android.database.sqlite.*

class MedicationDbHelper(context: Context) :
    SQLiteOpenHelper(context, "medications.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE medications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                dosage TEXT,
                frequency TEXT,
                startDate TEXT,
                endDate TEXT,
                intakeTime TEXT,
                reminderSet INTEGER,
                username TEXT,
                UNIQUE(name, username)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS medications")
        onCreate(db)
    }

    fun insertMedication(m: Medication): Boolean {
        val values = ContentValues().apply {
            put("name", m.name)
            put("dosage", m.dosage)
            put("frequency", m.frequency)
            put("startDate", m.startDate)
            put("endDate", m.endDate)
            put("intakeTime", m.intakeTime)
            put("reminderSet", if (m.reminderSet) 1 else 0)
            put("username", m.username)
        }
        return writableDatabase.insert("medications", null, values) != -1L
    }

    fun updateMedication(m: Medication): Boolean {
        val values = ContentValues().apply {
            put("dosage", m.dosage)
            put("frequency", m.frequency)
            put("startDate", m.startDate)
            put("endDate", m.endDate)
            put("intakeTime", m.intakeTime)
            put("reminderSet", if (m.reminderSet) 1 else 0)
        }
        return writableDatabase.update("medications", values, "name=? AND username=?", arrayOf(m.name, m.username)) > 0
    }

    fun deleteMedication(name: String, username: String): Boolean {
        return writableDatabase.delete("medications", "name=? AND username=?", arrayOf(name, username)) > 0
    }

    fun getAllMedications(username: String): List<Medication> {
        val list = mutableListOf<Medication>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM medications WHERE username=?", arrayOf(username))
        while (cursor.moveToNext()) {
            list.add(Medication(
                id = cursor.getInt(0),
                name = cursor.getString(1),
                dosage = cursor.getString(2),
                frequency = cursor.getString(3),
                startDate = cursor.getString(4),
                endDate = cursor.getString(5),
                intakeTime = cursor.getString(6),
                reminderSet = cursor.getInt(7) == 1,
                username = cursor.getString(8)
            ))
        }
        cursor.close()
        return list
    }

    fun getMedicationById(id: Int): Medication {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM medications WHERE id = ?", arrayOf(id.toString()))
        cursor.moveToFirst()
        val med = Medication(
            id = cursor.getInt(0),
            name = cursor.getString(1),
            dosage = cursor.getString(2),
            frequency = cursor.getString(3),
            startDate = cursor.getString(4),
            endDate = cursor.getString(5),
            intakeTime = cursor.getString(6),
            reminderSet = cursor.getInt(7) == 1,
            username = cursor.getString(8)
        )
        cursor.close()
        return med
    }
}