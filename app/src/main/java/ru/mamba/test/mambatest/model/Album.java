package ru.mamba.test.mambatest.model;

import android.graphics.Bitmap;

public class Album {

    private int mId;

    private String mTitle;

    private String mPhoto;

    private Bitmap mPhotoBitmap;

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

    public Bitmap getPhotoBitmap() {
        return mPhotoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        mPhotoBitmap = photoBitmap;
    }
}
