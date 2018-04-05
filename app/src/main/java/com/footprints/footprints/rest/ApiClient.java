package com.footprints.footprints.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static  final String BASE_URL = "http://10.0.2.2/footprints/public/";
  //  public static  final String BASE_URL = "http://192.168.50.102/footprints/public/";
    public static Retrofit retrofit =null;

    public  static  Retrofit getApiClient(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                           .addConverterFactory(GsonConverterFactory.create())
                            .build();
        }
        return retrofit;
    }
}
