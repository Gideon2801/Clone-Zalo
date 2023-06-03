package com.hcmute.edu.vn.zalo.group11.model;
//Class này cho biết thông tin của 1 story
public class Story {
    String storyID, imageURL;
    long time;

    public Story() {
    }

    public Story(String storyID, String imageURL, long time) {
        this.storyID = storyID;
        this.imageURL = imageURL;
        this.time = time;
    }

    public String getStoryID() {
        return storyID;
    }

    public void setStoryID(String storyID) {
        this.storyID = storyID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
