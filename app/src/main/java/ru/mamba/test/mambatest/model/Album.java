package ru.mamba.test.mambatest.model;

public class Album {

    private int mId;

    private String mTitle;

    private String mPhoto;

    public Album(int id, String title, String photo) {
        mId = id;
        mPhoto = photo;
        mTitle = title;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getId() {
        return mId;
    }
}
