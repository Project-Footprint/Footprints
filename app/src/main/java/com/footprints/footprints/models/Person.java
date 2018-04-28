package com.footprints.footprints.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Person implements ClusterItem {
    public final String name;
    public final String profilePhoto;
    private final LatLng mPosition;
    private final String aid;

    public Person(LatLng position, String aid, String name, String pictureResource) {
        this.name = name;
        profilePhoto = pictureResource;
        mPosition = position;
        this.aid = aid;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return name;
    }

    public String getAid() {
        return aid;
    }
}