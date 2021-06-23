package com.eradicatefakes.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.skydoves.elasticviews.ElasticLayout;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    TextView number, otpTime, resendOtp;
    ElasticLayout verifyBtn;
    PinView pinView;
    String num, backendotp;
    LinearLayout back;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        Bundle bundle = getIntent().getExtras();
        num = bundle.getString("number");
        backendotp = bundle.getString("backendotp");

        pinView = findViewById(R.id.pinView);
        verifyBtn = findViewById(R.id.verifyBtn);
        number = findViewById(R.id.number);
        back = findViewById(R.id.back);
        //otpTime = findViewById(R.id.otpTime);
        resendOtp = findViewById(R.id.resendOTP);

        number.setText(num);

        back.setOnClickListener(v -> onBackPressed());

        verifyBtn.setOnClickListener(v -> {
            String otp = pinView.getText().toString();
            if (otp.length() == 6) {

                if (backendotp != null) {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(backendotp, otp);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        boolean newUser = task.getResult().getAdditionalUserInfo().isNewUser();
                                        final Intent intent;
                                        if (newUser) {
                                            intent = new Intent(OtpActivity.this, MainActivity.class);
                                        } else {
                                            intent = new Intent(OtpActivity.this, MainActivity.class);
                                        }
                                        startActivity(intent);
                                        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                    } else {
                                        Toast.makeText(OtpActivity.this, "Task failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            } else {
                Toast.makeText(OtpActivity.this, "Please enter a valid otp", Toast.LENGTH_SHORT).show();
            }
        });

        resendOtp.setOnClickListener(view -> {

            PhoneAuthProvider.verifyPhoneNumber(
                    PhoneAuthOptions
                            .newBuilder(FirebaseAuth.getInstance())
                            .setActivity(this)
                            .setPhoneNumber(num)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setCallbacks(mCallbacks)
                            .build());

            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
//                            signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(OtpActivity.this, "Please try after some time", Toast.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        Toast.makeText(OtpActivity.this, "Try after some time", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    backendotp = verificationId;
                    Toast.makeText(OtpActivity.this, "Code sent successfully", Toast.LENGTH_SHORT).show();
                }
            };
        });
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}