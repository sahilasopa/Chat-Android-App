package com.sahilasopa.auth.models;

public class Users {
    String profile_pic;
    String username;
    String email;
    String password;
    String id;
    String lastMessage;

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    String contact_no;

    public Users(String profile_pic, String username, String email, String password, String id, String lastMessage) {
        this.profile_pic = profile_pic;
        this.username = username;
        this.email = email;
        this.password = password;
        this.id = id;
        this.lastMessage = lastMessage;
    }
    public String getProfile_pic() {
        return profile_pic;
    }

    public Users() {
    }

    public Users(String username, String email,String  password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }


    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

}
