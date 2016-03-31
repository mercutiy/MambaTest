package ru.mamba.test.mambatest.fetcher;

import android.graphics.Bitmap;
import org.json.JSONObject;

public class ImageResponse extends Response {

    Bitmap mPhoto;

    public ImageResponse(JSONObject json, Bitmap photo) {
        super(json);
        mPhoto = photo;
    }

    public Bitmap getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Bitmap photo) {
        mPhoto = photo;
    }
}
