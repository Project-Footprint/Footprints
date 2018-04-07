package com.footprints.footprints.rest.callbacks;

import com.footprints.footprints.activities.MapsActivity;
import com.footprints.footprints.models.Addresses;

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
    Call<Integer>addPoi(@Body MapsActivity.PoiLatLog latLog);

}
