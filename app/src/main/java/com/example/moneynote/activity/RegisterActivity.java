package com.example.moneynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.moneynote.R;
import com.example.moneynote.model.User;
import com.example.moneynote.util.Constants;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registes);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etName = findViewById(R.id.et_name_register);
        etEmail = findViewById(R.id.et_email_register);
        etPassword = findViewById(R.id.et_password_register);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        btnRegister.setOnClickListener(v -> registerUser());

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required.");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required.");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registrasi Authentication berhasil.
                        // Pengguna secara otomatis login setelah pendaftaran.
                        // Dapatkan UID untuk menyimpan data user ke Realtime Database.
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            User user = new User(name, email);

                            // Simpan informasi user ke Realtime Database
                            mDatabase.child(Constants.USERS_NODE).child(userId).setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegisterActivity.this, "Registration Successful. Please login.", Toast.LENGTH_LONG).show();

                                        // --- BAGIAN PENTING: Force Logout dan Arahkan ke LoginActivity ---
                                        mAuth.signOut(); // Logout pengguna yang baru saja terdaftar

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        // Clear back stack agar tidak bisa kembali ke RegisterActivity
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish(); // Tutup RegisterActivity

                                    })
                                    .addOnFailureListener(e -> {
                                        // Jika penyimpanan data user gagal, beri tahu user dan mungkin logout juga
                                        Toast.makeText(RegisterActivity.this, "Registration successful, but failed to save user data: " + e.getMessage() + ". Please try logging in.", Toast.LENGTH_LONG).show();
                                        mAuth.signOut(); // Pastikan logout juga jika penyimpanan data gagal
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    });
                        } else {
                            // Ini seharusnya tidak terjadi jika task.isSuccessful() true,
                            // tapi sebagai fallback
                            Toast.makeText(RegisterActivity.this, "Registration successful, but user not found. Please login.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // Registrasi Authentication gagal
                        Toast.makeText(RegisterActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}