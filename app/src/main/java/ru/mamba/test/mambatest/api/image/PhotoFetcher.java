package ru.mamba.test.mambatest.api.image;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PhotoFetcher<Token> extends HandlerThread {

    private static final String TAG = PhotoFetcher.class.getCanonicalName();

    private static final int MESSAGE_DOWNLOADED = 0;

    Handler mHandler;

    Map<Token, String> requestMap = new HashMap<Token, String>();

    Handler mResponseHandler;

    Listener<Token> mListener;

    public interface Listener<Token> {
        void onPhotoDownloaded(Token token, Bitmap bitmap);
    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }

    public PhotoFetcher(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    public void queueThumbnail(Token token, String url) {
        Log.i(TAG, "Got an URL: " + url);

        requestMap.put(token, url);
        mHandler
            .obtainMessage(MESSAGE_DOWNLOADED, token)
            .sendToTarget();
    }

    @SuppressWarnings("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Token token = (Token)msg.obj;
                Log.i(TAG, "Got a request from " + requestMap.get(token));
                handleRequest(token);
            }
        };
    }

    private void handleRequest(final Token token) {
        try {
            final String url = requestMap.get(token);
            if (url == null) {
                return;
            }

            final Bitmap bitmap = new ImageFetcher().fetchImage(url);

            mResponseHandler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        if (requestMap.get(token) != url) {
                            return;
                        }

                        requestMap.remove(token);
                        mListener.onPhotoDownloaded(token, bitmap);
                    }
                }
            );
        } catch (IOException e) {
            Log.e(TAG, "Error downloading image", e);
        }
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOADED);
        requestMap.clear();
    }
}
