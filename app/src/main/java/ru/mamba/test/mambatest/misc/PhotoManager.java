package ru.mamba.test.mambatest.misc;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import ru.mamba.test.mambatest.misc.PhotoFetcher;
import ru.mamba.test.mambatest.model.sub.Photo;

public class PhotoManager {

    public final static String TAG_CONTACT = "contact";

    private String mContentTag;

    private Bitmap mStubPhoto;

    private ImageCacher mCacher;

    private PhotoFetcher mPhotoFetcher;

    public PhotoManager() { }

    public PhotoManager(Bitmap stubPhoto) {
        setStubPhoto(stubPhoto);
    }

    public void destroy() {
        stopQueue();
    }

    public ImageCacher getCacher() {
        return mCacher;
    }

    public void setCacher(ImageCacher cacher) {
        mCacher = cacher;
    }

    public void setContentTag(String contentTag) {
        mContentTag = contentTag;
    }

    public void setStubPhoto(Bitmap stubPhoto) {
        mStubPhoto = stubPhoto;
    }

    private PhotoFetcher getQueue() {
        if (mPhotoFetcher == null) {
            mPhotoFetcher = new PhotoFetcher(new Handler());
            mPhotoFetcher.start();
            mPhotoFetcher.getLooper();
            mPhotoFetcher.setCacher(getCacher());
        }

        return mPhotoFetcher;
    }

    private void stopQueue() {
        if (mPhotoFetcher != null) {
            mPhotoFetcher.quit();
        }
    }

    public boolean placePhoto(Photo photo, ImageView imageView) {
        if (photo.getBitmap() != null) {
            imageView.setImageBitmap(photo.getBitmap());
            return true;
        }
        if (getCacher() != null && getCacher().get(photo)) {
            imageView.setImageBitmap(photo.getBitmap());
            return true;
        }

        placeStub(imageView);

        getQueue().queueThumbnail(photo, imageView);

        return true;
    }

    private boolean placeStub(ImageView imageView) {
        if (mStubPhoto == null) {
            return false;
        }
        imageView.setImageBitmap(mStubPhoto);
        return true;
    }
}
