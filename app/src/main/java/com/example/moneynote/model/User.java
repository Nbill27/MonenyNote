package com.example.moneynote.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String name;
    public String email;

    public User() {
        // Konstruktor kosong wajib untuk Firebase Realtime Database
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getter dan Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}