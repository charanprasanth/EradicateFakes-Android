package com.eradicatefakes.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataActivity extends AppCompatActivity {

    ImageView image;
    String col1, col2, col3, col4, col5, key, img, count;
    TextView data1, data2, data3, data4, data5;
    FloatingActionButton fab;
    ImageView type;
    TextView typeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        Bundle extras = getIntent().getExtras();
        key = extras.getString("key");
        col1 = extras.getString("col1");
        col2 = extras.getString("col2");
        col3 = extras.getString("col3");
        col4 = extras.getString("col4");
        col5 = extras.getString("col5");
        img = extras.getString("img");
        count = extras.getString("count");

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(DataActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        type = findViewById(R.id.type);
        typeText = findViewById(R.id.typeText);

        image = findViewById(R.id.img);

        data1 = findViewById(R.id.data1);
        data2 = findViewById(R.id.data2);
        data3 = findViewById(R.id.data3);
        data4 = findViewById(R.id.data4);
        data5 = findViewById(R.id.data5);

        if (Integer.parseInt(count) == 0){

            Log.d("lllll", "genuine");
            type.setImageResource(R.drawable.genuine);
            typeText.setText("This product is genuine.");
        }
        else if(Integer.parseInt(count) > 0){

            Log.d("lllll", "not genuine");
            type.setImageResource(R.drawable.danger);
            typeText.setText("This product has been previously scanned and could be fake.");
        }

        data1.setText(col1);
        data2.setText(col2);
        data3.setText(col3);
        data4.setText(col4);
        data5.setText(col5);

        Log.d("lllll", "http://www.eradicatefakes.com:8000" + img);

        Picasso.get()
                .load("http://www.eradicatefakes.com:8000" + img)
                .into(image);

        //fetch(key);
    }

    private void fetch(String key){
        String url = "https://pw1medius.pythonanywhere.com/apiuser/data/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url + key + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestApi restApi = retrofit.create(RestApi.class);

        Call<modelData> call = restApi.getData();

        call.enqueue(new Callback<modelData>() {
            @Override
            public void onResponse(Call<modelData> call, Response<modelData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        modelData model = response.body();

                        if (Integer.parseInt(model.getCount()) == 0){
                            type.setImageResource(R.drawable.genuine);
                            typeText.setText("This product is genuine.");
                        } else if(Integer.parseInt(model.getCount()) > 0){
                            type.setImageResource(R.drawable.danger);
                            typeText.setText("This product has been previously scanned and could be fake.");
                        }

                        data1.setText(model.getCol1());
                        data2.setText(model.getCol2());
                        data3.setText(model.getCol3());
                        data4.setText(model.getCol4());
                        data5.setText(model.getCol5());

                        String imageUrl = model.getImg();

                        Picasso.get()
                                .load("https://pw1medius.pythonanywhere.com" + imageUrl)
                                .into(image);
                    }
                }
                else {
                    //Toast.makeText(DataActivity.this, "Details not found: " + response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<modelData> call, Throwable t) {
                //Toast.makeText(DataActivity.this, "error: " + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
