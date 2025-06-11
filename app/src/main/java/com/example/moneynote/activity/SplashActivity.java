package com.example.moneynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.example.moneynote.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000; // 3 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                // User sudah login, langsung ke MainActivity
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
            } else {
                // User belum login, ke LoginActivity
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
            }
            finish();
        }, SPLASH_TIME_OUT);
    }
}