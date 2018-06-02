package com.footprints.footprints.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

public class Comment {

    @SerializedName("commentModel")
    @Expose
    private List<CommentModel> commentModel = null;

    public List<CommentModel> getCommentModel() {
        return commentModel;
    }

    public void setCommentModel(List<CommentModel> commentModel) {
        this.commentModel = commentModel;
    }

    public Comment withCommentModel(List<CommentModel> commentModel) {
        this.commentModel = commentModel;
        return this;
    }
@Parcel
    public static class CommentModel {

        @SerializedName("cid")
        @Expose
        private String cid;
        @SerializedName("comment")
        @Expose
        private String comment;
        @SerializedName("commentBy")
        @Expose
        private String commentBy;
        @SerializedName("commentDate")
        @Expose
        private String commentDate;
        @SerializedName("superParentId")
        @Expose
        private String superParentId;
        @SerializedName("parentId")
        @Expose
        private String parentId;
        @SerializedName("hasSubComment")
        @Expose
        private String hasSubComment;
        @SerializedName("level")
        @Expose
        private String level;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("profileUrl")
        @Expose
        private String profileUrl;
        @SerializedName("userToken")
        @Expose
        private String userToken;
        @SerializedName("subComments")
        @Expose
        private SubComments subComments;

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public CommentModel withCid(String cid) {
            this.cid = cid;
            return this;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public CommentModel withComment(String comment) {
            this.comment = comment;
            return this;
        }

        public String getCommentBy() {
            return commentBy;
        }

        public void setCommentBy(String commentBy) {
            this.commentBy = commentBy;
        }

        public CommentModel withCommentBy(String commentBy) {
            this.commentBy = commentBy;
            return this;
        }

        public String getCommentDate() {
            return commentDate;
        }

        public void setCommentDate(String commentDate) {
            this.commentDate = commentDate;
        }

        public CommentModel withCommentDate(String commentDate) {
            this.commentDate = commentDate;
            return this;
        }

        public String getSuperParentId() {
            return superParentId;
        }

        public void setSuperParentId(String superParentId) {
            this.superParentId = superParentId;
        }

        public CommentModel withSuperParentId(String superParentId) {
            this.superParentId = superParentId;
            return this;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public CommentModel withParentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public String getHasSubComment() {
            return hasSubComment;
        }

        public void setHasSubComment(String hasSubComment) {
            this.hasSubComment = hasSubComment;
        }

        public CommentModel withHasSubComment(String hasSubComment) {
            this.hasSubComment = hasSubComment;
            return this;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public CommentModel withLevel(String level) {
            this.level = level;
            return this;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public CommentModel withName(String name) {
            this.name = name;
            return this;
        }

        public String getProfileUrl() {
            return profileUrl;
        }

        public void setProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
        }

        public CommentModel withProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
            return this;
        }

        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }

        public CommentModel withUserToken(String userToken) {
            this.userToken = userToken;
            return this;
        }

        public SubComments getSubComments() {
            return subComments;
        }

        public void setSubComments(SubComments subComments) {
            this.subComments = subComments;
        }

        public CommentModel withSubComments(SubComments subComments) {
            this.subComments = subComments;
            return this;
        }

    }


    @Parcel
    public static  class LastComment {

        @SerializedName("comment")
        @Expose
        private String comment;
        @SerializedName("commentDate")
        @Expose
        private String commentDate;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("profileUrl")
        @Expose
        private String profileUrl;
        @SerializedName("commentBy")
        @Expose
        private String commentBy;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public LastComment withComment(String comment) {
            this.comment = comment;
            return this;
        }

        public String getCommentDate() {
            return commentDate;
        }

        public void setCommentDate(String commentDate) {
            this.commentDate = commentDate;
        }

        public LastComment withCommentDate(String commentDate) {
            this.commentDate = commentDate;
            return this;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LastComment withName(String name) {
            this.name = name;
            return this;
        }

        public String getProfileUrl() {
            return profileUrl;
        }

        public void setProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
        }

        public LastComment withProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
            return this;
        }

        public String getCommentBy() {
            return commentBy;
        }

        public void setCommentBy(String commentBy) {
            this.commentBy = commentBy;
        }
    }

    @Parcel

    public static  class SubComments {

        @SerializedName("total")
        @Expose
        private Integer total;
        @SerializedName("lastComment")
        @Expose
        private List<LastComment> lastComment = null;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public SubComments withTotal(Integer total) {
            this.total = total;
            return this;
        }

        public List<LastComment> getLastComment() {
            return lastComment;
        }

        public void setLastComment(List<LastComment> lastComment) {
            this.lastComment = lastComment;
        }

        public SubComments withLastComment(List<LastComment> lastComment) {
            this.lastComment = lastComment;
            return this;
        }

    }
}

