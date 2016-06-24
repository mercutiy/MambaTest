package ru.mamba.test.mambatest.helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

import ru.mamba.test.mambatest.R;
import ru.mamba.test.mambatest.api.exception.ApiException;

public class ErrorHandler {

    private static ErrorHandler sInstance = new ErrorHandler();

    public static ErrorHandler getInstance() {
        return sInstance;
    }

    private ErrorHandler() {
    }

    public void handle(Activity activity, Throwable exception, String message) {
        Log.e("", message != null ? message : exception.getMessage(), exception);
        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        int iconId;
        int titleId;
        int messageId;
        if (exception instanceof IOException) {
            iconId = R.drawable.ic_action_error;
            titleId = R.string.error_io;
            messageId = R.string.error_message_io;
        } else if (exception instanceof JSONException) {
            iconId = R.drawable.ic_action_error;
            titleId = R.string.error_json;
            messageId = R.string.error_message_json;
        } else if (exception instanceof ApiException) {
            iconId = R.drawable.ic_action_error;
            titleId = R.string.error_api;
            messageId = R.string.error_message_api;
        } else {
            iconId = R.drawable.ic_action_error;
            titleId = R.string.error_common;
            messageId = R.string.error_message_common;
        }
        ad.setIcon(iconId);
        ad.setTitle(titleId);
        ad.setMessage(messageId);
        ad.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // todo implement try again button
            }
        });
        ad.show();
    }

    public void handle(Activity activity, Throwable exception) {
        handle(activity, exception, null);
    }
}
