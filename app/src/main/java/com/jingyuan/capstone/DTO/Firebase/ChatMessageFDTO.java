package com.jingyuan.capstone.DTO.Firebase;

import com.google.firebase.Timestamp;

public class ChatMessageFDTO {
    private String sender;
    private String message;
    private Timestamp timestamp;

    public ChatMessageFDTO() {
    }

    public ChatMessageFDTO(String sender, String message, Timestamp timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
