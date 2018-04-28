package com.footprints.footprints.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.footprints.footprints.fragments.PlaceFriendFragment;
import com.footprints.footprints.fragments.PlacePublicFragment;
import com.footprints.footprints.fragments.PlaceReviewFragment;


public class PlaceViewPagerAdapter extends FragmentPagerAdapter {
    int size = 0;
    Context context;


    public PlaceViewPagerAdapter(FragmentManager fm, int size, Context context) {
        super(fm);
        this.size = size;
        this.context = context;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:

                PlaceFriendFragment friendFragment = new PlaceFriendFragment();
                return friendFragment;

            case 1:

                PlacePublicFragment publicFragment = new PlacePublicFragment();
                return publicFragment;


            case 2:

                PlaceReviewFragment reviewFragment = new PlaceReviewFragment();
                return reviewFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {


            case 0:

                return "Friends";
            case 1:

                    return "Public";


            case 2:

                return "Reviews";

            default:
                return null;
        }
    }

}
