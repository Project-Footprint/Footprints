package com.footprints.footprints.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.footprints.footprints.R;
import com.footprints.footprints.activities.PlaceActivity;
import com.footprints.footprints.controllers.SharedPreferenceController;
import com.footprints.footprints.models.Addresses;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.AddressInterface;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExplorePlaces extends Fragment {
    ListView listView;
    ArrayList<Addresses> arrayList = new ArrayList<>();
    ArrayList<String> eventLists = new ArrayList<>();
    Context context;
    ArrayAdapter<String> adapter;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public ExplorePlaces() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_places, container, false);
        listView = view.findViewById(R.id.recommendation_listview);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getRecommendationPlaces();
    }

    private void getRecommendationPlaces() {
        Map<String, String> params = new HashMap<String, String>();
        LatLng latLng = SharedPreferenceController.getUserLocation(context);
        params.put("lattitude", latLng.latitude+"");
        params.put("lognitude", latLng.longitude+"");

        AddressInterface addressInterface = ApiClient.getApiClient().create(AddressInterface.class);
        Call<List<Addresses>> call = addressInterface.getRecommendationAddresses(params);
        call.enqueue(new Callback<List<Addresses>>() {
            @Override
            public void onResponse(Call<List<Addresses>> call, Response<List<Addresses>> response) {
                if (response.body() != null) {

                    if (response.raw().cacheResponse() != null) {
                        Log.d("checkCache", "From Cache Place");
                    } else if (response.raw().networkResponse() != null) {
                        Log.d("checkCache", "Network call Place");
                    } else {
                        Log.d("checkCache", "Unknown Place");
                    }
                    for (int i = 0; i < response.body().size(); i++) {
                        arrayList.add(response.body().get(i));
                        eventLists.add(response.body().get(i).getName());
                    }
                    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, eventLists);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(context, PlaceActivity.class);
                            intent.putExtra("name", arrayList.get(position).getName());
                            intent.putExtra("latitude", arrayList.get(position).getLat());
                            intent.putExtra("longitude", arrayList.get(position).getLon());
                            startActivity(intent);
                        }
                    });
                }
            }


            @Override
            public void onFailure(Call<List<Addresses>> call, Throwable t) {
                Log.d("LogTracker4", "Retrofil Failure: " + t.getMessage());

            }
        });

    }


    @Override
    public void onPause() {
        super.onPause();
        if(adapter!=null){
            arrayList.clear();
            adapter.notifyDataSetChanged();
        }
    }
}
