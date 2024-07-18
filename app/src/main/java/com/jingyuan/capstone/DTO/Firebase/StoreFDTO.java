package com.jingyuan.capstone.DTO.Firebase;

public class StoreFDTO {
    private String doc;
    private String name;
    private String phone;
    private String ownerDoc;
    private String description;
    private String location;
    private String thumbnail;

    public StoreFDTO() {
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOwnerDoc() {
        return ownerDoc;
    }

    public void setOwnerDoc(String ownerDoc) {
        this.ownerDoc = ownerDoc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
