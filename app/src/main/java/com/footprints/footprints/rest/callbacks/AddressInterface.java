package com.footprints.footprints.rest.callbacks;

import com.footprints.footprints.fragments.ExploreMemories;
import com.footprints.footprints.fragments.PlaceReviewFragment;
import com.footprints.footprints.models.Addresses;
import com.footprints.footprints.models.FootprintsImage;
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

    @GET("app/retrivereview")
    Call<Review>retrivereview(@QueryMap Map<String, String> params);

    @GET("app/recommendation")
    Call<List<Addresses>> getRecommendationAddresses(@QueryMap Map<String, String> params);

    @GET("app/getplacefootprints")
    Call<FootprintsImage> getPlaceFootprints(@QueryMap Map<String, String> params);

}
