package com.eradicatefakes.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.skydoves.elasticviews.ElasticLayout;

import java.util.concurrent.TimeUnit;

public class AuthenticationActivity extends AppCompatActivity {

    EditText mobileNum;
    CountryCodePicker countryCodePicker;
    ElasticLayout otp;
    String Number;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mobileNum = findViewById(R.id.MobileNum);
        countryCodePicker = findViewById(R.id.CountryCode);
        otp = findViewById(R.id.sendOtp);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
//                            signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.d("ERROR_eh", e.getMessage());
                Toast.makeText(AuthenticationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(AuthenticationActivity.this, "failed", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(AuthenticationActivity.this, "Try after some time", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Intent intent = new Intent(AuthenticationActivity.this, OtpActivity.class);
                intent.putExtra("number", Number);
                intent.putExtra("backendotp", verificationId);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        };

        otp.setOnClickListener(v -> {

            if (!TextUtils.isEmpty(mobileNum.getText().toString())) {
                if (mobileNum.getText().toString().length() >= 7 && mobileNum.getText().toString().length() <= 16) {
                    String number = mobileNum.getText().toString();
                    String codePick = countryCodePicker.getFullNumberWithPlus();
                    String num = codePick + number;

                    if (num.startsWith("+")) {
                        Number = num;
                    } else {
                        Number = "+" + num;
                    }

                    PhoneAuthProvider.verifyPhoneNumber(
                            PhoneAuthOptions
                                    .newBuilder(FirebaseAuth.getInstance())
                                    .setActivity(AuthenticationActivity.this)
                                    .setPhoneNumber(Number)
                                    .setTimeout(60L, TimeUnit.SECONDS)
                                    .setCallbacks(mCallbacks)
                                    .build());
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter valid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Enter valid number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}