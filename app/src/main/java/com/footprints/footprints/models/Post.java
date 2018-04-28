 package com.footprints.footprints.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Post {

    @SerializedName("memories")
    @Expose
    private Memories memories;

    public Memories getMemories() {
        return memories;
    }

    public void setMemories(Memories memories) {
        this.memories = memories;
    }

    public class Memories {

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


    public class Message {

        @SerializedName("postId")
        @Expose
        private String postId;
        @SerializedName("post")
        @Expose
        private String post;
        @SerializedName("postUserId")
        @Expose
        private String postUserId;
        @SerializedName("statusImage")
        @Expose
        private String statusImage;
        @SerializedName("statusTime")
        @Expose
        private String statusTime;
        @SerializedName("likeCount")
        @Expose
        private String likeCount;
        @SerializedName("hasComment")
        @Expose
        private String hasComment;
        @SerializedName("privacy")
        @Expose
        private String privacy;
        @SerializedName("aid")
        @Expose
        private String aid;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("profileUrl")
        @Expose
        private String profileUrl;
        @SerializedName("userToken")
        @Expose
        private String userToken;

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getPost() {
            return post;
        }

        public void setPost(String post) {
            this.post = post;
        }

        public String getPostUserId() {
            return postUserId;
        }

        public void setPostUserId(String postUserId) {
            this.postUserId = postUserId;
        }

        public String getStatusImage() {
            return statusImage;
        }

        public void setStatusImage(String statusImage) {
            this.statusImage = statusImage;
        }

        public String getStatusTime() {
            return statusTime;
        }

        public void setStatusTime(String statusTime) {
            this.statusTime = statusTime;
        }

        public String getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(String likeCount) {
            this.likeCount = likeCount;
        }

        public String getHasComment() {
            return hasComment;
        }

        public void setHasComment(String hasComment) {
            this.hasComment = hasComment;
        }

        public String getPrivacy() {
            return privacy;
        }

        public void setPrivacy(String privacy) {
            this.privacy = privacy;
        }

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