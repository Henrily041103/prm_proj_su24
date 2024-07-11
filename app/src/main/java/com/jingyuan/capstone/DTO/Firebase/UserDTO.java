package com.jingyuan.capstone.DTO.Firebase;

public class UserDTO {
    private String username;
    private String pfp;
    private String email;
    private String fcmtoken;

    public UserDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPfp() {
        return "https://firebasestorage.googleapis.com/v0/b/capstone-c62ee.appspot.com/o/" + pfp + "?alt=media";
    }

    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFcmtoken() {
        return fcmtoken;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }
}
