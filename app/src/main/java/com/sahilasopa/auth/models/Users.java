package com.sahilasopa.auth.models;

public class Users {
    String id;
    String username;
    String email;
    String profile_pic;
    String password;
    String lastMessage;
    String status;
    String contact_no;

    public String getStatus() {
        return status;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public Users(String profile_pic, String username, String email, String password, String id, String lastMessage, String status) {
        this.profile_pic = profile_pic;
        this.username = username;
        this.email = email;
        this.password = password;
        this.id = id;
        this.lastMessage = lastMessage;
        this.status = status;
    }

    public Users() {
    }

    public Users(String username, String email, String password) {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
