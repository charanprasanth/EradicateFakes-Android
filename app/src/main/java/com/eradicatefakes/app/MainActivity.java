package com.eradicatefakes.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.skydoves.elasticviews.ElasticLayout;

public class MainActivity extends AppCompatActivity {

    ElasticLayout logout, scanQRBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanQRBtn = findViewById(R.id.scanQRBtn);
        scanQRBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
            startActivity(intent);
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> showDialog());
    }

    void showDialog() {

        Dialog dialog = new Dialog(MainActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.logout_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.bg));
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        ElasticLayout cancel = dialog.findViewById(R.id.cancelBtn);
        ElasticLayout logoutBtn = dialog.findViewById(R.id.logoutBtn);

        cancel.setOnClickListener(v -> dialog.dismiss());

        logoutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            dialog.dismiss();
            Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}