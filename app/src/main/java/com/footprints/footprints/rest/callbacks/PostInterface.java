package com.footprints.footprints.rest.callbacks;

import com.footprints.footprints.activities.UploadMemories;
import com.footprints.footprints.models.Post;
import com.footprints.footprints.models.UploadObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface PostInterface {

    @POST("app/postmemories")
    Call<Integer> postMemories(@Body UploadMemories.Status status);

    @GET("app/retriveposts")
    Call<Post> retrivePosts(@QueryMap Map<String, String> params);

    @POST("app/uploadmultipleimages")
    Call<UploadObject> uploadMultiFile(@Body RequestBody file);

    @GET("app/retriveuserposts")
    Call<Post> retriveUserPosts(@QueryMap Map<String, String> params);
}
