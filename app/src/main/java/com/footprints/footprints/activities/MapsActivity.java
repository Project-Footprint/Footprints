package com.footprints.footprints.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.models.Addresses;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    String lattitude, longitude;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    LocationCallback locationCallback;
    private ClusterManager<MapsActivity.PoiLatLog> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mClusterManager = new ClusterManager<MapsActivity.PoiLatLog>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        Log.d("LogTracker4", "onMapReady");

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setBuildingsEnabled(true);
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        Log.d("LogTracker4", "buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 12:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d("LogTracker4", "onConnected Switch Sucesfull");
                        getLastLocation();
                        Toast.makeText(MapsActivity.this, " Location permission Granted", Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to ...
                        Toast.makeText(MapsActivity.this, "We need Location permission", Toast.LENGTH_LONG).show();
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            final FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

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
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        lattitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());


        Map<String, String> params = new HashMap<String, String>();
        params.put("lattitude", lattitude);
        params.put("lognitude", longitude);


        AddressInterface addressInterface = ApiClient.getApiClient().create(AddressInterface.class);
        Call<List<Addresses>> call = addressInterface.getAddresses(params);
        call.enqueue(new Callback<List<Addresses>>() {
            @Override
            public void onResponse(Call<List<Addresses>> call, Response<List<Addresses>> response) {
                if (response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {
                        // Add a marker in Sydney and move the camera
                        double lat = Double.parseDouble(response.body().get(i).getLat());
                        double log = Double.parseDouble(response.body().get(i).getLon());
                        String markerTitle = response.body().get(i).getName();

                        LatLng sydney = new LatLng(lat, log);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(markerTitle);
                        markerOptions.snippet("Snippet: " + markerTitle);
                        markerOptions.position(sydney);
                        Random random = new Random();
                        int ran = random.nextInt(8);
                        switch (ran) {
                            case 0:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                break;
                            case 1:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                break;
                            case 2:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                break;

                            case 3:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                break;
                            case 4:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                break;
                            case 5:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                break;
                            case 6:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                break;
                            case 7:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                break;
                            case 8:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                break;
                            default:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                break;
                        }


                        //  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.download)));
                       // mMap.addMarker(markerOptions);
                        mClusterManager.addItem(new PoiLatLog(sydney,markerTitle,markerTitle));

                        Log.d("LogTracker4", "Lattitude: " + lat + " long: " + log + " title: " + markerTitle);
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Addresses>> call, Throwable t) {
                Log.d("LogTracker4", "Retrofil Failure: " + t.getMessage());

            }
        });


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLatitude()), 13f));
        // CameraUpdate cameraPosition = CameraUpdateFactory.scrollBy(10,30);

        //mMap.animateCamera(cameraPosition);
        //mMap.animateCamera(CameraUpdateFactory.scrollBy(10,10));


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                Log.d("MYcheck112", pointOfInterest.name + " Latlog: " + pointOfInterest.latLng.latitude + pointOfInterest.latLng.longitude + " placeID: " + pointOfInterest.placeId);
                AddressInterface addressInterface1 = ApiClient.getApiClient().create(AddressInterface.class);
                Call<Integer> call = addressInterface1.addPoi(new PoiLatLog(pointOfInterest.name, pointOfInterest.latLng.latitude + "", pointOfInterest.latLng.longitude + "", pointOfInterest.placeId, pointOfInterest.latLng.latitude, pointOfInterest.latLng.longitude));
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        Log.d("MYcheck11", "OnREsponse hero ");
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.d("MYcheck11", "Failed  hero " + t.getMessage());
                    }
                });
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

        LocationServices.getSettingsClient(this/*activity*/)
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
                                    ((ResolvableApiException) e).startResolutionForResult(MapsActivity.this, 12);
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

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        TextView textView = customMarkerView.findViewById(R.id.postNumber);
        textView.setText("9+");
        markerImageView.setImageResource(resId);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();

        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        // paint defines the text color, stroke width and size
        Paint color = new Paint();
        color.setTextSize(35);
        color.setColor(Color.BLACK);

        canvas.drawText("Kapil", 50, 50, color);

        if (drawable != null)
            drawable.draw(canvas);

        customMarkerView.draw(canvas);

        return returnedBitmap;
    }

    public class PoiLatLog implements ClusterItem {

        String lat;
        String lon;
        String name;
        String placeId;

        private LatLng mPosition;
        private String mTitle;
        private String mSnippet;


        PoiLatLog(String name, String lat, String log, String placeId, double lattitude, double logitude) {
            this.lat = lat;
            this.lon = log;
            this.name = name;
            this.placeId = placeId;


            mPosition = new LatLng(lattitude, logitude);
            this.mTitle = name;
            this.mSnippet = name;
        }

        public PoiLatLog(LatLng mPosition, String mTitle, String mSnippet) {
            this.mPosition = mPosition;
            this.mTitle = mTitle;
            this.mSnippet = mSnippet;
        }

        @Override
        public LatLng getPosition() {
            return this.mPosition;
        }

        @Override
        public String getTitle() {
            return this.mTitle;
        }

        @Override
        public String getSnippet() {
            return this.mSnippet;
        }
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }
}
