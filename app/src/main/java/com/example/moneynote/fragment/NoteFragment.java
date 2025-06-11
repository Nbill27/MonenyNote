package com.example.moneynote.fragment;

import android.app.DatePickerDialog; // Untuk menampilkan dialog pemilihan tanggal
import android.os.Bundle; // Untuk menyimpan state Fragment
import android.text.TextUtils; // Untuk memeriksa string kosong
import android.view.LayoutInflater; // Untuk menginflate layout Fragment
import android.view.View; // Kelas dasar untuk UI komponen
import android.view.ViewGroup; // Kelas dasar untuk layout
import android.widget.Button; // Tombol
import android.widget.EditText; // Kolom input teks
import android.widget.Toast; // Pesan pop-up

import androidx.fragment.app.Fragment; // Kelas dasar untuk Fragment

import com.google.firebase.auth.FirebaseAuth; // Firebase Authentication
import com.google.firebase.auth.FirebaseUser; // Objek pengguna Firebase
import com.google.firebase.database.DatabaseReference; // Referensi ke database Firebase
import com.google.firebase.database.FirebaseDatabase; // Instance Firebase Database

import com.example.moneynote.R; // Mengakses resource (strings, layouts, etc.)
import com.example.moneynote.model.Transaction; // Model data transaksi
import com.example.moneynote.util.Constants; // Konstanta untuk node Firebase

import java.text.SimpleDateFormat; // Untuk memformat tanggal
import java.util.Calendar; // Untuk bekerja dengan tanggal dan waktu
import java.util.Locale; // Untuk menentukan lokal (misalnya, bahasa dan format tanggal)

public class NoteFragment extends Fragment {

    private EditText etCategory, etAmount, etDate;
    private Button btnSaveNote;
    private Calendar calendar; // Objek Calendar untuk DatePicker

    private FirebaseAuth mAuth; // Objek Firebase Authentication
    private DatabaseReference mDatabase; // Objek Firebase Realtime Database reference

    // Konstruktor kosong wajib untuk Fragment
    public NoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Menginflate (membuat objek dari) layout XML untuk Fragment ini
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        // Inisialisasi instance Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Mengaitkan variabel dengan komponen UI dari layout
        etCategory = view.findViewById(R.id.et_category);
        etAmount = view.findViewById(R.id.et_amount);
        etDate = view.findViewById(R.id.et_date);
        btnSaveNote = view.findViewById(R.id.btn_save_note);

        calendar = Calendar.getInstance(); // Mendapatkan instance Calendar saat ini

        // Menambahkan listener untuk EditText tanggal agar DatePickerDialog muncul
        etDate.setOnClickListener(v -> showDatePickerDialog());
        // Menambahkan listener untuk tombol Save Note
        btnSaveNote.setOnClickListener(v -> saveNote());

        return view; // Mengembalikan View yang sudah di-inflate
    }

    // Metode untuk menampilkan DatePickerDialog
    private void showDatePickerDialog() {
        // Listener yang akan dipanggil saat tanggal dipilih
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInEditText(); // Memperbarui teks di EditText tanggal
        };

        // Membuat dan menampilkan DatePickerDialog
        new DatePickerDialog(getContext(), dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Metode untuk memperbarui teks di EditText tanggal dengan format yang diinginkan
    private void updateDateInEditText() {
        String myFormat = "yyyy-MM-dd"; // Format tanggal: YYYY-MM-DD
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US); // Menggunakan Locale.US untuk format konsisten
        etDate.setText(sdf.format(calendar.getTime()));
    }

    // Metode untuk menyimpan catatan keuangan ke Firebase Realtime Database
    private void saveNote() {
        String category = etCategory.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(category)) {
            etCategory.setError("Category is required.");
            return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Amount is required.");
            return;
        }
        if (TextUtils.isEmpty(date)) {
            etDate.setError("Date is required.");
            return;
        }

        double amount = 0;
        try {
            amount = Double.parseDouble(amountStr); // Konversi string amount ke double
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount format. Please enter a number.");
            return;
        }

        // Mendapatkan pengguna Firebase yang sedang login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Mendapatkan UID pengguna
            // Membuat key unik untuk transaksi baru di Firebase
            String transactionId = mDatabase.child(Constants.TRANSACTIONS_NODE).child(userId).push().getKey();

            // Membuat objek Transaction baru
            Transaction transaction = new Transaction(transactionId, category, amount, date);

            if (transactionId != null) {
                // Menyimpan objek transaksi ke Firebase Realtime Database
                // Path: transactions -> userId -> transactionId -> transaction_data
                mDatabase.child(Constants.TRANSACTIONS_NODE).child(userId).child(transactionId).setValue(transaction)
                        .addOnSuccessListener(aVoid -> {
                            // Jika berhasil disimpan
                            Toast.makeText(getContext(), "Note saved successfully!", Toast.LENGTH_SHORT).show();
                            // Mengosongkan field setelah berhasil disimpan
                            etCategory.setText("");
                            etAmount.setText("");
                            etDate.setText("");
                        })
                        .addOnFailureListener(e -> {
                            // Jika gagal disimpan
                            Toast.makeText(getContext(), "Failed to save note: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        } else {
            // Jika pengguna belum login
            Toast.makeText(getContext(), "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            // Anda mungkin ingin mengarahkan kembali ke halaman login di sini
        }
    }
}