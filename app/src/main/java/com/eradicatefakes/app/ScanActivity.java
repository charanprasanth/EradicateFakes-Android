package com.eradicatefakes.app;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.dm7.barcodescanner.zxing.ZXingScannerView.*;

public class ScanActivity extends AppCompatActivity implements ResultHandler {

    ZXingScannerView scannerView;
    LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scannerView = findViewById(R.id.Scanner);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> onBackPressed());

        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                scannerView.setResultHandler(ScanActivity.this);
                scannerView.startCamera();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(ScanActivity.this, "Accept the permission", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

            }
        }).check();
    }

    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        scannerView.stopCamera();
        super.onPause();
    }


    @Override
    public void handleResult(Result rawResult) {

        String RawResult = rawResult.getText().toUpperCase();
        showDialog(RawResult.toLowerCase());
        scannerView.stopCamera();
    }

    void showDialog(String key) {

        Log.d("lllll", "dialog shown");

        Dialog dialog = new Dialog(ScanActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.bg));
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        ImageView close = dialog.findViewById(R.id.close);
        LottieAnimationView loadingAnimation, invalidAnimation, successAnimation, matchNotFoundAnimation;
        TextView text = dialog.findViewById(R.id.text);

        loadingAnimation = dialog.findViewById(R.id.loadingAnimation);
        invalidAnimation = dialog.findViewById(R.id.invalidAnimation);
        successAnimation = dialog.findViewById(R.id.successAnimation);
        matchNotFoundAnimation = dialog.findViewById(R.id.matchNotFoundAnimation);

        if (key.length() == 64) {

            Log.d("lllll", "length64");
            String url = "http://www.eradicatefakes.com:8000/apiuser/data/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url + key + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RestApi restApi = retrofit.create(RestApi.class);

            Call<modelData> call = restApi.getData();

            call.enqueue(new Callback<modelData>() {
                @Override
                public void onResponse(Call<modelData> call, Response<modelData> response) {

                    Log.d("lllll", "on response");

                    if (response.isSuccessful()) {

                        Log.d("lllll", "response success");

                        if (response.body() != null) {

                            Log.d("lllll", "response not null");

                            modelData model = response.body();

                            String col1, col2, col3, col4, col5, img, count;

                            col1 = model.getCol1();
                            col2 = model.getCol2();
                            col3 = model.getCol3();
                            col4 = model.getCol4();
                            col5 = model.getCol5();
                            img = model.getImg();
                            count = model.getCount();

                            loadingAnimation.setVisibility(View.GONE);
                            successAnimation.setVisibility(View.VISIBLE);

                            text.setVisibility(View.INVISIBLE);

                            new Handler().postDelayed(() -> {
                                Intent i = new Intent(ScanActivity.this, DataActivity.class);
                                i.putExtra("key", key);
                                i.putExtra("col1", col1);
                                i.putExtra("col2", col2);
                                i.putExtra("col3", col3);
                                i.putExtra("col4", col4);
                                i.putExtra("col5", col5);
                                i.putExtra("img", img);
                                i.putExtra("count", count);
                                startActivity(i);

                            }, 1000);
                        } else {

                            Log.d("lllll", "response null");

                            loadingAnimation.setVisibility(View.GONE);
                            matchNotFoundAnimation.setVisibility(View.VISIBLE);

                            text.setText("Match not found, the product may be fake, Click to go back.");
                            text.setTextColor(getResources().getColor(R.color.red));

                            text.setOnClickListener(v -> {
                                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        }
                    }
                    else {

                        Log.d("lllll", response + "");

                        loadingAnimation.setVisibility(View.GONE);
                        matchNotFoundAnimation.setVisibility(View.VISIBLE);

                        text.setText("Match not found, the product may be fake, Click to go back.");
                        text.setTextColor(getResources().getColor(R.color.red));

                        text.setOnClickListener(v -> {
                            Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }
                }

                @Override
                public void onFailure(Call<modelData> call, Throwable t) {
                    Log.d("lllll", t.getMessage() + "");
                }
            });
        }
        else {
            Log.d("lllll", "length not 64");

            loadingAnimation.setVisibility(View.GONE);
            invalidAnimation.setVisibility(View.VISIBLE);

            text.setText("Invalid QR code, Click to go back.");
            text.setTextColor(getResources().getColor(R.color.red));

            text.setOnClickListener(v -> {
                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }

        close.setOnClickListener(v -> {
            Intent intent = new Intent(ScanActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }
}
