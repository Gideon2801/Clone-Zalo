package com.hcmute.edu.vn.zalo.group11.model;

public class UserStatus {
    String statusID, phone, name, imageProfile, imageStatus, description;
    long time;

    public UserStatus() {
    }

    public UserStatus(String statusID, String phone, String name, String imageProfile, String imageStatus, String description, long time) {
        this.statusID = statusID;
        this.phone = phone;
        this.name = name;
        this.imageProfile = imageProfile;
        this.imageStatus = imageStatus;
        this.description = description;
        this.time = time;
    }

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(String imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
