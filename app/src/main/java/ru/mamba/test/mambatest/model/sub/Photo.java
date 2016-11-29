package ru.mamba.test.mambatest.model.sub;

import android.graphics.Bitmap;
import android.util.Patterns;

public class Photo {

    private String mUrl;

    private Bitmap mPhoto;

    public Photo(String url) {
        setUrl(url);
    }

    public Photo(String url, Bitmap photo) {
        setUrl(url);
        setBitmap(photo);
    }

    public void setUrl(String url) {
        if (url == null || !Patterns.WEB_URL.matcher(url).matches()) {
            mUrl = null;
        } else {
            mUrl = url;
        }
    }

    public void setBitmap(Bitmap photo) {
        mPhoto = photo;
    }

    public String getUrl() {
        return mUrl;
    }

    public Bitmap getBitmap() {
        return mPhoto;
    }
}
