package com.footprints.footprints.rest.callbacks;

import com.footprints.footprints.fragments.ExploreEvents;
import com.footprints.footprints.models.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EventInterface  {

    @POST("app/addevent")
    Call<Integer> addEvent(@Body ExploreEvents.AddEvents addEvents);

    @POST("app/retriveevent")
    Call<List<Event>> retriveEvents(@Body ExploreEvents.RetriveEvents retriveEvents);
}
