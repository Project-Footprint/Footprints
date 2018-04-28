package com.footprints.footprints.rest.callbacks;

import com.footprints.footprints.fragments.ExploreMemories;
import com.footprints.footprints.fragments.PlaceReviewFragment;
import com.footprints.footprints.models.Addresses;
import com.footprints.footprints.models.Review;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface AddressInterface {

    @GET("app/addresses")
    Call<List<Addresses>> getAddresses(@QueryMap Map<String, String> params);


    @POST("app/addpoi")
    Call<Integer>addPoi(@Body ExploreMemories.AddPoi latLog);

    @POST("app/addreview")
    Call<Integer>addReview(@Body PlaceReviewFragment.AddReviews addReviews);

    @POST("app/retrivereview")
    Call<Review>retrivereview(@Body PlaceReviewFragment.RetriveReviews retriveReviews);
}
