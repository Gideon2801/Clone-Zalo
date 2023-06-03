package com.hcmute.edu.vn.zalo.group11.model;

public class Message {
    private String messageId, message, senderPhone, imageUrl;
    //Thời gian người dùng gửi tin nhắn
    private long timestamp;
    // feeling để biết react biểu tượng nào
    private int feeling = -1;

    public Message() {
    }

    public Message(String message, String senderPhone, long timestamp) {
        this.message = message;
        this.senderPhone = senderPhone;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderPhone;
    }

    public void setSenderId(String senderId) {
        this.senderPhone = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
