package com.jingyuan.capstone.DTO.Firebase;

import androidx.annotation.NonNull;

public class CategoryFDTO {
    private String id;
    private String name;
    private String thumbnail = "1686506930924.jpg";

    public String getThumbnail() {
        return  thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public CategoryFDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString () {
        return id + "\n" + name + "\n" + thumbnail + "\n";
    }

}
