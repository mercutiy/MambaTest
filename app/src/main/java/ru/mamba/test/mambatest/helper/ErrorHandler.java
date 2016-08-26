package ru.mamba.test.mambatest.helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import ru.mamba.test.mambatest.R;
import ru.mamba.test.mambatest.api.Fetcher;
import ru.mamba.test.mambatest.api.exception.ApiException;
import ru.mamba.test.mambatest.api.exception.ErrorCodeException;

public class ErrorHandler {

    private static ErrorHandler sInstance = new ErrorHandler();

    public static ErrorHandler getInstance() {
        return sInstance;
    }

    private ErrorHandler() {
    }

    private String mMessage;

    private Throwable mException;

    private Activity mActivity;

    private Fetcher mFetcher;

    public void handle(Fetcher fetcher, Throwable exception, String message) {
        setFetcher(fetcher);
        setActivity(fetcher.getActivity());
        setException(exception);
        setMessage(message);
        handle();
    }

    public void handle(Fetcher fetcher, Throwable exception) {
        setFetcher(fetcher);
        setActivity(fetcher.getActivity());
        setException(exception);
        setMessage(exception.getMessage());
        handle();
    }

    public void handle(Activity activity, Throwable exception) {
        setActivity(activity);
        setException(exception);
        setMessage(exception.getMessage());
        handle();
    }

    public void handle(Activity activity, Throwable exception, String message) {
        setActivity(activity);
        setException(exception);
        setMessage(message);
        handle();
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setException(Throwable exception) {
        mException = exception;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public void setFetcher(Fetcher fetcher) {
        mFetcher = fetcher;
    }

    private void handle() {
        Log.e("", mMessage, mException);
        AlertDialog.Builder ad = new AlertDialog.Builder(mActivity);
        int iconId;
        int titleId;
        int messageId;
        if (mException instanceof IOException) {
            iconId = R.drawable.ic_action_error;
            titleId = R.string.error_io;
            messageId = R.string.error_message_io;
        } else if (mException instanceof JSONException) {
            iconId = R.drawable.ic_action_error;
            titleId = R.string.error_json;
            messageId = R.string.error_message_json;
        } else if (mException instanceof ApiException) {
            iconId = R.drawable.ic_action_error;
            titleId = R.string.error_api;
            messageId = R.string.error_message_api;
        } else if (mException instanceof ErrorCodeException) {
            int errorCode = ((ErrorCodeException) mException).getErrorCode();
            switch (errorCode) {
                case ErrorCodeException.EC_USER_NOT_EXISTS:
                    Toast.makeText(mActivity, R.string.error_code_user_not_exists, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(mActivity, R.string.error_code_default_error, Toast.LENGTH_LONG).show();
                    break;
            }
            mActivity.finish();
            return;
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
                if (mFetcher != null) {
                    mFetcher.reFetch();
                }
            }
        });
        ad.show();
    }
}
