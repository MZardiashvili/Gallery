package com.example.gallery.data;

public class Picture {
    private  int id;
    private String pictureName;
    private String pictureDate;
    private int userId;
    private String userExternalId;

    public String getUserExternalId() {
        return userExternalId;
    }

    public void setUserExternalId(String userExternalId) {
        this.userExternalId = userExternalId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPictureDate() {
        return pictureDate;
    }

    public void setPictureDate(String pictureDate) {
        this.pictureDate = pictureDate;
    }
}