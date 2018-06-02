package com.footprints.footprints.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostCommentModel {

    @SerializedName("commentModel")
    @Expose
    private Comment.CommentModel commentModel = null;
    @SerializedName("success")
    @Expose
    private Integer success;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Comment.CommentModel getCommentModel() {
        return commentModel;
    }

    public void setCommentModel(Comment.CommentModel commentModel) {
        this.commentModel = commentModel;
    }
}