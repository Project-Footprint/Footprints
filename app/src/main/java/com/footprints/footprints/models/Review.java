package com.footprints.footprints.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;






public class Review {

    @SerializedName("review")
    @Expose
    private Review_ review;

    public Review_ getReview() {
        return review;
    }

    public void setReview(Review_ review) {
        this.review = review;
    }

    public class Review_ {

        @SerializedName("message")
        @Expose
        private List<Message> message = null;
        @SerializedName("success")
        @Expose
        private Integer success;

        public List<Message> getMessage() {
            return message;
        }

        public void setMessage(List<Message> message) {
            this.message = message;
        }

        public Integer getSuccess() {
            return success;
        }

        public void setSuccess(Integer success) {
            this.success = success;
        }

    }


  public  class Message {

        @SerializedName("rid")
        @Expose
        private String rid;
        @SerializedName("aid")
        @Expose
        private String aid;
        @SerializedName("review")
        @Expose
        private String review;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("postUserId")
        @Expose
        private String postUserId;
        @SerializedName("rating")
        @Expose
        private String rating;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("profileUrl")
        @Expose
        private String profileUrl;
        @SerializedName("userToken")
        @Expose
        private String userToken;

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
}
