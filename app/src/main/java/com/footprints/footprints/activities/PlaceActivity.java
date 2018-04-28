package com.footprints.footprints.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.footprints.footprints.R;
import com.footprints.footprints.adapter.PlaceViewPagerAdapter;

public class PlaceActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private Toolbar toolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PlaceViewPagerAdapter placeViewPagerAdapter;
    private FloatingActionButton floatingActionButton;
    public static String name, latitude, longitude;
    ImageView placeCoverImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
               startActivity(new Intent(PlaceActivity.this,MapsActivity.class));
            }
        });

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setTitle("@" + name);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        placeCoverImage =  collapsingToolbarLayout.findViewById(R.id.placeFeaturedId);
        placeCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String >(placeCoverImage,"sharedProfile");
                Intent chatIntent = new Intent(PlaceActivity.this,FullmageActivity.class);
                ActivityOptions activityOptions = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(PlaceActivity.this,pairs);
                }
                chatIntent.putExtra("imageUrl", "manang");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(chatIntent,activityOptions.toBundle());
                }else{
                    startActivity(chatIntent);
                }
            }
        });


        mViewPager = findViewById(R.id.ViewPager_profile);
        mTabLayout = findViewById(R.id.main_tab_profile);
        placeViewPagerAdapter = new PlaceViewPagerAdapter(getSupportFragmentManager(), 3, PlaceActivity.this);
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
                intent.putExtra("longitude", longitude );
                startActivity(intent);
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
        startActivity(new Intent(PlaceActivity.this,MapsActivity.class));
    }
}
