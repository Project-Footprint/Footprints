package com.footprints.footprints.models;
public  class AddComment {
    String comment;
    String commentBy;
    String superParentId;
    String parentId;
    String hasSubComment;
    String level;
    String postUserId;
    String commentUserId;


    public AddComment(String comment, String commentBy, String superParentId, String parentId, String hasSubComment, String level, String postUserId, String commentUserId) {
        this.comment = comment;
        this.commentBy = commentBy;
        this.superParentId = superParentId;
        this.parentId = parentId;
        this.hasSubComment = hasSubComment;
        this.level = level;
        this.postUserId = postUserId;
        this.commentUserId = commentUserId;

    }
}