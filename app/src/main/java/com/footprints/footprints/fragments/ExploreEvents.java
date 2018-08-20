package com.footprints.footprints.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.adapter.EventAdpater;
import com.footprints.footprints.controllers.ControllerPixels;
import com.footprints.footprints.controllers.SharedPreferenceController;
import com.footprints.footprints.models.Event;
import com.footprints.footprints.models.MyItem;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.EventInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExploreEvents extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context context;
    ListView listView;
    private ClusterManager<MyItem> mClusterManager;

    ArrayList<Event> eventsLists = new ArrayList<>();
    ArrayList<LatLng> latlog = new ArrayList<>();
    EventAdpater eventAdpater;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public ExploreEvents() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore_events, container, false);

        LinearLayout llBottomSheet = (LinearLayout) view.findViewById(R.id.bottom_sheet);

        listView = view.findViewById(R.id.bottom_sheet_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latlog.get(position)));

            }
        });
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setPeekHeight((int) ControllerPixels.convertDpToPixel(50.0f, getContext()));
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        TextView textView = view.findViewById(R.id.bottom_sheet_title);
        textView.setText("Nearby Events");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.eventMap);
        mapFragment.getMapAsync(ExploreEvents.this);
        retriveEvents();
    }

    private void retriveEvents() {

        EventInterface postInterface = ApiClient.getApiClient().create(EventInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        LatLng latLng = SharedPreferenceController.getUserLocation(context);
        params.put("lattitude", latLng.latitude + "");
        params.put("longnitude", latLng.longitude + "");
        Call<List<Event>> call = postInterface.retriveEvents(params);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, Response<List<Event>> response) {
                if (response.body() != null) {


                    if (response.raw().cacheResponse() != null) {
                        Log.d("checkCache", "From Cache");
                    } else if (response.raw().networkResponse() != null) {
                        Log.d("checkCache", "Network call");
                    } else {
                        Log.d("checkCache", "Unknown");
                    }
                    for (int i = 0; i < response.body().size(); i++) {
                        double lat = Double.parseDouble(response.body().get(i).getLat());
                        double log = Double.parseDouble(response.body().get(i).getLog());
                       /* LatLng latLng = new LatLng(lat, log);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(response.body().get(i).getEname());
                        markerOptions.position(latLng);
                        mMap.addMarker(markerOptions);*/
                        eventsLists.add(response.body().get(i));
                        latlog.add(new LatLng(lat, log));

                        MyItem offsetItem = new MyItem(lat, log);
                        mClusterManager.addItem(offsetItem);
                        mClusterManager.cluster();

                    }
                    for (int i = 0; i < eventsLists.size(); i++) {
                        Log.d("checkuui", eventsLists.get(i).getEname());
                        Log.d("checkuui", eventsLists.get(i).getEnddate());
                        Log.d("checkuui", eventsLists.get(i).getLat());
                    }
                    eventAdpater = new EventAdpater(eventsLists, context, R.layout.item_custom_event);
                    listView.setAdapter(eventAdpater);
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(context, "Events Retrival Failed... Please Retry !", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mClusterManager = new ClusterManager<MyItem>(getContext(), mMap);

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {

                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (ClusterItem item : cluster.getItems()) {
                    builder.include(item.getPosition());
                }
                final LatLngBounds bounds = builder.build();


                Location locationOne = new Location("");
                locationOne.setLatitude(cluster.getPosition().latitude);
                locationOne.setLongitude(cluster.getPosition().longitude);

                Location locationCenter = new Location("");
                locationCenter.setLatitude(bounds.getCenter().latitude);
                locationCenter.setLongitude(bounds.getCenter().longitude);


                float distanceInMetersOne = locationOne.distanceTo(locationCenter);
                if (distanceInMetersOne < 5) {
                    Toast.makeText(context, "Same", Toast.LENGTH_SHORT).show();

                }

                // Animate camera to the bounds
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMinZoomPreference(11f);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {

            mMap.setMyLocationEnabled(true);
        }

        LatLng latLng = SharedPreferenceController.getUserLocation(context);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                EventInterface postInterface = ApiClient.getApiClient().create(EventInterface.class);
                Call<Integer> call = postInterface.addEvent(new AddEvents(pointOfInterest.latLng.latitude + "", pointOfInterest.latLng.longitude + "", pointOfInterest.name + " Events", pointOfInterest.name));
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(@NonNull Call<Integer> call, Response<Integer> response) {
                        if (response.body() == 1) {
                            Toast.makeText(getContext(), "Event Added !", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Event Upload Failed... Please Retry !", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Toast.makeText(getContext(), "Event Upload Failed... Please Retry !", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }


    public class AddEvents {
        String lattitude;
        String longnitude;
        String name;
        String place;

        public AddEvents(String lattitude, String longitude, String name, String place) {
            this.lattitude = lattitude;
            this.longnitude = longitude;
            this.name = name;
            this.place = place;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (eventAdpater != null) {
            eventsLists.clear();
            latlog.clear();
            eventAdpater.notifyDataSetChanged();
        }

    }
}
