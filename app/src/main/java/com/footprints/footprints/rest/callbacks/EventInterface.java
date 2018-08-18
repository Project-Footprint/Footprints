package com.footprints.footprints.rest.callbacks;

import com.footprints.footprints.controllers.test.gps.GpsLocationReceiver;
import com.footprints.footprints.fragments.ExploreEvents;
import com.footprints.footprints.models.Event;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface EventInterface  {

    @POST("app/addevent")
    Call<Integer> addEvent(@Body ExploreEvents.AddEvents addEvents);

    @GET("app/retriveevent")
    Call<List<Event>> retriveEvents(@QueryMap Map<String, String> params);

    @POST("app/checknewevent")
    Call<Integer> checkNewEvent(@Body GpsLocationReceiver.NewEventModel newEventModel);

}
