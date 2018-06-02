package com.footprints.footprints.models;

public class AddLike {
    String userId;
    String contentId;
    String contentOwnerId;
    String likeLevel;

    public AddLike(String userId, String contentId, String contentOwnerId, String likeLevel) {
        this.userId = userId;
        this.contentId = contentId;
        this.contentOwnerId = contentOwnerId;
        this.likeLevel = likeLevel;
    }
}