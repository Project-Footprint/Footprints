package com.footprints.footprints.rest.callbacks;

import com.footprints.footprints.fragments.bottomsheets.BottomSheetComment;
import com.footprints.footprints.models.AddLike;
import com.footprints.footprints.models.PostCommentModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ActionInterface {
    @POST("app/addlike")
    Call<Integer> addLike(@Body AddLike addLike);

    @POST("app/unlike")
    Call<Integer> unLike(@Body AddLike addLike);

    @POST("app/postcomment")
    Call<PostCommentModel> postComment(@Body BottomSheetComment.AddComment addComment);


}
