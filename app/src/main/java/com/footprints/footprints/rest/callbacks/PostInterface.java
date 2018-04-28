package com.footprints.footprints.rest.callbacks;

import com.footprints.footprints.activities.UploadMemories;
import com.footprints.footprints.fragments.PlaceReviewFragment;
import com.footprints.footprints.models.Post;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostInterface {

    @POST("app/postmemories")
    Call<Integer> postMemories(@Body UploadMemories.Status status);

    @POST("app/retriveposts")
    Call<Post> retrivePosts(@Body PlaceReviewFragment.RetriveReviews retriveReviews);

}
