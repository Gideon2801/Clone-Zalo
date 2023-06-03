package com.hcmute.edu.vn.zalo.group11.model;

import java.util.ArrayList;
//Class này được dùng để lưu thông tin người dùng up story và các story người dùng up
public class UserStory {
    String phone, name, imageProfile;
    long lastUpdate;
    //Danh sách chứa các story đã up
    ArrayList<Story> stories;

    public UserStory() {
    }

    public UserStory(String name, String imageProfile, long lastUpdate, ArrayList<Story> stories) {
        this.name = name;
        this.imageProfile = imageProfile;
        this.lastUpdate = lastUpdate;
        this.stories = stories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<Story> getStories() {
        return stories;
    }

    public void setStories(ArrayList<Story> stories) {
        this.stories = stories;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
