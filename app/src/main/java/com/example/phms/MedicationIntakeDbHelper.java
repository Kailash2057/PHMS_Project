package com.example.phms;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

public class MedicationIntakeDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medication_intake.db";
    private static final int DATABASE_VERSION = 1;

    public MedicationIntakeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE MedicationIntake (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "medication_id INTEGER," +
                "username TEXT," +
                "intake_time TEXT" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MedicationIntake");
        onCreate(db);
    }

    public void insertMedicationIntake(int medicationId, String username, String intakeTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("medication_id", medicationId);
        values.put("username", username);
        values.put("intake_time", intakeTime);

        db.insert("MedicationIntake", null, values);
        db.close();
    }

    public boolean hasTakenMedicationToday(int medicationId, String username, String todayDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM MedicationIntake WHERE medicationId = ? AND username = ? AND dateTime LIKE ?",
                new String[]{String.valueOf(medicationId), username, todayDate + "%"}
        );

        boolean hasTaken = false;
        if (cursor.moveToFirst()) {
            hasTaken = cursor.getInt(0) > 0;
        }
        cursor.close();
        return hasTaken;
    }


}

