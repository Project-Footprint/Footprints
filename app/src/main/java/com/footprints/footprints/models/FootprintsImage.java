package com.footprints.footprints.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FootprintsImage {

    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("success")
    @Expose
    private String success;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
   public class Image {

        @SerializedName("statusImage")
        @Expose
        private String statusImage;

        public String getStatusImage() {
            return statusImage;
        }

        public void setStatusImage(String statusImage) {
            this.statusImage = statusImage;
        }

    }
}


