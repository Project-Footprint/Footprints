package com.footprints.footprints.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Addresses {

    @SerializedName("aid")
    @Expose
    private String aid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("postUserId")
    @Expose
    private String postUserId;
    @SerializedName("profileUrl")
    @Expose
    private String profileUrl;
    @SerializedName("distance")
    @Expose
    private String distance;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getPostUserId() {
        return postUserId;
    }

    public void setPostUserId(String postUserId) {
        this.postUserId = postUserId;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

}