package com.example.moneynote.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneynote.activity.LoginActivity;
import com.example.moneynote.model.Transaction;
import com.example.moneynote.model.User;
import com.example.moneynote.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.moneynote.R;
import com.example.moneynote.activity.LoginActivity;
import com.example.moneynote.model.Transaction;
import com.example.moneynote.model.User;
import com.example.moneynote.util.Constants;

import java.text.NumberFormat;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail, tvTotalExpenseAmount;
    private Button btnLogout;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ValueEventListener userListener; // Listener untuk data user
    private ValueEventListener transactionsListener; // Listener untuk data transaksi (pengeluaran)

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inisialisasi komponen UI
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);
        tvTotalExpenseAmount = view.findViewById(R.id.tv_total_expense_amount);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Atur listener untuk tombol logout
        btnLogout.setOnClickListener(v -> logoutUser());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserProfile(); // Muat data profil user
        calculateTotalExpense(); // Hitung dan tampilkan total pengeluaran
    }

    // Metode untuk memuat data profil user dari Firebase
    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = mDatabase.child(Constants.USERS_NODE).child(userId);

            // Hapus listener sebelumnya
            if (userListener != null) {
                userRef.removeEventListener(userListener);
            }

            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            tvProfileName.setText("Name: " + user.getName());
                            tvProfileEmail.setText("Email: " + user.getEmail());
                        }
                    } else {
                        // Jika data user tidak ada di database, tampilkan email dari Auth
                        tvProfileName.setText("Name: N/A");
                        tvProfileEmail.setText("Email: " + currentUser.getEmail());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load user profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            userRef.addValueEventListener(userListener); // Tambahkan listener
        } else {
            // Jika user belum login
            tvProfileName.setText("Name: N/A");
            tvProfileEmail.setText("Email: N/A");
        }
    }

    // Metode untuk menghitung total pengeluaran user
    private void calculateTotalExpense() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference transactionsRef = mDatabase.child(Constants.TRANSACTIONS_NODE).child(userId);

            // Hapus listener sebelumnya
            if (transactionsListener != null) {
                transactionsRef.removeEventListener(transactionsListener);
            }

            transactionsListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double totalExpense = 0;
                    if (snapshot.exists()) {
                        for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                            Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                            if (transaction != null) {
                                totalExpense += transaction.getAmount(); // Tambahkan jumlah transaksi
                            }
                        }
                    }
                    // Format total pengeluaran ke mata uang Rupiah
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                    currencyFormat.setMaximumFractionDigits(0); // Tanpa desimal
                    tvTotalExpenseAmount.setText(currencyFormat.format(totalExpense));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to calculate total expense: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    tvTotalExpenseAmount.setText("Rp 0");
                }
            };
            transactionsRef.addValueEventListener(transactionsListener); // Tambahkan listener
        } else {
            tvTotalExpenseAmount.setText("Rp 0");
        }
    }

    // Metode untuk logout user
    private void logoutUser() {
        mAuth.signOut(); // Logout dari Firebase Authentication
        Toast.makeText(getContext(), "Logged out successfully.", Toast.LENGTH_SHORT).show();
        // Pindah ke LoginActivity dan kosongkan back stack
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish(); // Tutup MainActivity
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Penting: Hapus semua listener saat tampilan fragment dihancurkan untuk mencegah memory leak
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            if (userListener != null) {
                mDatabase.child(Constants.USERS_NODE).child(userId).removeEventListener(userListener);
            }
            if (transactionsListener != null) {
                mDatabase.child(Constants.TRANSACTIONS_NODE).child(userId).removeEventListener(transactionsListener);
            }
        }
    }
}