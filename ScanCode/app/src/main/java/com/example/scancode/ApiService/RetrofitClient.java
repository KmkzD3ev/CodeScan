package com.example.scancode.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//biblioteca para requisiçoes externas
public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://emissorweb.com.br/POSSIAC/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}


