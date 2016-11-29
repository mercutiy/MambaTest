package ru.mamba.test.mambatest.misc;
import android.graphics.Bitmap;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.mamba.test.mambatest.model.sub.Photo;

public class PhotoFetcher extends HandlerThread {

    private static final String TAG = PhotoFetcher.class.getCanonicalName();

    private static final int MESSAGE_DOWNLOADED = 0;

    Handler mHandler;

    private Map<ImageView, Photo> requestMap = new HashMap<ImageView, Photo>();

    Handler mResponseHandler;

    ImageCacher mCacher;

    public PhotoFetcher(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    public void queueThumbnail(Photo photo, ImageView view) {
        requestMap.put(view, photo);
        mHandler.obtainMessage(MESSAGE_DOWNLOADED, view).sendToTarget();
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new DownloadHandler();
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOADED);
        requestMap.clear();
    }

    private class DownloadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            final ImageView view = (ImageView)msg.obj;
            final Photo photo = requestMap.get(view);
            if (photo == null) {
                return;
            }
            try {
                Bitmap bitmap = new ImageFetcher().fetchImage(photo.getUrl());
                Log.i(TAG, "Got a request from " + photo.getUrl());
                photo.setBitmap(bitmap);
                if (getCacher() != null) {
                    getCacher().put(photo);
                }
            } catch (IOException e) {
                return;
            }
            if (mResponseHandler != null) {
                mResponseHandler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (requestMap.get(view) != photo) {
                                return;
                            }
                            requestMap.remove(view);
                            view.setImageBitmap(photo.getBitmap());
                        }
                    }
                );
            }
        }
    }

    public ImageCacher getCacher() {
        return mCacher;
    }

    public void setCacher(ImageCacher cacher) {
        mCacher = cacher;
    }
}
