package com.example.moneynote.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties // Anotasi ini bisa membantu jika ada properti yang tidak ingin disimpan ke Firebase
public class Transaction {
    public String id; // ID unik untuk transaksi, seringkali adalah key dari Firebase
    public String category;
    public double amount;
    public String date; // Tanggal transaksi dalam format String (misal: "2023-06-11")

    public Transaction() {
        // Konstruktor kosong wajib untuk Firebase Realtime Database.
        // Firebase menggunakan ini saat mengambil data dari database dan mengkonversinya menjadi objek.
    }

    public Transaction(String id, String category, double amount, String date) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    // Getter dan Setter untuk setiap properti.
    // Ini juga penting agar Firebase dapat mengakses dan memanipulasi nilai properti.
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}