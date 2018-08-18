package com.footprints.footprints.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.activities.PlaceActivity;
import com.footprints.footprints.controllers.MultiDrawable;
import com.footprints.footprints.controllers.NetworkDetectController;
import com.footprints.footprints.controllers.SharedPreferenceController;
import com.footprints.footprints.models.Addresses;
import com.footprints.footprints.models.Person;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.AddressInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Cache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class ExploreMemories extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ClusterManager.OnClusterClickListener<Person>, ClusterManager.OnClusterItemInfoWindowClickListener<Person>, ClusterManager.OnClusterItemClickListener<Person>, ClusterManager.OnClusterInfoWindowClickListener<Person> {

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION = 1;
    public static String lattitude, longitude;


    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LocationCallback locationCallback;
    private ClusterManager<Person> mClusterManager;
    FragmentActivity context;
    public static Double lattitudeDouble, longitudeDouble;

    static int cacheSize = 10 * 1024 * 1024; // 10 MB

    public static Cache cache;

    public ExploreMemories() {
        // Required empty public constructor
    }

    public static boolean isNetwork = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (FragmentActivity) context;
        isNetwork = NetworkDetectController.checkConnection(context);
        cache = new Cache(context.getCacheDir(), cacheSize);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        View view = inflater.inflate(R.layout.fragment_explore_memories, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferenceController.saveInOut(context,false);
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(ExploreMemories.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mClusterManager = new ClusterManager<Person>(context, mMap);
        mClusterManager.setRenderer(new PersonRenderer());

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        //mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        //     mClusterManager.setOnClusterItemInfoWindowClickListener(this);


        Log.d("LogTracker4", "onMapReady");

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMinZoomPreference(11f);

        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 12:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d("LogTracker4", "onConnected Switch Sucesfull");
                        getLastLocation();
                        Toast.makeText(context, " Location permission Granted", Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to ...
                        Toast.makeText(context, "We need Location permission", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void getLastLocation() {
        Log.d("LogTracker4", "getLastLocation");
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            final FusedLocationProviderClient locationClient = getFusedLocationProviderClient(context);

            locationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
          /*  locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("LogTracker4","onLocationChanged Failure");

                            e.printStackTrace();
                        }
                    });*/
        }
    }

    public void onLocationChanged(Location location) {
        Log.d("LogTracker4", "onLocationChanged");
        // New location has now been determined


        String msg = "Updated Location: " + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
      //  Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();


        // You can now create a LatLng Object for use with maps
        lattitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        lattitudeDouble = location.getLatitude();
        longitudeDouble = location.getLongitude();

        SharedPreferenceController.saveUserLocation(context,lattitudeDouble,longitudeDouble);

        Map<String, String> params = new HashMap<String, String>();
        params.put("lattitude", lattitude);
        params.put("lognitude", longitude);
        params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());


        AddressInterface addressInterface = ApiClient.getApiClient().create(AddressInterface.class);
        Call<List<Addresses>> call = addressInterface.getAddresses(params);
        call.enqueue(new Callback<List<Addresses>>() {
            @Override
            public void onResponse(Call<List<Addresses>> call, Response<List<Addresses>> response) {
                if (response.body() != null) {

                    if (response.raw().cacheResponse() != null) {
                        Log.d("checkCache", "From Cache Memories");
                    } else if (response.raw().networkResponse() != null) {
                        Log.d("checkCache", "Network call Memories");
                    } else {
                        Log.d("checkCache", "Unknown Memories");
                    }

                    for (int i = 0; i < response.body().size(); i++) {
                        // Add a marker in Sydney and move the camera
                        double lat = Double.parseDouble(response.body().get(i).getLat());
                        double log = Double.parseDouble(response.body().get(i).getLon());
                        String markerTitle = response.body().get(i).getName();
                        String aid = response.body().get(i).getAid();
                        String profileUrl = ApiClient.BASE_URL + response.body().get(i).getProfileUrl();

                        LatLng sydney = new LatLng(lat, log);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(markerTitle);
                        markerOptions.snippet("Snippet: " + markerTitle);
                        markerOptions.position(sydney);
                        Random random = new Random();
                        int ran = random.nextInt(8);
                        switch (ran) {
                            case 0:
                                mClusterManager.addItem(new Person(sydney, aid, markerTitle, profileUrl));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                break;
                            case 1:
                                mClusterManager.addItem(new Person(sydney, aid, markerTitle, profileUrl));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                break;
                            case 2:
                                mClusterManager.addItem(new Person(sydney, aid, markerTitle, profileUrl));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                break;

                            case 3:
                                mClusterManager.addItem(new Person(sydney, aid, markerTitle, profileUrl));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                break;
                            case 4:
                                mClusterManager.addItem(new Person(sydney, aid, markerTitle, profileUrl));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                break;
                            case 5:
                                mClusterManager.addItem(new Person(sydney, aid, markerTitle, profileUrl));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                break;
                            case 6:
                                mClusterManager.addItem(new Person(sydney, aid, markerTitle, profileUrl));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                break;
                            case 7:
                                mClusterManager.addItem(new Person(sydney, aid, markerTitle, profileUrl));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                break;
                            case 8:
                                mClusterManager.addItem(new Person(sydney, aid, markerTitle, profileUrl));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                break;
                            default:
                                mClusterManager.addItem(new Person(sydney, aid, markerTitle, profileUrl));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                break;
                        }

                        mClusterManager.cluster();
                        //  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.download)));
                        // mMap.addMarker(markerOptions);
                        // mClusterManager.addItem(new PoiLatLog(sydney,markerTitle,markerTitle));


                        Log.d("LogTracker4", "Lattitude: " + lat + " long: " + log + " title: " + markerTitle);
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Addresses>> call, Throwable t) {
                Log.d("LogTracker4", "Retrofil Failure: " + t.getMessage());

            }
        });


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLatitude()), 12f));
        // CameraUpdate cameraPosition = CameraUpdateFactory.scrollBy(10,30);

        //mMap.animateCamera(cameraPosition);
        //mMap.animateCamera(CameraUpdateFactory.scrollBy(10,10));


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        GoogleMap.OnPoiClickListener onPoiClickListener = new GoogleMap.OnPoiClickListener() {

            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {

            }
        };

        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
               /* AddressInterface addressInterface1 = ApiClient.getApiClient().create(AddressInterface.class);
                Call<Integer> call = addressInterface1.addPoi(new AddPoi( pointOfInterest.latLng.latitude + "", pointOfInterest.latLng.longitude + "", pointOfInterest.name));
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        Log.d("MYcheck11", "OnREsponse hero ");
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.d("MYcheck11", "Failed  hero " + t.getMessage());
                    }
                });*/

                Intent intent = new Intent(context, PlaceActivity.class);
                intent.putExtra("name", pointOfInterest.name);
                intent.putExtra("latitude", pointOfInterest.latLng.latitude + "");
                intent.putExtra("longitude", pointOfInterest.latLng.longitude + "");
                startActivity(intent);

            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("LogTracker4", "onConnected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {

                    onLocationChanged(location);
                } else {
                    if (mGoogleApiClient.isConnected()) {
                        Log.d("LogTracker4", "Client is Connected");
                    } else {
                        Log.d("LogTracker4", "Client is not Connected");
                    }
                    Log.d("LogTracker4", "null Brother location");
                }
            }
        };
        LocationSettingsRequest settingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .build();

        LocationServices.getSettingsClient(context/*activity*/)
                .checkLocationSettings(settingsRequest)
                .addOnCompleteListener(new OnCompleteListener() {
                    public void onComplete(Task task) {
                        if (task.isSuccessful()) {
                            // ***REQUEST LAST LOCATION HERE***
                            Log.d("LogTracker4", "onConnected direct Sucesfull");
                            getLastLocation();
                        } else {
                            Exception e = task.getException();
                            if (e instanceof ResolvableApiException) {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult()
                                try {
                                    ((ResolvableApiException) e).startResolutionForResult(context, 12);
                                } catch (IntentSender.SendIntentException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                //Location can not be resolved, inform the user
                            }
                        }
                    }
                });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public boolean onClusterClick(Cluster<Person> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        // Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        Log.d("cehcks", "Cluster position: " + cluster.getPosition());
        final LatLngBounds bounds = builder.build();
        Log.d("cehcks", bounds.northeast + "" + " South: " + bounds.southwest + " Center: " + bounds.getCenter() + " ");
      /*  if(bounds.contains(cluster.getPosition())){
            Toast.makeText(context,"Same",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Different",Toast.LENGTH_SHORT).show();
        }*/

        Location locationOne = new Location("");
        locationOne.setLatitude(cluster.getPosition().latitude);
        locationOne.setLongitude(cluster.getPosition().longitude);

        Location locationCenter = new Location("");
        locationCenter.setLatitude(bounds.getCenter().latitude);
        locationCenter.setLongitude(bounds.getCenter().longitude);


        float distanceInMetersOne = locationOne.distanceTo(locationCenter);
        if (distanceInMetersOne < 5) {
            // Toast.makeText(context,"Same",Toast.LENGTH_SHORT).show();

        /*    Log.d("sameWith5",cluster.getItems().iterator().next().name+" name ");
            Log.d("sameWith5",cluster.getItems().iterator().next().getPosition().latitude+" lattitude");
            Log.d("sameWith5",cluster.getItems().iterator().next().getPosition().longitude+" longitude");*/

            Intent intent = new Intent(context, PlaceActivity.class);
            intent.putExtra("name", cluster.getItems().iterator().next().name);
            intent.putExtra("latitude", cluster.getItems().iterator().next().getPosition().latitude + "");
            intent.putExtra("longitude", cluster.getItems().iterator().next().getPosition().longitude + "");
            startActivity(intent);
            //Toast.makeText(context,"Inside if ",Toast.LENGTH_SHORT).show();

        }

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onClusterItemClick(Person person) {

        Intent intent = new Intent(context, PlaceActivity.class);
        intent.putExtra("name", person.name);
        intent.putExtra("latitude", person.getPosition().latitude + "");
        intent.putExtra("longitude", person.getPosition().longitude + "");
        startActivity(intent);

        return true;
    }

    @Override
    public void onClusterItemInfoWindowClick(Person person) {

    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Person> cluster) {

    }


    public class AddPoi {
        String lat;
        String lon;
        String name;

        AddPoi(String lat, String lon, String name, String placeId) {
            this.lat = lat;
            this.lon = lon;
            this.name = name;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }


    }

    public class PersonRenderer extends DefaultClusterRenderer<Person> {
        private final IconGenerator mIconGenerator = new IconGenerator(context.getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(context.getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PersonRenderer() {
            super(context.getApplicationContext(), mMap, mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.item_multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(context.getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(final Person person, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            Picasso.with(context).load(person.profilePhoto).placeholder(R.drawable.img_default_user).networkPolicy(NetworkPolicy.OFFLINE).into(mImageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(person.profilePhoto).placeholder(R.drawable.img_default_user).into(mImageView);
                }
            });

            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Person> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Person p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = retrieve(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    private static class CacheTarget implements Target {
        private Bitmap cacheBitmap;

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            cacheBitmap = bitmap;
            Log.d("checkOnBitMapLoaded", "OnBitMapLoaded");
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }

        public Bitmap getCacheBitmap() {
            return cacheBitmap;
        }
    }

    public Drawable retrieve(String url) {
        final CacheTarget cacheTarget = new CacheTarget();
        Picasso.with(context)
                .load(url)
                .into(cacheTarget);
        return new BitmapDrawable(context.getResources(), cacheTarget.getCacheBitmap());

    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferenceController.saveInOut(context,true);
    }
}
