package com.footprints.footprints.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("nid")
    @Expose
    private String nid;
    @SerializedName("notificationTo")
    @Expose
    private String notificationTo;
    @SerializedName("notificationFrom")
    @Expose
    private String notificationFrom;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("notificationTime")
    @Expose
    private String notificationTime;
    @SerializedName("postId")
    @Expose
    private String postId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profileUrl")
    @Expose
    private String profileUrl;
    @SerializedName("post")
    @Expose
    private String post;

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public Notification withNid(String nid) {
        this.nid = nid;
        return this;
    }

    public String getNotificationTo() {
        return notificationTo;
    }

    public void setNotificationTo(String notificationTo) {
        this.notificationTo = notificationTo;
    }

    public Notification withNotificationTo(String notificationTo) {
        this.notificationTo = notificationTo;
        return this;
    }

    public String getNotificationFrom() {
        return notificationFrom;
    }

    public void setNotificationFrom(String notificationFrom) {
        this.notificationFrom = notificationFrom;
    }

    public Notification withNotificationFrom(String notificationFrom) {
        this.notificationFrom = notificationFrom;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Notification withType(String type) {
        this.type = type;
        return this;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public Notification withNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
        return this;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Notification withPostId(String postId) {
        this.postId = postId;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Notification withName(String name) {
        this.name = name;
        return this;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public Notification withProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
        return this;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Notification withPost(String post) {
        this.post = post;
        return this;
    }

}