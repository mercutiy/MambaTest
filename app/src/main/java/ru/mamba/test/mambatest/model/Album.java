package ru.mamba.test.mambatest.model;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.api.controller.Login;

public class Album extends Model {

    protected final static String F_INT_ID = "id";

    protected final static String F_STR_TITLE = "name";

    protected final static String F_STR_PHOTO = "coverUrl";

    private int mId;

    private String mTitle;

    private String mPhoto;

    private Bitmap mPhotoBitmap;

    public Album(JSONObject json) throws JSONException {
        super(json);
        setId(json.getInt(F_INT_ID));
        setTitle(json.getString(F_STR_TITLE));
        if (!json.isNull(F_STR_PHOTO)) {
            setPhoto(json.getString(F_STR_PHOTO));
        }
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

    public void setId(int id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public Bitmap getPhotoBitmap() {
        return mPhotoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        mPhotoBitmap = photoBitmap;
    }
}
