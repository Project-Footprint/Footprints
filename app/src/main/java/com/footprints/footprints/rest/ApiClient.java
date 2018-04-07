package com.footprints.footprints.rest;


import android.app.Activity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient extends Activity {
    //private static final String BASE_URL = "http://10.0.2.2/footprints/public/";
     private static  final String BASE_URL = "http://192.168.50.102/footprints/public/";
    private static Retrofit retrofit = null;

   /* private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();*/


    private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();




    public static Retrofit getApiClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;

    }
}
