package com.example.moneynote.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneynote.R;
import com.example.moneynote.model.Transaction;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    private NumberFormat currencyFormat; // Objek untuk format mata uang

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
        // Inisialisasi format mata uang untuk Rupiah Indonesia
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        currencyFormat.setMaximumFractionDigits(0); // Atur agar tidak ada digit desimal untuk Rupiah
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menginflate (membuat objek View dari) layout XML untuk setiap item daftar
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view); // Mengembalikan instance ViewHolder baru
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position); // Dapatkan objek Transaction pada posisi ini
        // Mengisi data dari objek Transaction ke TextView yang sesuai di ViewHolder
        holder.tvCategory.setText("Category: " + transaction.getCategory());
        holder.tvAmount.setText("Amount: " + currencyFormat.format(transaction.getAmount()));
        holder.tvDate.setText("Date: " + transaction.getDate());
    }

    @Override
    public int getItemCount() {
        return transactionList.size(); // Mengembalikan jumlah total item dalam daftar
    }

    // Inner class ViewHolder untuk menampung View dari setiap item daftar
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount, tvDate; // Deklarasi komponen UI dalam satu item

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Menginisialisasi komponen UI dengan ID dari layout item_transaction.xml
            tvCategory = itemView.findViewById(R.id.tv_transaction_category);
            tvAmount = itemView.findViewById(R.id.tv_transaction_amount);
            tvDate = itemView.findViewById(R.id.tv_transaction_date);
        }
    }
}