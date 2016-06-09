package ru.mamba.test.mambatest.helper;

import android.app.Activity;

public class ErrorHandler {

    private static ErrorHandler sInstance = new ErrorHandler();

    public static ErrorHandler getInstance() {
        return sInstance;
    }

    private ErrorHandler() {
    }

    public void handle(Activity activity, Throwable exception, String message) {

    }

    public void handle(Activity activity, Throwable exception) {
        handle(activity, exception, null);
    }
}
