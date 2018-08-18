package com.footprints.footprints.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class SharedPreferenceController {

    static SharedPreferences prefs;


    public static void saveUserInfo(Context context, String uid, String name, String email, String profileUrl, String userToken) {
        prefs = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("uid", uid);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("profileUrl", profileUrl);
        editor.putString("userToken", userToken);
        editor.apply(); // This line is IMPORTANT !!!

    }

    public static void saveUserLocation(Context context, double lat, double log) {
        prefs = context.getSharedPreferences("userLocation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        float lat1 = (float) lat;
        float log1 = (float) log;
        editor.putFloat("lat", lat1);
        editor.putFloat("log", log1);
        editor.apply();

    }

    public static void saveInOut(Context context,boolean value) {
        prefs = context.getSharedPreferences("userLocation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isOut", value);
        editor.apply();

    }

    public static boolean getInOut(Context context) {
        prefs = context.getSharedPreferences("userLocation", Context.MODE_PRIVATE);
        return  prefs.getBoolean("isOut", false);

    }

    public static LatLng getUserLocation(Context context) {
        prefs = context.getSharedPreferences("userLocation", Context.MODE_PRIVATE);
        LatLng latLng = new LatLng(prefs.getFloat("lat", 0.0f), prefs.getFloat("log", 0.0f));
        return latLng;
    }

    public void getUserInfo(Context context) {
        prefs = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
        Log.d("MyApp451", "uid : " + prefs.getString("uid", null) + "\nEmail : " + prefs.getString("fb_email", null));
    }

    public static String getUserId(Context context) {
        prefs = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
        return prefs.getString("uid", null);
    }

    public static String getToken(Context context) {
        prefs = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
        return prefs.getString("fb_access_token", null);
    }

    public static void clearFbData(Context context) {
        prefs = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // This line is IMPORTANT !!!
    }

}

