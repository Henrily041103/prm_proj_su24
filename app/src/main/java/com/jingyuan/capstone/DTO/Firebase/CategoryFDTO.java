package com.jingyuan.capstone.DTO.Firebase;

public class CategoryFDTO {
    private String doc;
    private String name;
    private String thumbnail;

    public String getThumbnail() {
        return "https://firebasestorage.googleapis.com/v0/b/capstone-c62ee.appspot.com/o/" + "1686506930924.jpg" + "?alt=media";
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public CategoryFDTO() {
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
}
