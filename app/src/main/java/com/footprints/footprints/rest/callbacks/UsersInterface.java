package com.footprints.footprints.rest.callbacks;

import com.footprints.footprints.activities.ProfileActivity;
import com.footprints.footprints.appintro.Splash;
import com.footprints.footprints.models.Notification;
import com.footprints.footprints.models.User;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;


public interface UsersInterface {

    @GET("app/users")
    Call<List<User>> getUsers();

    @POST("app/siginin")
    Call<Integer>singIn(@Body Splash.UserInfoClass userInfoClass);

    @POST("app/uploadimage")
    Call<Integer> uploadImage(@Body RequestBody file);

    @GET("app/loadowninfo")
    Call<User> getOwnInfo(@QueryMap Map<String, String> params);

    @GET("app/getnotification")
    Call<List<Notification>> getNotification(@QueryMap Map<String, String> params);


    @GET("app/loadotherinfo")
    Call<User> getOthersInfo(@QueryMap Map<String, String> params);

    @POST("app/performbtnaction")
    Call<Integer> performBtnAction(@Body ProfileActivity.PeformActoin peformActoin);
}
