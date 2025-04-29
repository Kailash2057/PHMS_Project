package com.example.phms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PersonalHealth.db";
    private static final int DATABASE_VERSION = 1; // Increased version for new fields

    private static final String TABLE_USER = "users";
    private static final String TABLE_NOTES = "notes";

    // User table columns
    private static final String COL_ID = "id";
    private static final String COL_FIRSTNAME = "first_name";
    private static final String COL_MIDDLENAME = "middle_name";
    private static final String COL_LASTNAME = "last_name";
    private static final String COL_GENDER = "gender";
    private static final String COL_DOB = "dob";
    private static final String COL_PHONE = "phone";
    private static final String COL_EMAIL = "email";
    private static final String COL_ADDRESS = "address";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_SECURITY_QUESTION = "security_question";
    private static final String COL_SECURITY_ANSWER = "security_answer";

    // Notes table columns
    private static final String COL_NOTE_ID = "id";
    private static final String COL_NOTE_USERNAME = "username";
    private static final String COL_NOTE_TITLE = "title";
    private static final String COL_NOTE_DESCRIPTION = "description";
    private static final String COL_NOTE_TYPE = "type";
    private static final String COL_NOTE_CREATED_AT = "created_at";
    private static final String COL_NOTE_UPDATED_AT = "updated_at";

    // Your Doctor table columns
    private static final String TABLE_DOCTORS = "doctors";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_FIRSTNAME + " TEXT, "
                + COL_MIDDLENAME + " TEXT, "
                + COL_LASTNAME + " TEXT, "
                + COL_GENDER + " TEXT, "
                + COL_DOB + " TEXT, "
                + COL_PHONE + " TEXT, "
                + COL_EMAIL + " TEXT UNIQUE, "
                + COL_ADDRESS + " TEXT, "
                + COL_USERNAME + " TEXT UNIQUE, "
                + COL_PASSWORD + " TEXT, "
                + COL_SECURITY_QUESTION + " TEXT, "
                + COL_SECURITY_ANSWER + " TEXT, "
                + "is_password_changed INTEGER DEFAULT 0"
                + ")";

        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + COL_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NOTE_USERNAME + " TEXT, "
                + COL_NOTE_TITLE + " TEXT, "
                + COL_NOTE_DESCRIPTION + " TEXT, "
                + COL_NOTE_TYPE + " TEXT, "
                + COL_NOTE_CREATED_AT + " TEXT, "
                + COL_NOTE_UPDATED_AT + " TEXT"
                + ")";

        String CREATE_DOCTORS_TABLE = "CREATE TABLE " + TABLE_DOCTORS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT,"
                + "email TEXT,"
                + "phone TEXT"
                + ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_NOTES_TABLE);
        db.execSQL(CREATE_DOCTORS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // ---------------- User-related functions ---------------- //

    public boolean insertUser(String firstName, String middleName, String lastName, String gender, String dob,
                              String phone, String email, String address, String username, String password,
                              String securityQuestion, String securityAnswer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FIRSTNAME, firstName);
        values.put(COL_MIDDLENAME, middleName);
        values.put(COL_LASTNAME, lastName);
        values.put(COL_GENDER, gender);
        values.put(COL_DOB, dob);
        values.put(COL_PHONE, phone);
        values.put(COL_EMAIL, email);
        values.put(COL_ADDRESS, address);
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put(COL_SECURITY_QUESTION, securityQuestion);
        values.put(COL_SECURITY_ANSWER, securityAnswer);

        long result = db.insert(TABLE_USER, null, values);
        return result != -1;
    }

    public boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " +
                COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check if the user's password is changed
    public boolean isPasswordChanged(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT is_password_changed FROM " + TABLE_USER + " WHERE " + COL_USERNAME + " = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(0);
            cursor.close();
            return status == 1;
        }
        cursor.close();
        return false;
    }

    // Set is_password_changed to 1 after changing the password
    public void setPasswordChanged(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_password_changed", 1);
        db.update(TABLE_USER, values, COL_USERNAME + " = ?", new String[]{username});
    }

    // Fetch user details by username
    public Cursor getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COL_USERNAME + " = ?", new String[]{username});
        return cursor; // Don't forget to close the cursor when you're done with it
    }

    // Fetch the email address based on the username
    public String getUserEmail(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_EMAIL + " FROM " + TABLE_USER +
                " WHERE " + COL_USERNAME + " = ?", new String[]{username});

        String email = null;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COL_EMAIL);
            if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                email = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return email;
    }


    // For Forgot Password - Validate username and security answer
    public boolean validateUsernameAndSecurityAnswer(String username, String securityAnswer) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER +
                " WHERE " + COL_USERNAME + " = ? AND " + COL_SECURITY_ANSWER + " = ?", new String[]{username, securityAnswer});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // New method to validate email, security question, and answer
    public boolean validateEmailSecurityQuestionAndAnswer(String email, String securityQuestion, String securityAnswer) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND security_question = ? AND security_answer = ?", new String[]{email, securityQuestion, securityAnswer});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // Update password by username
    public boolean updatePasswordByUsername(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD, newPassword);
        int update = db.update(TABLE_USER, values, COL_USERNAME + " = ?", new String[]{username});
        return update > 0;
    }

    // Fetch the security question based on the username
    public String getSecurityQuestion(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_SECURITY_QUESTION + " FROM " + TABLE_USER +
                " WHERE " + COL_USERNAME + " = ?", new String[]{username});

        String question = null;

        if (cursor != null && cursor.moveToFirst()) {
            // Check if the column exists and is not null
            int columnIndex = cursor.getColumnIndex(COL_SECURITY_QUESTION);
            if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                question = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return question;
    }

    // Check if the username exists
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " +
                COL_USERNAME + " = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " +
                COL_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getUsernameByEmailAndSecurityAnswer(String email, String securityAnswer) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USERNAME + " FROM " + TABLE_USER +
                " WHERE " + COL_EMAIL + " = ? AND " + COL_SECURITY_ANSWER + " = ?", new String[]{email, securityAnswer});
        if (cursor.moveToFirst()) {
            String username = cursor.getString(0);
            cursor.close();
            return username;
        }
        cursor.close();
        return null;
    }

    public boolean validateEmailAndSecurityAnswer(String email, String securityAnswer) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER +
                " WHERE " + COL_EMAIL + " = ? AND " + COL_SECURITY_ANSWER + " = ?", new String[]{email, securityAnswer});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD, newPassword);
        int update = db.update(TABLE_USER, values, COL_EMAIL + " = ?", new String[]{email});
        return update > 0;
    }

    // ---------------- Notes-related functions ---------------- //

    public boolean createNewNote(String username, String title, String description, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTE_USERNAME, username);
        values.put(COL_NOTE_TITLE, title);
        values.put(COL_NOTE_DESCRIPTION, description);
        values.put(COL_NOTE_TYPE, type);

        // Set timestamps
        String currentTime = String.valueOf(System.currentTimeMillis());
        values.put(COL_NOTE_CREATED_AT, currentTime);
        values.put(COL_NOTE_UPDATED_AT, currentTime);

        long result = db.insert(TABLE_NOTES, null, values);
        return result != -1;
    }

    public List<Note> getAllNotes(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Note> notes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES +
                        " WHERE " + COL_NOTE_USERNAME + " = ?",
                new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE_CREATED_AT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE_UPDATED_AT))
                );
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public boolean deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NOTES, COL_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        return result > 0;
    }

    public boolean updateNote(int noteId, String newTitle, String newDescription, String newType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTE_TITLE, newTitle);
        values.put(COL_NOTE_DESCRIPTION, newDescription);
        values.put(COL_NOTE_TYPE, newType);
        values.put(COL_NOTE_UPDATED_AT, String.valueOf(System.currentTimeMillis()));

        int rowsAffected = db.update(TABLE_NOTES, values, COL_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});

        if (rowsAffected == 0) {
            Log.e("DatabaseHelper", "Update failed: No note found with ID = " + noteId);
        } else {
            Log.d("DatabaseHelper", "Note updated successfully: ID = " + noteId);
        }
        return rowsAffected > 0;
    }

    public void addDoctor(Doctor doctor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", doctor.getName());
        values.put("email", doctor.getEmail());
        values.put("phone", doctor.getPhone());
        db.insert(TABLE_DOCTORS, null, values);
        db.close();
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctorList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DOCTORS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Doctor doctor = new Doctor();
                doctor.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                doctor.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                doctor.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                doctor.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                doctorList.add(doctor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return doctorList;
    }

    public boolean updateDoctor(Doctor doctor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", doctor.getName());
        values.put("email", doctor.getEmail());
        values.put("phone", doctor.getPhone());

        int rows = db.update("doctors", values, "id=?", new String[]{String.valueOf(doctor.getId())});
        return rows > 0;
    }
    public List<Doctor> getValidDoctors() {
        List<Doctor> allDoctors = getAllDoctors();
        List<Doctor> validDoctors = new ArrayList<>();
        for (Doctor doctor : allDoctors) {
            if (!doctor.getEmail().equals("N/A")) {
                validDoctors.add(doctor);
            }
        }
        return validDoctors;
    }
}
