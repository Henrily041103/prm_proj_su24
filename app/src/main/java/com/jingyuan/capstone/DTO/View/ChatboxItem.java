package com.jingyuan.capstone.DTO.View;

public class ChatboxItem {
    private String chatroomDoc;
    private String username;
    private String pfp;
    private String lastMessage;

    public String getChatroomDoc() {
        return chatroomDoc;
    }

    public void setChatroomDoc(String chatroomDoc) {
        this.chatroomDoc = chatroomDoc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPfp() {
        return pfp;
    }

    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
