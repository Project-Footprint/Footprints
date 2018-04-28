package com.footprints.footprints.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.footprints.footprints.R;
import com.footprints.footprints.fragments.ExploreEvents;
import com.footprints.footprints.fragments.ExploreMemories;
import com.footprints.footprints.fragments.ExplorePlaces;

public class MapsActivity extends FragmentActivity {

    FrameLayout frameLayout;
    private ExploreEvents exploreEvents;
    private ExploreMemories exploreMemories;
    private ExplorePlaces explorePlaces;


    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_maps);
        frameLayout = findViewById(R.id.mainFramelayout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        exploreEvents = new ExploreEvents();
        exploreMemories = new ExploreMemories();
        explorePlaces = new ExplorePlaces();

        setFragment(exploreMemories);

        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_main);
        bottomNavigationView.setItemBackgroundResource(R.color.colorPrimary);
        bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(bottomNavigationView.getContext(), R.color.nav_item_colors));
        bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(bottomNavigationView.getContext(), R.color.nav_item_colors));

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.explore_memories:
                               // bottomNavigationView.setItemBackgroundResource(R.color.colorPrimary);
                                setFragment(exploreMemories);
                                break;
                            case R.id.explore_places:
                               // bottomNavigationView.setItemBackgroundResource(R.color.colorAccent);
                                setFragment(explorePlaces);
                                break;

                            case R.id.explore_events:
                               // bottomNavigationView.setItemBackgroundResource(R.color.cardview_dark_background);
                                setFragment(exploreEvents);
                                break;

                        }
                        return true;
                    }
                });


    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFramelayout, fragment);
        fragmentTransaction.commit();
    }
}
