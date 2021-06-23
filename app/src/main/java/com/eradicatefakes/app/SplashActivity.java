package com.eradicatefakes.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 1500);
        }
        else {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                finish();
            }, 1500);
        }
    }
}