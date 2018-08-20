package com.footprints.footprints.rest.callbacks;

import com.footprints.footprints.models.AddComment;
import com.footprints.footprints.models.AddLike;
import com.footprints.footprints.models.PostCommentModel;
import com.footprints.footprints.models.SearchModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ActionInterface {
    @POST("app/addlike")
    Call<Integer> addLike(@Body AddLike addLike);

    @POST("app/unlike")
    Call<Integer> unLike(@Body AddLike addLike);

    @POST("app/postcomment")
    Call<PostCommentModel> postComment(@Body AddComment addComment);


    @GET("app/searchdb")
    Call<SearchModel> getSearchResponse(@QueryMap Map<String, String> params);
}
