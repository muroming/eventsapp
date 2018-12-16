package com.f.events.eventapp.Data;

public class UserDAO {
    private String name;
    private String imageUri;

    public UserDAO(String name, String imageUri) {
        this.name = name;
        this.imageUri = imageUri;
    }

    public UserDAO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
