package com.eradicatefakes.app;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestApi {

    @GET(" ")
    Call<modelData> getData();
}
