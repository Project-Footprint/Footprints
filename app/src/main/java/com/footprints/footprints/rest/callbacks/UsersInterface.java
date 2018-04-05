package com.footprints.footprints.rest.callbacks;

import com.footprints.footprints.appintro.Splash;
import com.footprints.footprints.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface UsersInterface {

    @GET("app/users")
    Call<List<User>> getUsers();

    @POST("app/siginin")
    Call<Integer>singIn(@Body Splash.UserInfoClass userInfoClass);

}
