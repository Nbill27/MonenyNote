package com.example.moneynote.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.moneynote.R;
import com.example.moneynote.adapter.TransactionAdapter;
import com.example.moneynote.model.Transaction;
import com.example.moneynote.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView rvTransactions;
    private TextView tvNoData; // TextView untuk menampilkan pesan jika tidak ada data
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ValueEventListener transactionListener; // Listener untuk mendengarkan perubahan data

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvTransactions = view.findViewById(R.id.rv_transactions);
        tvNoData = view.findViewById(R.id.tv_no_data);

        // Konfigurasi RecyclerView
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList);
        rvTransactions.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTransactions(); // Panggil metode untuk memuat transaksi
    }

    private void loadTransactions() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Referensi ke lokasi transaksi user di Firebase
            DatabaseReference userTransactionsRef = mDatabase.child(Constants.TRANSACTIONS_NODE).child(userId);

            // Hapus listener sebelumnya untuk menghindari duplikasi jika fragment dibuat ulang
            if (transactionListener != null) {
                userTransactionsRef.removeEventListener(transactionListener);
            }

            // Inisialisasi ValueEventListener baru
            transactionListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    transactionList.clear(); // Hapus data lama
                    if (snapshot.exists()) {
                        // Iterasi setiap transaksi dalam snapshot
                        for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                            Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                            if (transaction != null) {
                                // Set ID transaksi dari key Firebase (penting untuk identifikasi)
                                transaction.setId(transactionSnapshot.getKey());
                                transactionList.add(transaction);
                            }
                        }
                        tvNoData.setVisibility(View.GONE); // Sembunyikan pesan "no data"
                        rvTransactions.setVisibility(View.VISIBLE); // Tampilkan RecyclerView
                    } else {
                        tvNoData.setVisibility(View.VISIBLE); // Tampilkan pesan "no data"
                        rvTransactions.setVisibility(View.GONE); // Sembunyikan RecyclerView
                    }
                    adapter.notifyDataSetChanged(); // Beri tahu adapter bahwa data telah berubah
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load transactions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    tvNoData.setVisibility(View.VISIBLE);
                    rvTransactions.setVisibility(View.GONE);
                }
            };
            userTransactionsRef.addValueEventListener(transactionListener); // Tambahkan listener ke database
        } else {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            tvNoData.setVisibility(View.VISIBLE);
            rvTransactions.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Penting: Hapus listener saat tampilan fragment dihancurkan untuk mencegah memory leak
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && transactionListener != null) {
            mDatabase.child(Constants.TRANSACTIONS_NODE).child(currentUser.getUid()).removeEventListener(transactionListener);
        }
    }
}