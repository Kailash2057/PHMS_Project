package com.example.phms;

public class User {
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String dob;
    private String phoneNumber;
    private String email;
    private String address;
    private String username;
    private String password;

    // Constructor
    public User(String firstName, String middleName, String lastName, String gender,
                String dob, String phoneNumber, String email, String address,
                String username, String password) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.username = username;
        this.password = password;
    }

    // Getter and Setter methods
    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getPhone() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // If needed, you can also add setter methods here to update values later
}