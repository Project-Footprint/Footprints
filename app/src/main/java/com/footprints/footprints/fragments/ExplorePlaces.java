package com.footprints.footprints.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.footprints.footprints.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExplorePlaces extends Fragment {


    public ExplorePlaces() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore_places, container, false);
    }

}
