package com.jingyuan.capstone.DTO.Firebase;

import com.google.firebase.Timestamp;

public class ChatRoomFDTO {
    private String userDoc;
    private String shopDoc;
    private Timestamp timeCreated;

    public ChatRoomFDTO() {
    }

    public String getUserDoc() {
        return userDoc;
    }

    public void setUserDoc(String userDoc) {
        this.userDoc = userDoc;
    }

    public String getShopDoc() {
        return shopDoc;
    }

    public void setShopDoc(String shopDoc) {
        this.shopDoc = shopDoc;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }
}
