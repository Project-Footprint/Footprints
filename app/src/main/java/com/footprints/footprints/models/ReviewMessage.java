package com.footprints.footprints.models;

public class ReviewMessage {


    private String rid;
    private String aid;
    private String review;
    private String date;
    private String postUserId;
    private String rating;
    private String name;
    private String profileUrl;
    private String userToken;

    public ReviewMessage(String rid, String aid, String review, String date, String postUserId, String rating, String name, String profileUrl, String userToken) {
        this.rid = rid;
        this.aid = aid;
        this.review = review;
        this.date = date;
        this.postUserId = postUserId;
        this.rating = rating;
        this.name = name;
        this.profileUrl = profileUrl;
        this.userToken = userToken;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostUserId() {
        return postUserId;
    }

    public void setPostUserId(String postUserId) {
        this.postUserId = postUserId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
