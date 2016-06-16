package ru.mamba.test.mambatest.helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import ru.mamba.test.mambatest.R;

public class ErrorHandler {

    private static ErrorHandler sInstance = new ErrorHandler();

    public static ErrorHandler getInstance() {
        return sInstance;
    }

    private ErrorHandler() {
    }

    public void handle(Activity activity, Throwable exception, String message) {
        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        /*ad.setIcon(R.drawable.ic_action_error);
        ad.setTitle(title);
        ad.setMessage(message);*/
        ad.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad.show();
    }

    public void handle(Activity activity, Throwable exception) {
        handle(activity, exception, null);
    }
}
