package com.footprints.footprints.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event {

    @SerializedName("eid")
    @Expose
    private String eid;
    @SerializedName("ename")
    @Expose
    private String ename;
    @SerializedName("eplace")
    @Expose
    private String eplace;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("log")
    @Expose
    private String log;
    @SerializedName("startdate")
    @Expose
    private String startdate;
    @SerializedName("enddate")
    @Expose
    private String enddate;
    @SerializedName("distance")
    @Expose
    private String distance;

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getEname() {
        return ename;
    }

    public String getEplace() {
        return eplace;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public void setEplace(String eplace) {
        this.eplace = eplace;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Event(String eid, String ename, String eplace, String lat, String log, String startdate, String enddate, String distance) {
        this.eid = eid;
        this.ename = ename;
        this.eplace = eplace;
        this.lat = lat;
        this.log = log;
        this.startdate = startdate;
        this.enddate = enddate;
        this.distance = distance;
    }
}