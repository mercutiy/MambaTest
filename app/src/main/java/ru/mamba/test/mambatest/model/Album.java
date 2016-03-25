package ru.mamba.test.mambatest.model;

public class Album {

    private String mTitle;

    private String mPhoto;

    public Album(String photo, String title) {
        mPhoto = photo;
        mTitle = title;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public String getTitle() {
        return mTitle;
    }
}
