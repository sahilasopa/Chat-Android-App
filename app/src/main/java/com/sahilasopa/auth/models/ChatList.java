package com.sahilasopa.auth.models;

public class ChatList {
    private String userId;
    private String me;
    private long timestamp;

    public ChatList() {

    }

    public String getMe() {
        return me;
    }

    public void setMe(String me) {
        this.me = me;
    }

    public ChatList(String userId, long timestamp, String me) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.me = me;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
