package com.footprints.footprints.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.footprints.footprints.R;
import com.footprints.footprints.adapter.PlaceViewPagerAdapter;
import com.footprints.footprints.models.FootprintsImage;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.AddressInterface;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, BaseSliderView.OnSliderClickListener {

    private Toolbar toolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PlaceViewPagerAdapter placeViewPagerAdapter;
    private FloatingActionButton floatingActionButton;
    public static String name, latitude, longitude;
    SliderLayout sliderLayout;
    TextView default_place_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*requestWindowFeature(Window.FEATURE_NO_TITLE); */
        //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_place_details);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlaceActivity.this, MapsActivity.class));
            }
        });

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setTitle("@" + name);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        sliderLayout = collapsingToolbarLayout.findViewById(R.id.placeslider);


       /* placeCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String >(placeCoverImage,"shared");
                Intent chatIntent = new Intent(PlaceActivity.this,FullmageActivity.class);
                ActivityOptions activityOptions = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(PlaceActivity.this,pairs);
                }
                chatIntent.putExtra("imageUrl", "R.drawable.manang");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(chatIntent,activityOptions.toBundle());
                }else{
                    startActivity(chatIntent);
                }
            }
        });*/
        default_place_name = findViewById(R.id.default_place_name);

        mViewPager = findViewById(R.id.ViewPager_profile);
        mTabLayout = findViewById(R.id.main_tab_profile);

        placeViewPagerAdapter = new PlaceViewPagerAdapter(getSupportFragmentManager(), 2, PlaceActivity.this);
        mViewPager.setAdapter(placeViewPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mTabLayout.setupWithViewPager(mViewPager);

        floatingActionButton = findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceActivity.this, UploadMemories.class);
                intent.putExtra("name", name);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        getFootprints();
    }

    private void getFootprints() {
        AddressInterface addressInterface = ApiClient.getApiClient().create(AddressInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("lattitude", latitude);
        params.put("longnitude", longitude);
        params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Call<FootprintsImage> call = addressInterface.getPlaceFootprints(params);
        call.enqueue(new Callback<FootprintsImage>() {
            @Override
            public void onResponse(@NonNull Call<FootprintsImage> call, Response<FootprintsImage> response) {

                if (response.body() != null) {

                    DefaultSliderView defaultSliderView;
                    List<FootprintsImage.Image> urls = response.body().getImages();
                    Log.d("checkTimes", urls.size()+" Size should be");

                    if (urls.size() == 0) {
                        default_place_name.setText("Photo Memories not avaiable.\nBe the first \nto leave your footprints at \n" + name);
                        default_place_name.setVisibility(View.VISIBLE);
                        sliderLayout.setVisibility(View.GONE);
                    } else {
                        for (int i = 0; i < urls.size(); i++) {
                            Log.d("checkTimes", urls.get(i).getStatusImage()+" should be");
                            defaultSliderView = new DefaultSliderView(PlaceActivity.this);
                            defaultSliderView.empty(R.drawable.default_image_placeholder);
                            defaultSliderView.image(ApiClient.BASE_URL + urls.get(i).getStatusImage())
                                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                    .setOnSliderClickListener(PlaceActivity.this);


                            sliderLayout.addSlider(defaultSliderView);
                        }
                        sliderLayout.startAutoCycle();
                        sliderLayout.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
                        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
                        sliderLayout.setDuration(4000);
                    }


                }


            }

            @Override
            public void onFailure(Call<FootprintsImage> call, Throwable t) {
                Log.d("checkUrlss", "Failed brother");
                Toast.makeText(PlaceActivity.this, " Failed... Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                floatingActionButton.show();
                break;
            case 1:
                floatingActionButton.show();
                break;

            case 2:
            default:
                floatingActionButton.hide();

                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PlaceActivity.this, MapsActivity.class));
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();
    }
}
