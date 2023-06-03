package com.hcmute.edu.vn.zalo.group11.model;

public class User {

    String userID;
    String  userName;
    String phoneNumber;
    String profileImage;
    String birthday;
    String address;

    public User(){
    }

    public User(String userID, String userName, String phoneNumber, String profileImage, String birthday, String address) {
        this.userID = userID;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.birthday = birthday;
        this.address = address;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
